package org.nekosoft.shlink.entity.support;

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class AuditInfo(
    @Column(name = "created_date")
    @CreatedDate
    var createdDate: Instant? = null,

    @Column(name = "modified_date")
    @LastModifiedDate
    var modifiedDate: Instant? = null,

    @Column(name = "created_by")
    @CreatedBy
    var createdBy: String? = null,

    @Column(name = "modified_by")
    @LastModifiedBy
    var modifiedBy: String? = null,
)
