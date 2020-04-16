package dtu.openhealth.integration.garmin.garmin

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.openmhealth.schema.domain.omh.DurationUnit
import org.openmhealth.schema.domain.omh.DurationUnitValue
import org.openmhealth.schema.domain.omh.SleepDuration2

data class SleepSummaryGarmin(
        val userId: String? = null,
        val userAccessToken: String? = null,
        val summaryId: String? = null,
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
    override fun mapToOMH(): OmhDTO {
        val sleepDuration = durationInSeconds?.let {
            SleepDuration2.Builder(
                    DurationUnitValue(DurationUnit.SECOND, (it-awakeDurationInSeconds!!).toBigDecimal()),
                    getTimeInterval(startTimeInSeconds, startTimeOffsetInSeconds, durationInSeconds))
                    .build()
        }

        return OmhDTO(userId = userId, sleepDuration2 = sleepDuration)
    }
}


data class SleepTimeFrame(
        val startTimeInSeconds: Int,
        val endTimeInSeconds: Int
)
