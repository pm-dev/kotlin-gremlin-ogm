package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class PropertyMissingOnProperty(
        kClass: KClass<*>,
        key: String
) : AnnotationException("@param:Property annotation with name $key must also be found on an " +
        "@property:Property annotation. Class $kClass")
