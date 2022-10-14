package org.nekosoft.shlink.oauth2server.user.rest

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

data class PaginationData(
    var currentPage: Int,
    var pageSize: Int,
    var currentPageSize: Int,
    var totalPages: Int,
    var totalSize: Long,
) {

    companion object {

        fun fromPage(page: Page<*>): PaginationData = PaginationData(
            currentPage = page.number + 1,
            pageSize = page.size,
            currentPageSize = page.numberOfElements,
            totalPages = page.totalPages,
            totalSize = page.totalElements,
        )

        fun paginationToPageable(pagination: PaginationOptions): Pageable = if (pagination.page == null) {
            Pageable.unpaged()
        } else {
            PageRequest.of(pagination.page!!, pagination.pageSize ?: 100)
        }

    }

}
