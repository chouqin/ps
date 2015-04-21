package ps.common

import scala.collection.mutable


class VectorClock {
  private var minClock = -1
  private val id2clock = mutable.HashMap.empty[Int, Int]

  def this(ids: Array[Int]) = {
    this()
    minClock = 0
    ids.foreach { id =>
      id2clock(id) = 0
    }
  }

  def addClock(id: Int, clock: Int): Unit = {
    id2clock(id) = 0
    if (minClock == -1 || clock < minClock) {
      minClock = clock
    }
  }

  def tick(id: Int): Int = {
    id2clock(id) = id2clock(id) + 1
    if (isUniqueMin(id)) {
      minClock += 1
      minClock
    } else {
      0
    }
  }

  def tickUntil(id: Int, clock: Int): Int = {
    val currentClock = getClock(id)
    val numTicks = clock - currentClock
    var newClock = 0
    for (i <- 0 until numTicks) {
      val clockChanged = tick(id)
      if (clockChanged != 0) {
        newClock = clockChanged
      }
    }
    newClock
  }

  def getClock(id: Int): Int = {
    require(id2clock.isDefinedAt(id), "ps.common.VectorClock doesn't contain key: " + id)
    id2clock(id)
  }

  private def isUniqueMin(id: Int): Boolean = {
    if (id2clock(id) != minClock) {
      return false
    }

    id2clock.count(_._2 == minClock) == 1
  }
}
