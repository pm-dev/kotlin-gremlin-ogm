package starwars.graphql

import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
internal open class StarwarsGraphQLCorsFilter : CorsFilter(
        UrlBasedCorsConfigurationSource().let { source ->
            val config = CorsConfiguration()
            config.allowCredentials = true
            allowedClients.forEach(config::addAllowedOrigin)
            config.addAllowedHeader("*")
            config.addAllowedMethod("POST")
            source.registerCorsConfiguration("/graphql", config)
            source
        }) {
    companion object {
        val allowedClients = listOf("http://localhost:3000")
    }
}
