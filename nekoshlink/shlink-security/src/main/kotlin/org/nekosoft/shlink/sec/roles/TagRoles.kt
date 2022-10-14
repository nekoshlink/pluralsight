package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags') and hasRole('Viewer')")
annotation class IsTagViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags') and hasRole('Viewer') and (!#options.withStats or hasRole('Stats'))")
annotation class IsTagStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags') and hasAnyRole('Editor')")
annotation class IsTagEditor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags') and hasRole('Admin')")
annotation class IsTagAdmin
