package dtu.openhealth.integration.common.serializer

import dtu.openhealth.integration.common.serialization.LocalDateSerializer
import dtu.openhealth.integration.data.fitbit.FitbitCalories
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LocalDateSerializerTest {

    @Test
    fun testLocalDateSerializer() {
        val json = Json(JsonConfiguration.Stable)
        val expectedDate = LocalDate.of(2020, 2, 5)
        val dateJson = """{"date":"2020-02-05"}"""
        val localDate = json.parse(LocalDateTestClass.serializer(), dateJson)

        assertThat(localDate.date).isEqualTo(expectedDate)
    }
}

@Serializable
data class LocalDateTestClass(
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate
)