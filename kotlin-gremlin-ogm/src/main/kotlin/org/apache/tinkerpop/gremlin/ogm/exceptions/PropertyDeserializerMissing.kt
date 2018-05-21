package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class PropertyDeserializerMissing(
        obj: Any,
        kClass: KClass<*>
) : ClientException(
        description = "You need to tell the library how to deserialize property value: $obj from the graph. " +
                "Where you create your GraphMapper instance, please register a PropertyBiMapper as a scalar vertexMapper " +
                "for class ${kClass::class}."
)