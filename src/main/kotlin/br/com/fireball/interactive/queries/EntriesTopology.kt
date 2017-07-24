package br.com.fireball.interactive.queries

import br.com.fireball.interactive.queries.configuration.KafkaConfiguration
import br.com.fireball.interactive.queries.model.Entry
import moip.kafkautils.serde.GenericJsonSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class EntriesTopology {

    private val logger = LoggerFactory.getLogger(EntriesTopology::class.java)

    @Autowired
    private lateinit var kafkaConfiguration: KafkaConfiguration

    fun entriesStream(): KafkaStreams {
        val stream = KafkaStreams(streamBuilder(), kafkaConfiguration.basicProperties())

        stream.setUncaughtExceptionHandler { _, throwable ->
            logger.info("Error on entries topology {}", throwable)
        }

        return stream
    }

    private fun streamBuilder(): KStreamBuilder {

        val jsonSerde = GenericJsonSerde(Entry::class.java)
        val builder = KStreamBuilder()

        val entriesStream = builder.stream(Serdes.StringSerde(), jsonSerde, "test.entries")

        val groupedByMpa = entriesStream.map { _, value -> generateKV(value.moipAccount, value.amount) }.groupByKey()
        val consolidatedByMpa = groupedByMpa.reduce({ value1, value2 ->

            logger.info("MAP [{} ${value1.javaClass}] to [{} ${value2.javaClass}]", value1, value2)
            value1 + value2
        },
                "consolidated_by_mpa")
                .to(Serdes.StringSerde(), Serdes.LongSerde(), "test.balance")

        return builder
    }

    private fun sumValues(value1: Any, value2: Any): Any {
        logger.info("SUM [{} ${value1.javaClass}] and [{} ${value2.javaClass}]", value1, value2)

        return value2
    }

    private fun generateKV(mpa: String, amount: Long): KeyValue<String, Long> {
        logger.info("MAP [{} ${mpa.javaClass}] to [{} ${amount.javaClass}]", mpa, amount)
        return KeyValue(mpa, amount)
    }
}


