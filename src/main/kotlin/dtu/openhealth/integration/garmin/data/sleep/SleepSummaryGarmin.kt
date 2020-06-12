package dtu.openhealth.integration.garmin.data.sleep

import dtu.openhealth.integration.garmin.data.GarminData
import dtu.openhealth.integration.shared.dto.OmhDTO
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.DurationUnit
import org.openmhealth.schema.domain.omh.DurationUnitValue
import org.openmhealth.schema.domain.omh.SleepDuration2

@Serializable
data class SleepSummaryGarmin(
        val userId: String,
        val userAccessToken: String,
        val summaryId: String,
        val calendarDate: String? = null, //Date
        val startTimeInSeconds: Int? = null,
        val startTimeOffsetInSeconds: Int? = null,
        val durationInSeconds: Int? = null,
        val unmeasurableSleepDurationInSeconds: Int? = null,
        val deepSleepDurationInSeconds: Int? = null,
        val lightSleepDurationInSeconds: Int? = null,
        val remSleepInSeconds: Int? = null,
        val awakeDurationInSeconds: Int? = null,
        val sleepLevelsMap: Map<String, List<SleepTimeFrame>>? = null,
        val validation: String? = null,
        val timeOffsetSleepRespiration: Map<String, Float>? = null,
        val timeOffsetSleepSpo2: Map<String, Int>? = null
): GarminData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        val sleepDuration = durationInSeconds?.let {
            SleepDuration2.Builder(
                    DurationUnitValue(DurationUnit.SECOND, (it-awakeDurationInSeconds!!).toBigDecimal()),
                    getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        val localDate = getLocalDate(startTimeInSeconds, startTimeOffsetInSeconds)

        return OmhDTO(extUserId = userAccessToken, date = localDate, sleepDuration2 = sleepDuration)
    }
}

@Serializable
data class SleepTimeFrame(
        val startTimeInSeconds: Int,
        val endTimeInSeconds: Int
)
