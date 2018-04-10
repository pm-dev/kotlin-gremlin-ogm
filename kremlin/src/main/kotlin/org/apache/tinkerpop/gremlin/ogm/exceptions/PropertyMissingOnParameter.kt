package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class PropertyMissingOnParameter(
        kClass: KClass<*>,
        key: String
) : AnnotationException("@property:Property annotation with name $key must also be found on an " +
        "@param:Property annotation. Class $kClass")
