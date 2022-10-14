package org.nekosoft.shlink.sec.delegation

import org.nekosoft.shlink.sec.delegation.annotation.RunAs

import org.nekosoft.shlink.sec.delegation.RunAs as RunAsBlock

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RunAsDelegationAspect {

    @Around("@annotation(runAs)")
    fun delegate(joinPoint: ProceedingJoinPoint, runAs: RunAs): Any? {

        val executor = if (runAs.allowAnonymous) {
            RunAsBlock.Companion::anonymousWithRoles
        } else {
            RunAsBlock.Companion::userWithRoles
        }

        val result = executor(runAs.roles). use {
            joinPoint.proceed()
        }

        return result
    }

}
