package starwars.graphql.scalars

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.net.URL

internal object GraphQLURL : GraphQLScalarType(
        "URL",
        "A url to a resource on the web. Formatted as a json string.",
        object : Coercing<URL, String> {

            override fun serialize(dataFetcherResult: Any?): String = when (dataFetcherResult) {
                is URL -> dataFetcherResult.toString()
                else -> throw CoercingSerializeException("URL field cannot serialize '" + dataFetcherResult?.javaClass?.simpleName + "'.")
            }

            override fun parseLiteral(input: Any?): URL = when (input) {
                is String -> URL(input)
                else -> throw CoercingSerializeException("URL field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }

            override fun parseValue(input: Any?): URL = when (input) {
                is StringValue -> URL(input.value)
                else -> throw CoercingSerializeException("URL field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }
        }
)
