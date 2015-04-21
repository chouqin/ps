package ps.table

import scala.reflect.ClassTag

class DenseRow[T](val capacity: Int)(implicit n: Numeric[T], m : ClassTag[T])
    extends AbstractRow[T]{
  import n._
  private val cols: Array[T] = Array.fill[T](capacity)(zero)

  def reset: Unit = {
    for (i <- 0 until capacity) {
      cols(i) = zero
    }
  }

  def applyInc(colId: Int, delta: T): Unit = {
    cols(colId) = cols(colId) + delta
  }

  def applyBatchInc(colIds: Array[Int], deltas: Array[T]): Unit = {
    colIds.zip(deltas).foreach { t =>
      cols(t._1) = cols(t._1) + t._2
    }
  }

  def apply(colId: Int): T = cols(colId)
}
