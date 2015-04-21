package ps.common

object ThreadContext {
  val clock = new ThreadLocal[Int]
}
