package starwars.models

import org.apache.tinkerpop.gremlin.ogm.annotations.Property

internal data class Name(

        @Property(key = "given")
        val given: String,

        @Property(key = "surname")
        val surname: String? = null
) {
    val full get() = if (surname == null) given else "$given $surname"

    override fun toString() = full

    companion object {
        fun parse(raw: String): Name {
            val nameParts = raw.split(" ")
            val lastName = nameParts.subList(1, nameParts.size).joinToString(" ")
            return Name(given = nameParts.first(), surname = if (lastName.isEmpty()) null else lastName)
        }
    }
}
