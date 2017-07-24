package br.com.fireball.interactive.queries

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableAutoConfiguration
open class Application

fun main(args: Array<String>) {
    val context = SpringApplication.run(Application::class.java, *args)
    context.getBean(EntriesTopology::class.java).entriesStream().start()
}