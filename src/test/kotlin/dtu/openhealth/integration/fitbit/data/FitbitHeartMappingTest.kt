package dtu.openhealth.integration.fitbit.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.TemporalRelationshipToPhysicalActivity
import java.time.LocalDate

class FitbitHeartMappingTest {
    private val date = LocalDate.of(2020,6,27)
    private val fitbitUserId = "dljflejsiof"
    private val restingHeartRate = 58L

    @Test
    fun testFitbitHeartRate() {
        val heartRateSummary = FitbitHeartRateSummary(listOf(prepareActivitiesHeart()))
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId))
        val dto = heartRateSummary.mapToOMH(parameters)

        assertThat(dto.extUserId).isEqualTo(fitbitUserId)
        assertThat(dto.heartRate).isNotNull
        assertThat(dto.heartRate?.heartRate?.value?.longValueExact())
                .isEqualTo(restingHeartRate)
        assertThat(dto.heartRate?.temporalRelationshipToPhysicalActivity)
                .isEqualTo(TemporalRelationshipToPhysicalActivity.AT_REST)
    }

    private fun prepareActivitiesHeart(): FitbitActivitiesHeart {
        return FitbitActivitiesHeart(date, FitbitHeartRateValues(emptyList(), emptyList(), restingHeartRate))
    }
}