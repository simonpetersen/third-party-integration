package dtu.openhealth.integration.shared.util.serializer

import dtu.openhealth.integration.shared.util.serialization.LocalTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalTime

class LocalTimeSerializerTest {

    @Test
    fun testLocalDateTimeSerialization() {
        val json = Json(JsonConfiguration.Stable)
        val expectedTime = LocalTime.of(7,50)
        val dateTimeJson = """{"time":"07:50"}"""
        val localDateTime = json.parse(LocalTimeTestClass.serializer(), dateTimeJson)

        assertThat(localDateTime.time).isEqualTo(expectedTime)
    }
}

@Serializable
data class LocalTimeTestClass(
        @Serializable(with = LocalTimeSerializer::class) val time: LocalTime
)
