package ps.oplog

import scala.reflect.ClassTag

class DenseRowOpLog[T](
    val capacity: Int
    )(implicit n: Numeric[T], m: ClassTag[T]) extends AbstractRowOpLog[T] {
  private val cols = Array.fill[T](capacity)(n.zero)

  import n._

  def reset: Unit = {
    for (i <- 0 until capacity) {
      cols(i) = zero
    }
  }

  def getCol(colId: Int): T = {
    cols(colId)
  }

  def updateCol(colId: Int, delta: T): Unit = {
    cols(colId) = cols(colId) + delta
  }

  def iterator: Iterator[(Int, T)] = {
    cols.zipWithIndex.map(t => (t._2, t._1)).iterator.
      filter(t => zero != t._2)
  }

  def getSize: Int = capacity

  def clearZerosAndGetNoneZeroSize: Int = {
    cols.count(_ != zero)
  }
}
