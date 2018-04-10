package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * Represents the primary unique constraint used to reference the id of a Vertex.
 * This annotation MUST be present on a nullable parameter AND property of classes annotated with @Vertex.
 * The property annotated with @ID may not be set manually, as it is generated automatically by the graph
 * implementation.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER])
@Inherited
annotation class ID
