package org.apache.tinkerpop.gremlin.ogm.paths.steps

import org.apache.tinkerpop.gremlin.ogm.paths.Path

/**
 * A step that maps the current object to a new object, or removes the current object if the map function returns null.
 */
class FilterMap<OUT, IN>(private val map: (OUT) -> IN?) : Step.ToOptional<OUT, IN>({
    it.traversal.map { map(it.get()) }.filter { it.get() != null }.map { it.get()!! }
})

fun <OUT, IN, NEXT> Path.ToMany<OUT, IN>.filterMap(map: (IN) -> NEXT?): Path.ToMany<OUT, NEXT> = to(FilterMap(map))
fun <OUT, IN, NEXT> Path.ToOptional<OUT, IN>.filterMap(map: (IN) -> NEXT?): Path.ToOptional<OUT, NEXT> = to(FilterMap(map))
fun <OUT, IN, NEXT> Path.ToSingle<OUT, IN>.filterMap(map: (IN) -> NEXT?): Path.ToOptional<OUT, NEXT> = to(FilterMap(map))

