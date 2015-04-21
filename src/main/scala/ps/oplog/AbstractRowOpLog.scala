package ps.oplog

import ps.oplog.RowOpLogType.RowOpLogType

import scala.reflect.{ClassTag, ClassManifest}

trait AbstractRowOpLog[T] {
  def reset : Unit

  def getCol(colId: Int): T

  /**
   * Create Column if necessary
   */
  def updateCol(colId: Int, delta: T): Unit

  def iterator: Iterator[(Int, T)]

  def getSize: Int

  def clearZerosAndGetNoneZeroSize: Int

  // todo: the following functions haven't be considered because don't know how to implement it in Scala
  // 1. overwriteWithDenseUpdate: in dense row oplog, can use memcpy in C++
  // 2. serialize and deserialize, use Java serializer right now, can take Spark SQL as a reference
  // 3. more op log types if necessary
}


object AbstractRowOpLog {
  def apply[T: Numeric : ClassTag](
    rowOpLogType: RowOpLogType,
    rowCapacity: Int)
  : AbstractRowOpLog[T] = {
    rowOpLogType match {
      case RowOpLogType.DenseRowOpLogType =>
        new DenseRowOpLog[T](rowCapacity)
      case RowOpLogType.SparseRowOpLogType =>
        new SparseRowOpLog[T]
      case _ =>
        throw new IllegalArgumentException("Unknown row oplog type")
    }
  }
}