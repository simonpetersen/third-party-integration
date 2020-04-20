package dtu.openhealth.integration.shared.util.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dtu.openhealth.integration.shared.util.serialization.JacksonDeserializer
import dtu.openhealth.integration.shared.util.serialization.JacksonSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset


class OffsetDateTimeSerializerTest {

    @Test
    fun testOffsetDateTimeSerialization() {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(OffsetDateTime::class.java, JacksonDeserializer())
        module.addSerializer(OffsetDateTime::class.java, JacksonSerializer())
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(module)
        val expectedTime = OffsetDateTime.of(2020,1,1,1,0,0,0, ZoneOffset.UTC)
        val serializedTime = mapper.writeValueAsString(expectedTime)
        val deserializedTime = mapper.readValue(serializedTime, OffsetDateTime::class.java)

        assertThat(expectedTime.toEpochSecond()).isEqualTo(deserializedTime.toEpochSecond())
    }

}
