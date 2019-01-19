package org.apache.tinkerpop.gremlin.ogm.paths.steps


fun <FROM, TO> Step.ToMany<FROM, TO>.filter(predicate: (TO) -> Boolean): Step.ToMany<FROM, TO> = to(Filter(predicate))
fun <FROM, TO> Step.ToOptional<FROM, TO>.filter(predicate: (TO) -> Boolean): Step.ToOptional<FROM, TO> = to(Filter(predicate))
fun <FROM, TO> Step.ToSingle<FROM, TO>.filter(predicate: (TO) -> Boolean): Step.ToOptional<FROM, TO> = to(Filter(predicate))

fun <FROM, TO, NEXT> Step.ToMany<FROM, TO>.filterMap(map: (TO) -> NEXT?): Step.ToMany<FROM, NEXT> = to(FilterMap(map))
fun <FROM, TO, NEXT> Step.ToOptional<FROM, TO>.filterMap(map: (TO) -> NEXT?): Step.ToOptional<FROM, NEXT> = to(FilterMap(map))
fun <FROM, TO, NEXT> Step.ToSingle<FROM, TO>.filterMap(map: (TO) -> NEXT?): Step.ToOptional<FROM, NEXT> = to(FilterMap(map))

fun <FROM, TO, NEXT> Step.ToMany<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Step.ToMany<FROM, NEXT> = to(FlatMap(map))
fun <FROM, TO, NEXT> Step.ToOptional<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Step.ToMany<FROM, NEXT> = to(FlatMap(map))
fun <FROM, TO, NEXT> Step.ToSingle<FROM, TO>.flatMap(map: (TO) -> Iterable<NEXT>): Step.ToMany<FROM, NEXT> = to(FlatMap(map))

fun <FROM, TO, NEXT> Step.ToMany<FROM, TO>.map(map: (TO) -> NEXT): Step.ToMany<FROM, NEXT> = to(Map(map))
fun <FROM, TO, NEXT> Step.ToOptional<FROM, TO>.map(map: (TO) -> NEXT): Step.ToOptional<FROM, NEXT> = to(Map(map))
fun <FROM, TO, NEXT> Step.ToSingle<FROM, TO>.map(map: (TO) -> NEXT): Step.ToSingle<FROM, NEXT> = to(Map(map))

fun <FROM, TO> Step.ToMany<FROM, TO>.slice(range: LongRange): Step.ToMany<FROM, TO> = to(Slice(range))
fun <FROM, TO> Step.ToMany<FROM, TO>.sort(comparator: Comparator<TO>): Step.ToMany<FROM, TO> = to(Sort(comparator))
fun <FROM, TO> Step.ToMany<FROM, TO>.dedup(): Step.ToMany<FROM, TO> = to(Dedup())
