package org.nekosoft.shlink.entity.support

data class TagStats(
    val id: Long,
    var name: String,
    var description: String? = null,
    var shortUrlCount: Long = -1,
    var visitCount: Long = -1,
)
