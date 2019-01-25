package starwars.graphql.scalars

import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.math.BigDecimal

internal object GraphQLDecimal : GraphQLScalarType(
        "Decimal",
        "A number in base 10. Formatted as a json string (because floating point decimal numbers may not have " +
                "an exact binary representation when stored as a float or double)",
        object : Coercing<BigDecimal, String> {

            override fun serialize(dataFetcherResult: Any?): String = when (dataFetcherResult) {
                is BigDecimal -> dataFetcherResult.toString()
                else -> throw CoercingSerializeException("Decimal field cannot serialize '" + dataFetcherResult?.javaClass?.simpleName + "'.")
            }

            override fun parseLiteral(input: Any?): BigDecimal = when (input) {
                is String -> BigDecimal(input)
                is Long -> BigDecimal.valueOf(input)
                is Int -> BigDecimal.valueOf(input.toLong())
                is Double -> BigDecimal.valueOf(input)
                is Float -> BigDecimal.valueOf(input.toDouble())
                is Short -> BigDecimal.valueOf(input.toLong())
                else -> throw CoercingSerializeException("Decimal field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }

            override fun parseValue(input: Any?): BigDecimal = when (input) {
                is StringValue -> BigDecimal(input.value)
                is FloatValue -> input.value
                is IntValue -> input.value.toBigDecimal()
                else -> throw CoercingSerializeException("Decimal field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }
        }
)
