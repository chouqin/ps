package ps.oplog

import scala.collection.mutable

class SparseRowOpLog[T](implicit n: Numeric[T])
    extends AbstractRowOpLog[T] {
  import n._
  private val cols = mutable.HashMap.empty[Int, T]

  def reset : Unit = {
    cols.clear()
  }

  def getCol(colId: Int): T = {
    cols.getOrElse(colId, zero)
  }

  def updateCol(colId: Int, delta: T): Unit = {
    if (!cols.isDefinedAt(colId)) {
      cols(colId) = delta
    } else {
      cols(colId) = cols(colId) + delta
    }
  }

  def iterator: Iterator[(Int, T)] = {
    cols.iterator
  }

  def getSize: Int = cols.size

  def clearZerosAndGetNoneZeroSize: Int = {
    cols.retain((_, v) => v != zero)
    cols.size
  }
}

object SparseRowOpLog {
  def main(args: Array[String]): Unit = {
    val l = new SparseRowOpLog[Double]
    l.updateCol(1, 1.5)
    l.updateCol(1, 1.5)
    println(l.getCol(1))
  }
}
