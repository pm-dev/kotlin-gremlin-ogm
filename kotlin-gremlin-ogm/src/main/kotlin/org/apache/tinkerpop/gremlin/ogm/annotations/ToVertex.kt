package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * An annotation used to mark the constructor parameter for an edge's to-vertex
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.VALUE_PARAMETER])
@Inherited
annotation class ToVertex
