package starwars.graphql.scalars

import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import starwars.graphql.scalars.GraphQLTimestamp.MILLIS_IN_SECOND
import java.time.Instant


object GraphQLTimestamp : GraphQLScalarType(
        "Timestamp",
        "A single instantaneous point on the time-line. This timestamp counts in seconds from the unix epoch. " +
                "Accurate to the nearest millisecond. Formatted as a json number.",
        object : Coercing<Instant, Double> {

            override fun serialize(dataFetcherResult: Any?): Double = when (dataFetcherResult) {
                is Instant -> dataFetcherResult.toEpochMilli() / MILLIS_IN_SECOND
                else -> throw CoercingSerializeException("Timestamp field cannot serialize '" + dataFetcherResult?.javaClass?.simpleName + "'.")
            }

            override fun parseLiteral(input: Any?): Instant = when (input) {
                is Number -> Instant.ofEpochMilli((input.toDouble() * MILLIS_IN_SECOND).toLong())
                else -> throw CoercingSerializeException("Timestamp field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }

            override fun parseValue(input: Any?): Instant = when (input) {
                is IntValue -> Instant.ofEpochMilli((input.value.toDouble() * MILLIS_IN_SECOND).toLong())
                is FloatValue -> Instant.ofEpochMilli((input.value.toDouble() * MILLIS_IN_SECOND).toLong())
                else -> throw CoercingSerializeException("Timestamp field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }
        }
) {
    private const val MILLIS_IN_SECOND: Double = 1000.0
}
