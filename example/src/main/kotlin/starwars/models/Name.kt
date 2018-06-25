package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Property


internal data class Name(

        @Property(key = "first")
        val first: String,

        @Property(key = "last")
        val last: String? = null
) {
    val full get() = if (last == null) first else "$first $last"

    companion object {
        fun parse(raw: String): Name {
            val nameParts = raw.split(" ")
            val lastName = nameParts.subList(1, nameParts.size).joinToString(" ")
            return Name(first = nameParts.first(), last = if (lastName.isEmpty()) null else lastName)
        }
    }
}
