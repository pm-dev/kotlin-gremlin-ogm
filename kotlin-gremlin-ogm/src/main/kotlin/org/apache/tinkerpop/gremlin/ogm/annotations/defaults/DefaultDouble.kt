package org.apache.tinkerpop.gremlin.ogm.annotations.defaults

import java.lang.annotation.Inherited

/**
 * Annotation that, when used with the @Property annotation, specifies a value to use when
 * the graph does not have a value for the given parameter. This can be useful as an alternative to a migration
 * when adding a non-nullable property to an Element.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
annotation class DefaultDouble(

        /**
         * The value to use when deserializing this property from the graph, but the graph has no value for this
         * property.
         */
        val value: Double)
