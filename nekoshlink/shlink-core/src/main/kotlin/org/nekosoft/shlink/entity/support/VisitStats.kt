package org.nekosoft.shlink.entity.support

data class VisitStats(
    val totalCount: Long,
    val orphanCount: Long,
    val successCount: Long,
    val forbiddenCount: Long,
    val outOfTimeCount: Long,
    val overLimitCount: Long,
    val errorCount: Long,
)
