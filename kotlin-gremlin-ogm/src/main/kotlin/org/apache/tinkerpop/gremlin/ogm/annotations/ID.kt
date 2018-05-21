package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * Represents the primary unique constraint used to reference the id of a graph element (Vertex or Edge).
 * This annotation must be present on a nullable constructor parameter and property for classes annotated with @Vertex
 * or @Edge. We require annotation on @Vertex classes because the library needs to
 * do indexed look-ups to prevent duplicate or conflicting edges.
 * The property annotated with @ID may not be set manually, as it is generated automatically by the graph
 * implementation.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER])
@Inherited
annotation class ID
