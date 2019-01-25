package starwars.graphql.scalars

import graphql.Scalars
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType

internal object GraphQLPageLimit : GraphQLScalarType(
        "PageLimit",
        "[0 - 100]",
        object : Coercing<Int, Int> {

            @Suppress("UNCHECKED_CAST")
            val integerCoercing = Scalars.GraphQLInt.coercing as Coercing<Int, Int>

            override fun serialize(dataFetcherResult: Any?) = integerCoercing.serialize(dataFetcherResult).also {
                if (it < 0 || it > 100) {
                    throw CoercingSerializeException("GQL scalar type 'PageLimit' must be 0-100. Was $dataFetcherResult")
                }
            }

            override fun parseLiteral(input: Any?) = integerCoercing.parseLiteral(input).also {
                if (it < 0 || it > 100) {
                    throw CoercingSerializeException("GQL scalar type 'PageLimit' must be 0-100. Was $input")
                }
            }

            override fun parseValue(input: Any?) = integerCoercing.parseValue(input).also {
                if (it < 0 || it > 100) {
                    throw CoercingSerializeException("GQL scalar type 'PageLimit' must be 0-100. Was $input")
                }
            }
        }
)
