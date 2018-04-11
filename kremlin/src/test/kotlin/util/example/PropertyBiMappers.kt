package util.example

import org.apache.tinkerpop.gremlin.ogm.mappers.PropertyBiMapper
import java.util.*

internal class Base64Mapper : PropertyBiMapper<String, String> {

    override fun forwardMap(from: String): String = Base64.getEncoder().encodeToString(from.toByteArray())

    override fun inverseMap(from: String): String = String(Base64.getDecoder().decode(from))
}

internal class NumberToStringMapper : PropertyBiMapper<Number, String> {

    override fun forwardMap(from: Number): String {
        val prefix = when (from) {
            is Long -> "Long"
            is Byte -> "Byte"
            is Int -> "Int"
            is Double -> "Double"
            is Float -> "Float"
            is Short -> "Short"
            else -> throw Exception("Unrecognized number type: $from")
        }
        return "$prefix:$from"
    }

    override fun inverseMap(from: String): Number {
        val split = from.split(":")
        val prefix = split.first()
        val numberString = split.last()
        return when (prefix) {
            "Long" -> numberString.toLong()
            "Byte" -> numberString.toByte()
            "Int" -> numberString.toInt()
            "Double" -> numberString.toDouble()
            "Float" -> numberString.toFloat()
            "Short" -> numberString.toShort()
            else -> throw Exception("Unrecognized prefix: $from")
        }
    }
}
