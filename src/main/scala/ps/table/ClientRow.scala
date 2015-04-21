package ps.table

class ClientRow[T](val abstractRow: AbstractRow[T]) {
  private var clock = 0

  def setClock(clk: Int): Unit = {
    clock = clk
  }

  def getClock(): Int = clock
}
