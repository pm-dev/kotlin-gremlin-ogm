package starwars

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
internal open class Application

internal fun main(args: Array<String>) {
    runApplication<Application>(*args)
}


