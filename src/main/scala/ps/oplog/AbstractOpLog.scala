package ps.oplog

import ps.oplog.OpLogType.OpLogType
import ps.oplog.RowOpLogType._

import scala.reflect.ClassTag

trait AbstractOpLog[T] {
  def inc(rowId: Int, colId: Int, delta: T): Int
  def batchInc(rowId: Int, colIds: Array[Int], deltas: Array[T]): Int

  def getOrInsertOpLog(rowId: Int): AbstractRowOpLog[T]

  // todo:
  // 1. dense batch inc
  // 2. append only buffer
  // 3. serialize and deserialize
}

object AbstractOpLog {
  def apply[T : Numeric : ClassTag](
    opLogType: OpLogType,
    capacity: Int,
    rowOpLogType: RowOpLogType,
    rowCapacity: Int): AbstractOpLog[T] = {
    opLogType match {
      case OpLogType.DenseOpLogType =>
        new DenseOpLog[T](capacity, rowCapacity, rowOpLogType)
      case OpLogType.SparseOpLogType =>
        new SparseOpLog[T](rowCapacity, rowOpLogType)
      case _ =>
        throw new IllegalArgumentException("Unknown op log type")
    }
  }
}
