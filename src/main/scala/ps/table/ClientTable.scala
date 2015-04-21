package ps.table

import ps.common.{ThreadContext, LRUCacheStorage}
import ps.oplog.OpLogType.OpLogType
import ps.oplog.RowOpLogType.RowOpLogType
import ps.oplog.{AbstractOpLog, OpLogType, RowOpLogType}

import scala.reflect.ClassTag

case class ClientTableConfig(
  storageCapacity: Int = 0,
  opLogCapacity: Int = 0,
  opLogType: OpLogType = OpLogType.SparseOpLogType,
  rowOpLogType: RowOpLogType = RowOpLogType.DenseRowOpLogType,
  rowCapacity: Int = 0,
  tableInfo: TableInfo
  )

// TODO: only support SSP consistency right now,
// move the consistency controller if necessary
class ClientTable[T : Numeric : ClassTag](
  val tableId: Int,
  val config: ClientTableConfig
  ) {

  private val storage = new LRUCacheStorage[ClientRow[T]](config.storageCapacity)
  private val opLog = AbstractOpLog(
    config.opLogType,
    config.opLogCapacity,
    config.rowOpLogType,
    config.rowCapacity
  )
  private val staleness = config.tableInfo.tableStaleness

  def get(rowId: Int): ClientRow[T] = {
    val stalestClock = math.max(0, ThreadContext.clock.get() - staleness)
    var clientRow = storage.get(rowId)

    if (!clientRow.isEmpty) {
      val clock = clientRow.get.getClock()
      // check staleness
      if (clock >= stalestClock) {
        return clientRow.get
      }
    }

    var numFetches = 0
    while (numFetches != 3 && clientRow.isEmpty) {
      // request row
      clientRow = storage.get(rowId)
      numFetches += 1
    }
    assert(clientRow.isDefined, "can't get client row after 3 fetches")
    assert(clientRow.get.getClock >= stalestClock,
      "client row's clock must greater than stalestClock")

    clientRow.get
  }

  def inc(rowId: Int, colId: Int, delta: T): Unit = {
    opLog.inc(rowId, colId, delta)
    val clientRow = storage.get(rowId)
    if (clientRow.nonEmpty) {
      clientRow.get.abstractRow.applyInc(colId, delta)
    }
  }

  def batchInc(rowId: Int, colIds: Array[Int], deltas: Array[T]): Unit = {
    opLog.batchInc(rowId, colIds, deltas)
    val clientRow = storage.get(rowId)
    if (clientRow.nonEmpty) {
      clientRow.get.abstractRow.applyBatchInc(colIds, deltas)
    }
  }

  def clock(): Unit = {
    // send oplog

    // set clock
    val clock = ThreadContext.clock.get()
    ThreadContext.clock.set(clock + 1)
  }

  // TODO: not supported function
  // denseBatchInc: use System.arraycopy?
}
