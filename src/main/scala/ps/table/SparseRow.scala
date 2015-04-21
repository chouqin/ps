package ps.table

import scala.collection.mutable
import scala.reflect.ClassTag

class SparseRow[T](implicit n: Numeric[T],  m : ClassTag[T])
  extends AbstractRow[T]{
  import n._
  private var cols = mutable.HashMap.empty[Int, T]

  def reset: Unit = {
    cols = mutable.HashMap.empty[Int, T]
  }

  def applyInc(colId: Int, delta: T): Unit = {
    if (cols.isDefinedAt(colId)) {
      cols(colId) = cols(colId) + delta
    } else {
      cols(colId) = delta
    }
  }

  def applyBatchInc(colIds: Array[Int], deltas: Array[T]): Unit = {
    colIds.zip(deltas).foreach { t =>
      applyInc(t._1, t._2)
    }
  }

  def apply(colId: Int): T = cols(colId)
}
