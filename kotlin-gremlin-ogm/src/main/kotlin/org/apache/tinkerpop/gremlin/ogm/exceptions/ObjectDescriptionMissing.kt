package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class ObjectDescriptionMissing(
        kClass: KClass<*>
) : ClientException(
        description = "You need to tell the library how to serialize/deserialize an object to the graph. " +
                "Where you create your GraphMapper instance, please register $kClass as a nested " +
                "object (if it is annotated) or register a scalarMapper for $kClass."
)
