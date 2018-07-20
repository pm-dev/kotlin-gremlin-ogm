package graphql.servlet.batched

import org.apache.tinkerpop.gremlin.ogm.GraphMapper

interface GraphMapperFactory {
    
    operator fun invoke(): GraphMapper
}
