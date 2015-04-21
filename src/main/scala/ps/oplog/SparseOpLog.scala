package ps.oplog

import ps.oplog.RowOpLogType.RowOpLogType

import scala.collection.mutable
import scala.reflect.ClassTag

class SparseOpLog [T : Numeric : ClassTag](
  val rowCapacity: Int,
  val rowOpLogType: RowOpLogType) extends AbstractOpLog[T] {
  private val rows = mutable.HashMap.empty[Int, AbstractRowOpLog[T]]

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
    if (!rows.isDefinedAt(rowId)) {
      rows(rowId) = AbstractRowOpLog[T](rowOpLogType, rowCapacity)
    }
    rows(rowId)
  }
}
