package br.com.fireball.interactive.queries

import br.com.fireball.interactive.queries.configuration.KafkaConfiguration
import br.com.fireball.interactive.queries.model.Entry
import moip.kafkautils.serde.GenericJsonSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.kstream.TimeWindows
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

        val entriesStream = builder.stream(Serdes.String(), jsonSerde, "test.entries")

        val groupedByMpa = entriesStream.map { _, value ->
            KeyValue(value.moipAccount, value.amount)
        }.groupByKey(Serdes.String(), Serdes.Long())

        groupedByMpa.aggregate(
                { -> 0L },
                { _, value, aggregate -> aggregate + value },
                Serdes.Long(),
                "test.aggregate")
                .to(Serdes.String(), Serdes.Long(), "test.balance")

        groupedByMpa.aggregate(
                { -> 0L },
                { _, value, aggregate -> aggregate + value },
                TimeWindows.of(2000L),
                Serdes.Long(),
                "test.aggregate.by.days")
                .to("test.balance.by.days")

        return builder
    }
}



