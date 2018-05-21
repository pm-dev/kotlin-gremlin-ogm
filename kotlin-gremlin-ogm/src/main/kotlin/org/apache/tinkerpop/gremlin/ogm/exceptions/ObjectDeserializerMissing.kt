package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ObjectDeserializerMissing(
        obj: Map<*, *>,
        kClass: KClass<*>
) : ClientException(
        description = "You need to tell the library how to deserialize object: $obj from the graph. " +
                "Where you create your GraphMapper instance, you'll need to register $kClass as a nested object."
)
