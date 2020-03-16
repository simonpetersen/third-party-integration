package dtu.openhealth.integration.common.serializer

import dtu.openhealth.integration.data.fitbit.FitbitCalories
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LocalDateSerializerTest {

    @Test
    fun testLocalDateSerializer() {
        val caloriesJson = """{"dateTime":"2020-02-05","value":1122}"""
        val json = Json(JsonConfiguration.Stable)
        val expectedDate = LocalDate.of(2020, 2, 5)
        val fitbitCalories = json.parse(FitbitCalories.serializer(), caloriesJson)
        assertThat(fitbitCalories.value).isEqualTo(1122)
        assertThat(fitbitCalories.dateTime).isEqualTo(expectedDate)

        val jsonString = json.stringify(FitbitCalories.serializer(), fitbitCalories)
        assertThat(caloriesJson).isEqualTo(jsonString)
    }
}
