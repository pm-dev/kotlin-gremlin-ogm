package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * An annotation used to mark the property and constructor parameter for an edge's out-vertex.
 * For relationships that are symmetric, it doesn't matter which property/param is marked as the to-vertex and
 * and which one is marked as the out-vertex.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.VALUE_PARAMETER])
@Inherited
annotation class FromVertex
