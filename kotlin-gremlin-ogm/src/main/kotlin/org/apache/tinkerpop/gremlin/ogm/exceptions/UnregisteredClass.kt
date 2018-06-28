package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class UnregisteredClass(
        kClass: KClass<*>
) : ClientException(
        description = "Attempting to serialize object to the graph whose class: $kClass " +
                "has not been registered with your GraphMapper instance."
)
