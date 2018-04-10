package util.example

import org.apache.tinkerpop.gremlin.ogm.annotations.ID
import org.apache.tinkerpop.gremlin.ogm.annotations.Mapper
import org.apache.tinkerpop.gremlin.ogm.annotations.Property
import org.apache.tinkerpop.gremlin.ogm.annotations.Vertex
import org.assertj.core.internal.bytebuddy.utility.RandomString
import java.time.Instant
import java.util.*
import kotlin.math.absoluteValue


@Vertex(label = "VertexWithInt")
class VertexWithInt(

        @ID
        id: Long? = null,

        @Property(key = "a")
        int: Int

) : Base<Int>(id = id, a = int) {
    companion object {
        fun sample() = VertexWithInt(int = Random().nextInt())
    }
}

@Vertex(label = "VertexWithByte")
class VertexWithByte(

        @ID
        id: Long? = null,

        @Property(key = "a")
        byte: Byte

) : Base<Byte>(id = id, a = byte) {
    companion object {
        fun sample() = VertexWithByte(byte = Byte.MAX_VALUE)
    }
}

@Vertex(label = "VertexWithBoolean")
class VertexWithBoolean(

        @ID
        id: Long? = null,

        @Property(key = "a")
        bool: Boolean

) : Base<Boolean>(id = id, a = bool) {
    companion object {
        fun sample() = VertexWithBoolean(bool = Random().nextBoolean())
    }
}

@Vertex(label = "VertexWithDouble")
class VertexWithDouble(

        @ID
        id: Long? = null,


        @Property(key = "a")
        double: Double

) : Base<Double>(id = id, a = double) {
    companion object {
        fun sample() = VertexWithDouble(double = Random().nextDouble())
    }
}

@Vertex(label = "VertexWithFloat")
class VertexWithFloat(

        @ID
        id: Long? = null,

        @Property(key = "a")
        float: Float

) : Base<Float>(id = id, a = float) {
    companion object {
        fun sample() = VertexWithFloat(float = Random().nextFloat())
    }
}

@Vertex(label = "VertexWithLong")
class VertexWithLong(

        @ID
        id: Long? = null,

        @Property(key = "a")
        long: Long

) : Base<Long>(id = id, a = long) {
    companion object {
        fun sample() = VertexWithLong(long = Random().nextLong())
    }
}

@Vertex(label = "VertexWithString")
class VertexWithString(

        @ID
        id: Long? = null,

        @Property(key = "a")
        string: String

) : Base<String>(id = id, a = string) {
    companion object {
        fun sample() = VertexWithString(string = RandomString.make())
    }
}

@Vertex(label = "VertexWithInstant")
class VertexWithInstant(

        @ID
        id: Long? = null,

        @Property(key = "a")
        instant: Instant

) : Base<Instant>(id = id, a = instant) {
    companion object {
        fun sample() = VertexWithInstant(instant = Instant.now())
    }
}

@Vertex(label = "VertexWithUUID")
class VertexWithUUID(

        @ID
        id: Long? = null,

        @Property(key = "a")
        uuid: UUID

) : Base<UUID>(id = id, a = uuid) {
    companion object {
        fun sample() = VertexWithUUID(uuid = UUID.randomUUID())
    }
}

@Vertex(label = "VertexWithDoubleNested")
class VertexWithDoubleNested(

        @ID
        id: Long? = null,

        @Property(key = "a")
        nested: Nested

) : Base<Nested>(id = id, a = nested) {
    companion object {
        fun sample() = VertexWithDoubleNested(nested = Nested.sample())
    }
}

@Vertex(label = "VertexWithPrimitiveList")
class VertexWithPrimitiveList(

        @ID
        id: Long? = null,

        @Property(key = "a")
        listOfInts: List<Int>

) : Base<List<Int>>(id = id, a = listOfInts) {
    companion object {
        fun sample() = VertexWithPrimitiveList(listOfInts = listOf(Random().nextInt(), Random().nextInt()))
    }
}

@Vertex(label = "VertexWithPrimitiveSet")
class VertexWithPrimitiveSet(

        @ID
        id: Long? = null,

        @Property(key = "a")
        setOfStrings: Set<String>

) : Base<Set<String>>(id = id, a = setOfStrings) {
    companion object {
        fun sample() = VertexWithPrimitiveSet(setOfStrings = setOf(RandomString.make(), RandomString.make()))
    }
}

@Vertex(label = "VertexWithObjectList")
class VertexWithObjectList(

        @ID
        id: Long? = null,

        @Property(key = "a")
        listOfObjextWithInts: List<ObjectWithInt>

) : Base<List<ObjectWithInt>>(id = id, a = listOfObjextWithInts) {
    companion object {
        fun sample() = VertexWithObjectList(listOfObjextWithInts = listOf(ObjectWithInt.sample(), ObjectWithInt.sample()))
    }
}

@Vertex(label = "VertexWithPrimitiveMap")
class VertexWithPrimitiveMap(

        @ID
        id: Long? = null,

        @Property(key = "a")
        intMap: Map<String, Int>

) : Base<Map<String, Int>>(id = id, a = intMap) {
    companion object {
        fun sample() = VertexWithPrimitiveMap(intMap = mapOf(
                "first" to Random().nextInt(),
                "second" to Random().nextInt()))
    }
}

@Vertex(label = "VertexWithObjectMap")
class VertexWithObjectMap(

        @ID
        id: Long? = null,

        @Property(key = "a")
        objWithIntMap: Map<String, ObjectWithInt>

) : Base<Map<String, ObjectWithInt>>(id = id, a = objWithIntMap) {
    companion object {
        fun sample() = VertexWithObjectMap(objWithIntMap = mapOf(
                "first" to ObjectWithInt.sample(),
                "second" to ObjectWithInt.sample()))
    }
}

@Vertex(label = "VertexWithEnum")
class VertexWithEnum(

        @ID
        id: Long? = null,

        @Property(key = "a")
        sport: Sport

) : Base<Sport>(id = id, a = sport) {
    companion object {
        fun sample() = VertexWithEnum(sport = Sport.values()[Random().nextInt().absoluteValue % Sport.values().size])
    }
}

@Vertex(label = "VertexWithNumber")
class VertexWithNumber(

        @ID
        id: Long? = null,

        @Property(key = "a")
        number: Number

) : Base<Number>(id = id, a = number) {
    companion object {
        fun sample() = VertexWithNumber(number = Random().nextDouble())
    }
}

@Vertex(label = "VertexWithCustomMapper")
class VertexWithCustomMapper(

        @ID
        id: Long? = null,

        @Property(key = "a")
        @Mapper(Base64Mapper::class)
        string: String

): Base<String>(id = id, a = string) {
    companion object {
        fun sample() = VertexWithCustomMapper(string = RandomString.make())
    }
}

@Vertex(label = "VertexWithNullable")
class VertexWithNullable(

        @ID
        id: Long? = null,

        @Property(key = "a")
        nullableString: String? = null

) : Base<String?>(id = id, a = nullableString) {
    companion object {
        fun sample() = VertexWithNullable()
    }
}

@Vertex(label = "VertexWithTransient")
class VertexWithTransient(

        @param:ID
        @property:ID
        val id: Long? = null,

        val transientString: String? = null

) {
    companion object {
        fun sample() = VertexWithTransient(transientString = RandomString.make())
    }
}

abstract class Base<out T : Any?>(

        @property:ID
        val id: Long? = null,

        @property:Property(key = "a")
        val a: T
) {
    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean = id != null && other != null && other is Base<*> && id == other.id
}
