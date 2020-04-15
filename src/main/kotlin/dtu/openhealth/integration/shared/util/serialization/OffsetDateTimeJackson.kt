package dtu.openhealth.integration.shared.util.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.vertx.core.logging.LoggerFactory
import java.io.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class JacksonSerializer: JsonSerializer<OffsetDateTime>() {

    private val LOGGER = LoggerFactory.getLogger(JacksonSerializer::class.java)

    override fun serialize(value: OffsetDateTime, gen: JsonGenerator, serializers: SerializerProvider?) {
        LOGGER.info("Serialize")
        gen.writeString(value.toString())
    }
}

class JacksonDeserializer: JsonDeserializer<OffsetDateTime>() {

    private val LOGGER = LoggerFactory.getLogger(JacksonDeserializer::class.java)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OffsetDateTime {
        LOGGER.info("Deserialize")
        return OffsetDateTime.parse(p.valueAsString)
    }
}
