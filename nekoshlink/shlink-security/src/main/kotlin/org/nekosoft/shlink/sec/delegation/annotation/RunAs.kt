package org.nekosoft.shlink.sec.delegation.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RunAs(
    vararg val roles: String,
    val allowAnonymous: Boolean = false,
)
