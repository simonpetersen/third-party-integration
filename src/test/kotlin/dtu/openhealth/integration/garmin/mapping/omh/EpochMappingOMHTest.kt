package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.garmin.EpochSummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*

class EpochMappingOMHTest {

    private val activityName = "walking"
    private val caloriesBurned = 1
    private val distance = 1F
    private val duration = 1
    private val activeTime = 1
    private val startTime = 1
    private val startTimeOffset = 1
    private val steps = 1
    private val intensity = "SEDENTARY"

    private val epochSummary = EpochSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", startTime, startTimeOffset,
            activityName, duration, activeTime, steps, distance, caloriesBurned, 1F, intensity, 1F, 1F)

    @Test
    fun testEpochMappingOMH() {
        val omhDTO = epochSummary.mapToOMH()

        val stepCount = omhDTO.stepCount2
        assertThat(stepCount).isNotNull
        assertThat(stepCount?.stepCount).isEqualTo(steps.toBigDecimal())
        assertThat(stepCount?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())

        assertThat(omhDTO.physicalActivities?.size).isEqualTo(1)
        val physicalActivity = omhDTO.physicalActivities?.get(0)
        assertThat(physicalActivity?.activityName).isEqualTo(activityName)
        assertThat(physicalActivity?.caloriesBurned).isEqualTo(KcalUnitValue(KcalUnit.KILOCALORIE, caloriesBurned.toBigDecimal()))
        assertThat(physicalActivity?.distance).isEqualTo(LengthUnitValue(LengthUnit.METER, distance.toBigDecimal()))
        assertThat(physicalActivity?.reportedActivityIntensity?.name).isEqualTo("LIGHT")
        assertThat(physicalActivity?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())
    }

}
