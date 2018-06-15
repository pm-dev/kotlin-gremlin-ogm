package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Property


internal data class Name(

        @Property(key = "first")
        val first: String,

        @Property(key = "last")
        val last: String? = null
) {
        val full get() = if (last == null) first else "$first $last"
}
