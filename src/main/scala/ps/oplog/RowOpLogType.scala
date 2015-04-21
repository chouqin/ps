package ps.oplog

object RowOpLogType extends Enumeration {
  type RowOpLogType = Value
  val DenseRowOpLogType, SparseRowOpLogType = Value
}
