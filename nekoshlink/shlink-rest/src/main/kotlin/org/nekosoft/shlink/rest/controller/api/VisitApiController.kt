package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.VisitDataAccess
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.rest.ShlinkRestApiServer.Companion.VERSION_STRING
import org.nekosoft.shlink.vo.rest.PaginationData
import org.nekosoft.shlink.vo.rest.PaginationData.Companion.paginationToPageable
import org.nekosoft.shlink.vo.rest.RestResult
import org.nekosoft.shlink.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v$VERSION_STRING/visits") // this short mapping guarantees it cannot be a short code (min five characters)
class VisitApiController(
    private val visits: VisitDataAccess,
) {

    @PreAuthorize("hasRole('Visits') and hasRole('Viewer')")
    @GetMapping
    fun visits(options: VisitListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<Visit>> {
        val results = visits.getVisits(options, paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

}
