package org.apache.tinkerpop.gremlin.ogm.annotations

import java.lang.annotation.Inherited

/**
 * Registers a parameter or property as a value that should be mapped to/from a property of a vertex.
 * The class must be annotation with @Vertex and registered as a vertex with a GraphMapper.
 * Without this annotation, a property will be transient (not persisted to the graph) and must be nullable.
 *
 * **For each property annotated with @Property, there must also be a primary constructor parameter annotated
 * with @Property that has the same key
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Inherited
annotation class Property(

        /**
         * The key of the property as stored in the graph. We require clients to specify an explicit
         * key (instead of using the property name) to guard against refactoring situations
         * where the property name is changed and this annotation is not updated to keep the original key.
         */
        val key: String
)
