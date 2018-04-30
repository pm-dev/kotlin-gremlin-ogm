package org.apache.tinkerpop.gremlin.ogm.annotations

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * Annotation to override the default mapping behavior for a vertex property. Using @Mapper allows
 * clients to specify a custom vertexMapper for a vertex property annotated with @param:Property.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Inherited
annotation class Mapper(

        /**
         * The class to use for mapping a property to/from the graph. Must have a constructor with
         * no arguments or where getV arguments are optional.
         */
        val kClass: KClass<out PropertyBiMapper<out Any, out SerializedProperty>>
)
