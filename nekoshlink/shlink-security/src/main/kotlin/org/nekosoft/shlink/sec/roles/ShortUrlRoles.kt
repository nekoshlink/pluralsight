package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls') and hasRole('Viewer')")
annotation class IsShortUrlViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls') and hasRole('Viewer') and (!#options.withStats or hasRole('Stats'))")
annotation class IsShortUrlStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls') and hasAnyRole('Editor')")
annotation class IsShortUrlEditor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls') and hasRole('Admin')")
annotation class IsShortUrlAdmin
