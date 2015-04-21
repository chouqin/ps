package ps.table

import ps.table.RowType.RowType

import scala.reflect.ClassTag

trait AbstractRow[T] {
  def reset: Unit

  def applyInc(colId: Int, delta: T): Unit

  def applyBatchInc(colIds: Array[Int], deltas: Array[T]): Unit

  // TODO:
  // 1. importance, related to SSPPush
}

object AbstractRow {
  def apply[T : ClassTag : Numeric](rowType: RowType, capacity: Int): AbstractRow[T] = {
    rowType match {
      case RowType.DenseRowType => new DenseRow[T](capacity)
      case RowType.SparseRowType => new SparseRow[T]()
      case _ => throw new IllegalArgumentException("Unknown row type")
    }
  }
}
