package org.apache.tinkerpop.gremlin.ogm.exceptions

import kotlin.reflect.KClass

internal class PrimaryConstructorMissing(
        kClass: KClass<*>
) : AnnotationException(
        description = "Encountered a vertex object without a primary constructor $kClass"
)
