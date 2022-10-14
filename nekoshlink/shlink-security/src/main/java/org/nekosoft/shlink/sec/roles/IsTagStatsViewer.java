package org.nekosoft.shlink.sec.roles;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('Tags') and hasRole('Viewer') and (!#options.withStats or hasRole('Stats'))")
public @interface IsTagStatsViewer {
}
