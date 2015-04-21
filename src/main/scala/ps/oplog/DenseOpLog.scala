package ps.oplog

import ps.oplog.RowOpLogType.RowOpLogType

import scala.reflect.ClassTag


class DenseOpLog[T : Numeric : ClassTag](
    val capacity: Int,
    val rowCapacity: Int,
    val rowOpLogType: RowOpLogType) extends AbstractOpLog[T] {
  private val rows: Array[AbstractRowOpLog[T]] = Array.fill[AbstractRowOpLog[T]](capacity)(null)

  def inc(rowId: Int, colId: Int, delta: T): Int = {
    getOrInsertOpLog(rowId).updateCol(colId, delta)
    0
  }

  def batchInc(rowId: Int, colIds: Array[Int], deltas: Array[T]): Int = {
    colIds.zip(deltas).foreach { t =>
      getOrInsertOpLog(rowId).updateCol(t._1, t._2)
    }
    0
  }

  def getOrInsertOpLog(rowId: Int): AbstractRowOpLog[T] = {
    if (rows(rowId) == null) {
      rows(rowId) = AbstractRowOpLog[T](rowOpLogType, rowCapacity)
    }
    rows(rowId)
  }
}
