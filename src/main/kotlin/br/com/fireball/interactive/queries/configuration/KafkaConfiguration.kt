package br.com.fireball.interactive.queries.configuration

import org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig.*
import org.springframework.stereotype.Component
import java.util.*


@Component
open class KafkaConfiguration {

    fun  basicProperties(): Properties {
        val streamsConfiguration = Properties().apply {
            put(APPLICATION_ID_CONFIG, "interactive-queries.entries.app-1.0.0")
            put(BOOTSTRAP_SERVERS_CONFIG, "kafka:9092")
            put(ZOOKEEPER_CONNECT_CONFIG, "zookeeper:2181")
            put(KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
            put(VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
            put(MAX_POLL_RECORDS_CONFIG, 100)
        }

        return streamsConfiguration
    }

}