package ps.table

import ps.oplog.RowOpLogType.RowOpLogType
import ps.table.RowType.RowType


case class TableInfo(tableStaleness: Int, rowType: RowType, rowCapacity: Int, rowOpLogType: RowOpLogType)
