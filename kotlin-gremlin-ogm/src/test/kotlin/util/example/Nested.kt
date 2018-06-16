package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import java.util.*

internal data class ObjectWithInt(

        @Property(key = "intVal")
        val intVal: Int
) {
    companion object {
        fun sample() = ObjectWithInt(intVal = Random().nextInt())
    }
}

internal data class Nested(

        @Property(key = "objWithInt")
        val nestedObj: ObjectWithInt
) {
    companion object {
        fun sample() = Nested(nestedObj = ObjectWithInt.sample())
    }
}
