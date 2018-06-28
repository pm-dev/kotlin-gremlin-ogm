package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class PropertyMapperMissing(
        kClass: KClass<*>
) : ClientException(
        description = "You need to tell the library how to serialize/deserialize property of class: $kClass. " +
                "Where you create your GraphMapper instance, please register a PropertyBiMapper as a scalar mapper."
)
