package org.nekosoft.shlink.sec.delegation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Aspect
@Component
public class RunAsDelegationAspect {

    @Around("@annotation(runAs)")
    public Object delegate(ProceedingJoinPoint joinPoint, org.nekosoft.shlink.sec.delegation.annotation.RunAs runAs) throws Throwable {

        Function<String[], RunAs> executor;
        if (runAs.allowAnonymous()) {
            executor = RunAs::anonymousWithRoles;
        } else {
            executor = RunAs::userWithRoles;
        }

        Object result;
        try (RunAs ignored = executor.apply(runAs.roles())) {
            result = joinPoint.proceed();
        }
        return result;
    }

}
