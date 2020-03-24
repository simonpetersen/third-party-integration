package dtu.openhealth.integration.common.serializer

import dtu.openhealth.integration.common.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class LocalDateTimeSerializerTest {

    @Test
    fun testLocalDateTimeSerialization() {
        val json = Json(JsonConfiguration.Stable)
        val expectedDateTime = LocalDateTime.of(2020,3,24,7,13,30)
        val dateTimeJson = """{"dateTime":"2020-03-24T07:13:30.000"}"""
        val localDateTime = json.parse(LocalDateTimeTestClass.serializer(), dateTimeJson)

        assertThat(localDateTime.dateTime).isEqualTo(expectedDateTime)
    }
}

@Serializable
data class LocalDateTimeTestClass(
        @Serializable(with = LocalDateTimeSerializer::class) val dateTime: LocalDateTime
)