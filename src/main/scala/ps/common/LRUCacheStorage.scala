package ps.common

import scala.collection.mutable

class LRUCacheStorage[T](val capacity: Int) {
  class Node(val key: Int, var next: Node = null, var prev: Node = null)

  private val kvs = mutable.HashMap.empty[Int, (Node, T)]
  private var currentSize = 0
  private var head: Node = null
  private var tail: Node = null

  def get(key: Int): Option[T] = {
    if (kvs.isDefinedAt(key)) {
      reference(key)
      Some(kvs(key)._2)
    } else {
      None
    }
  }

  def set(key: Int, value: T): Unit = {
    if (kvs.isDefinedAt(key)) {
      kvs(key) = (kvs(key)._1, value)
      reference(key)
      return
    }

    if (currentSize == 0) {
      tail = new Node(key)
      head = tail
      kvs(key) = (tail, value)
      currentSize += 1
      return
    }

    val current = new Node(key)
    tail.next = current
    current.prev = tail
    tail = current
    kvs(key) = (tail, value)

    if (currentSize < capacity) {
      currentSize += 1
    } else {
      kvs.remove(head.key)
      head = head.next
    }
  }


  private def reference(key: Int): Unit = {
    val current = kvs(key)._1

    if (current == tail) {
      return
    }

    if (current == head) {
      head = head.next
    } else {
      current.prev.next = current.next
      current.next.prev = current.prev
    }

    current.prev = tail
    current.next = null
    tail.next = current
    tail = current
  }
}

object LRUCacheStorageTest {
  def main(args: Array[String]): Unit = {
    val cache = new LRUCacheStorage[Int](4)

    cache.set(1, 1)
    printf("%d %d %d %d %d \n", cache.get(1).getOrElse(-1), cache.get(2).getOrElse(-1),
      cache.get(4).getOrElse(-1), cache.get(4).getOrElse(-1), cache.get(5).getOrElse(-1))

    cache.set(2, 2)
    cache.set(3, 3)
    cache.set(4, 4)
    cache.set(5, 5)
    printf("%d %d %d %d %d \n", cache.get(1).getOrElse(-1), cache.get(2).getOrElse(-1),
      cache.get(3).getOrElse(-1), cache.get(4).getOrElse(-1), cache.get(5).getOrElse(-1))

    cache.set(1, 6)
    printf("%d %d %d %d %d \n", cache.get(1).getOrElse(-1), cache.get(2).getOrElse(-1),
      cache.get(3).getOrElse(-1), cache.get(4).getOrElse(-1), cache.get(5).getOrElse(-1))

  }
}
