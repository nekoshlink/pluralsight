package org.nekosoft.shlink.rest.controller

import mu.KotlinLogging
import org.nekosoft.shlink.entity.Visit
import org.nekosoft.shlink.entity.support.VisitSource
import org.nekosoft.shlink.service.VisitDataEnricher
import org.nekosoft.shlink.service.exception.*
import org.nekosoft.shlink.vo.ResolveMeta
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

private val logger = KotlinLogging.logger {}

abstract class AbstractResolveController<R> {

    abstract fun getSource(): VisitSource
    abstract fun getResponse(meta: ResolveMeta, enricher: VisitDataEnricher, request: HttpServletRequest): ResponseEntity<R>

    // no need for pre-authorize here as the security filter requires no authorization for this controller
    @GetMapping
    fun resolveShortUrl(@PathVariable("shortCode") shortCode: String, request: HttpServletRequest): ResponseEntity<R> {
        return commonResolver(shortCode, null, request)
    }

    // no need for pre-authorize here as the security filter requires no authorization for this controller
    @GetMapping("{*pathTrail}")
    fun resolveShortUrl(@PathVariable("shortCode") shortCode: String, @PathVariable("pathTrail") pathTrail: String, request: HttpServletRequest): ResponseEntity<R> {
        return commonResolver(shortCode, pathTrail, request)
    }

    protected open fun commonResolver(shortCode: String, pathTrail: String?, request: HttpServletRequest): ResponseEntity<R> {

        try {

            val meta = ResolveMeta(
                shortCode = shortCode,
                domain = request.getHeader(HttpHeaders.HOST),
                password = request.getParameter(SHLINK_PASSWORD_PARAMETER),
                pathTrail = pathTrail,
                queryParams = request.parameterMap.filter { it.key != SHLINK_PASSWORD_PARAMETER },
            )

            val enricher: VisitDataEnricher = {
                Visit(
                    source = getSource(),
                    referrer = request.getHeader(HttpHeaders.REFERER),
                    remoteAddr = request.remoteAddr,
                    pathTrail = meta.pathTrail,
                    queryString = request.queryString,
                    userAgent = request.getHeader(HttpHeaders.USER_AGENT),
                )
            }

            return getResponse(meta, enricher, request)

        } catch (e: NekoShlinkResolutionException) {

            when (e) {
                is ProtectedShortUrlResolutionException, is ShortUrlHasExpiredException, is ShortUrlNotEnabledYetException, is MaxVisitLimitReachedException ->
                    throw ResponseStatusException(HttpStatus.FORBIDDEN, e.message, e)
                is MissingShortUrlException, is PathTrailNotAllowedException, is QueryParamsNotAllowedException ->
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
            }

        } catch (e: AccessDeniedException) {

            throw e

        } catch (e: Exception) {

            logger.error { e }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)

        }

    }

    companion object {
        const val SHLINK_PASSWORD_PARAMETER = "__nekoshlink_password"
    }

}
