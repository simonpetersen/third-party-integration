package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.garmin.data.ActivitySummaryGarmin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDateTime
import java.time.ZoneOffset

class ActivityMappingOMHTest {

    private val garminUserId = "4aacafe82427c251df9c9592d0c06768"
    private val userAccessToken = "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2"
    private val activityName = "walking"
    private val caloriesBurned = 1
    private val distance = 1
    private val startTime = 1
    private val startTimeOffset = 1

    private val activitySummaryDataAllFields = ActivitySummaryGarmin(garminUserId, userAccessToken,
            "EXAMPLE_678901", startTime, startTimeOffset,
            activityName, 5, 0.toFloat(), 0, 0.toFloat(),
            0.toFloat(), 0.toFloat(), 0.toFloat(), caloriesBurned, "Garmin",
            distance.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),
            0.toFloat(),0,0.toFloat(),0.toFloat(),0,0.toFloat(),
            0.toFloat(),false,0, false
    )

    private val activitySummaryNoActivityName = ActivitySummaryGarmin(garminUserId, userAccessToken,
            "EXAMPLE_678901", 1, 1,
            null, 5, 0.toFloat(), 0, 0.toFloat(),
            0.toFloat(), 0.toFloat(), 0.toFloat(), 1, "Garmin",
            0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),0.toFloat(),
            0.toFloat(),0,0.toFloat(),0.toFloat(),0,0.toFloat(),
            0.toFloat(),false,0, false
    )

    @Test
    fun testMappingToOMH() {
        val omhDTO = activitySummaryDataAllFields.mapToOMH()
        val localDate = LocalDateTime
                .ofEpochSecond(startTime.toLong(), 0, ZoneOffset.ofTotalSeconds(startTimeOffset))
                .toLocalDate()
        assertThat(omhDTO.extUserId).isEqualTo(userAccessToken)
        assertThat(omhDTO.date).isEqualTo(localDate)

        val physicalActivities = omhDTO.physicalActivities
        assertThat(physicalActivities?.size).isEqualTo(1)

        val physicalActivity = physicalActivities?.get(0)
        assertThat(physicalActivity?.activityName).isEqualTo(activityName)
        assertThat(physicalActivity?.caloriesBurned).isEqualTo(KcalUnitValue(KcalUnit.KILOCALORIE, caloriesBurned.toBigDecimal()))
        assertThat(physicalActivity?.distance).isEqualTo(LengthUnitValue(LengthUnit.METER, distance.toBigDecimal()))
        assertThat(physicalActivity?.effectiveTimeFrame?.timeInterval?.startDateTime?.toEpochSecond())
                .isEqualTo((startTime - startTimeOffset).toLong())
    }


}
