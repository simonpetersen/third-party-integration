package dtu.openhealth.integration.garmin.mapping

import dtu.openhealth.integration.data.garmin.ActivitySummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*

class ActivityMappingOMHTest {

    private val activityName = "walking"
    private val caloriesBurned = 1
    private val distance = 1
    private val startTime = 1
    private val startTimeOffset = 1

    private val activitySummaryDataAllFields = ActivitySummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", startTime, startTimeOffset,
            activityName, 5, 0.toFloat(), 0, 0.toFloat(),
            0.toFloat(), 0.toFloat(), 0.toFloat(), caloriesBurned, "Garmin",
            distance.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),
            0.toFloat(),0,0.toFloat(),0.toFloat(),0,0.toFloat(),
            0.toFloat(),false,0, false
    )

    private val activitySummaryNoActivityName = ActivitySummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", 1, 1,
            null, 5, 0.toFloat(), 0, 0.toFloat(),
            0.toFloat(), 0.toFloat(), 0.toFloat(), 1, "Garmin",
            0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),
            0.toFloat(),0,0.toFloat(),0.toFloat(),0,0.toFloat(),
            0.toFloat(),false,0, false
    )

    @Test
    fun testMappingToOMH() {
        val measures = activitySummaryDataAllFields.mapToOMH()

        assertThat(measures.size).isEqualTo(1)
        assertThat(measures[0]).isInstanceOf(PhysicalActivity::class.java)

        val physicalActivity: PhysicalActivity = measures[0] as PhysicalActivity

        assertThat(physicalActivity.activityName).isEqualTo(activityName)
        assertThat(physicalActivity.caloriesBurned).isEqualTo(KcalUnitValue(KcalUnit.KILOCALORIE, caloriesBurned.toBigDecimal()))
        assertThat(physicalActivity.distance).isEqualTo(LengthUnitValue(LengthUnit.METER, distance.toBigDecimal()))
        assertThat(physicalActivity.effectiveTimeFrame.timeInterval.startDateTime.toEpochSecond())
                .isEqualTo((startTime + startTimeOffset).toLong())
    }

    @Test
    fun testWithNoMeasures() {
        val measures = activitySummaryNoActivityName.mapToOMH()
        assertThat(measures.size).isEqualTo(0)
    }


}
