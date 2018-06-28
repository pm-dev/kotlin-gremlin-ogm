package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * Registers a parameter or property as a value that should be mapped to/from a property of a vertex.
 * The class must be annotated with @Element and registered as a vertex or edge with a GraphMapper.
 * Without this annotation, a property will be transient (not persisted to the graph) and must be nullable.
 *
 * If the annotated property is declared separate from its constructor param, its constructor param must
 * have the same name, or also have this annotation with the same key parameter.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Inherited
annotation class Property(

        /**
         * The key of the property as stored to the graph. We require clients to specify an explicit
         * key (instead of using the property name) to guard against refactoring situations
         * where the property name is changed and this annotation is not updated to keep the original key.
         */
        val key: String
)
