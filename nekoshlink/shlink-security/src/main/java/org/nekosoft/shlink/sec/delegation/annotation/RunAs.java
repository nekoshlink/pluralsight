package org.nekosoft.shlink.sec.delegation.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RunAs {
    String[] roles();
    boolean allowAnonymous() default false;
}