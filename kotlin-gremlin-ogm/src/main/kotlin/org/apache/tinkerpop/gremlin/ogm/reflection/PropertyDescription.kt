package org.apache.tinkerpop.gremlin.ogm.reflection

import org.apache.tinkerpop.gremlin.ogm.exceptions.PropertyUnsupported
import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import org.apache.tinkerpop.gremlin.ogm.mappers.SerializedProperty
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1

/**
 * Describes a property on an object representing a vertex to a graph.
 */
internal data class PropertyDescription<RECEIVER, TYPE>(

        /**
         * The primary constructor parameter on RECEIVER that sets this property.
         */
        val parameter: KParameter,

        /**
         * The property which can be found on RECEIVER.
         */
        val property: KProperty1<RECEIVER, TYPE>,

        /**
         * The a custom serializer to map this property to/from its SerializedProperty form
         */
        val mapper: PropertyBiMapper<Any, SerializedProperty>?
) {
    /**
     * The concrete KClass type for this property if this property represents such a type. If this property description
     * does not have a KClass, it must have a non-null vertexMapper.
     */
    val kClass = property.returnType.classifier as? KClass<*> ?: throw PropertyUnsupported(parameter)
}
