package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Property


data class Name(

        @param:Property(key = "first")
        @property:Property(key = "first")
        val first: String,

        @param:Property(key = "last")
        @property:Property(key = "last")
        val last: String? = null
) {
        val full get() = if (last == null) first else "$first $last"
}
