package org.janusgraph.ogm.annotations

import java.lang.annotation.Inherited

/**
 * Registers a property that should be indexed in the graph. Indexing [Iterable] and [Map] properties is currently
 * unsupported. Indexing nested-object/non-scalar properties is allowed, however be aware that these objects are
 * persisted in the graph with multiple vertex properties, so each vertex property will be indexed.
 * For example
 *      class Name(val first: String, val last: String)
 *      @Element("person") class Person(@Indexed @Property("name") val name: Name)
 *
 *      ... will result in both name properties being indexed: "name.first" and "name.last"
 *
 * The class must be annotated with @Element and registered as a vertex with a GraphMapper.
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.PROPERTY])
@Inherited
annotation class Indexed(

        /**
         * True if the value for this property may not be repeated in different instances of the same type of element.
         */
        val unique: Boolean = false
)
