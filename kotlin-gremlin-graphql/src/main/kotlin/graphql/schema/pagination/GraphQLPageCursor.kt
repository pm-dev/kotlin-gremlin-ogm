package graphql.schema.pagination

import com.google.gson.Gson
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.slf4j.LoggerFactory
import java.util.*

class GraphQLPageCursor<T : Identifiable, I : Page.Info<T>>(
        name: String,
        clazz: Class<I>
) : GraphQLScalarType(
        name,
        "A string allowing clients to resume pagination",
        object : Coercing<I, String> {

            private val logger = LoggerFactory.getLogger(GraphQLPageCursor::class.java)

            private val gson = Gson()

            override fun serialize(dataFetcherResult: Any?): String = when (dataFetcherResult) {
                is Page.Info<*> -> {
                    val jsonString = gson.toJson(dataFetcherResult)
                    logger.debug("Serializing $name as $jsonString")
                    Base64.getEncoder().encodeToString(jsonString.toByteArray())
                }
                else -> throw CoercingSerializeException("$name field cannot serialize '" + dataFetcherResult?.javaClass?.simpleName + "'.")
            }

            override fun parseValue(input: Any?): I = when (input) {
                is String -> {
                    val jsonString = String(Base64.getDecoder().decode(input.toByteArray()))
                    logger.debug("Parsing (deserializing) $name from $jsonString")
                    gson.fromJson(jsonString, clazz)
                }
                else -> throw CoercingSerializeException("$name field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }

            override fun parseLiteral(input: Any?): I = when (input) {
                is StringValue -> parseValue(input.value)
                else -> throw CoercingSerializeException("$name field cannot deserialize '" + input?.javaClass?.simpleName + "'.")
            }
        }
)
