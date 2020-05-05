package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class FitbitSleepMappingTest {
    private val sleepDate = LocalDate.of(2020,6,27)
    private val sleepDateString = "2020-06-27"
    private val fitbitUserId = "hjdlafhska"

    @Test
    fun testFitbitSleepMapping() {
        val sleepLog = prepareSleepLog()
        val sleepSummary = FitbitSleepSummary(
                FitbitSleepStages(deep = 88, light = 266, rem = 92, wake = 61),
                totalMinutesAsleep = 448,
                totalSleepRecords = 1,
                totalTimeInBed = 507)
        val fitbitSleepLogSummary = FitbitSleepLogSummary(listOf(sleepLog), sleepSummary)
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId),
                Pair(FitbitConstants.DateParameterTag, sleepDateString))
        val omhDTO = fitbitSleepLogSummary.mapToOMH(parameters)
        assertThat(omhDTO.extUserId).isEqualTo(fitbitUserId)
        assertThat(omhDTO.date).isEqualTo(sleepDate)

        validateSleepEpisodes(omhDTO, sleepLog)
        validateSleepDuration(omhDTO, sleepSummary)
    }

    private fun validateSleepEpisodes(omhDTO: OmhDTO, sleepLog: FitbitSleepLog) {
        val sleepEpisodeList = omhDTO.sleepEpisodes
        val expectedElements = 1
        assertThat(sleepEpisodeList?.size).isEqualTo(expectedElements)

        val sleepEpisode = sleepEpisodeList?.get(0)
        val episodeTimeFrame = sleepEpisode?.effectiveTimeFrame
        assertThat(episodeTimeFrame?.timeInterval?.startDateTime).isEqualTo(sleepLog.startTime.atOffset(ZoneOffset.UTC))
        assertThat(episodeTimeFrame?.timeInterval?.endDateTime).isEqualTo(sleepLog.endTime.atOffset(ZoneOffset.UTC))
        assertThat(sleepEpisode?.mainSleep).isEqualTo(sleepLog.isMainSleep)
        assertThat(sleepEpisode?.numberOfAwakenings).isEqualTo(sleepLog.awakeningsCount)
        assertThat(sleepEpisode?.latencyToSleepOnset?.value?.longValueExact()).isEqualTo(sleepLog.minutesToFallAsleep)
        assertThat(sleepEpisode?.totalSleepTime?.value?.longValueExact()).isEqualTo(sleepLog.minutesAsleep)
        assertThat(sleepEpisode?.sleepMaintenanceEfficiencyPercentage?.value?.longValueExact()).isEqualTo(sleepLog.efficiency)
        assertThat(sleepEpisode?.latencyToArising?.value?.longValueExact()).isEqualTo(sleepLog.minutesAfterWakeup)
    }

    private fun validateSleepDuration(omhDTO: OmhDTO, sleepSummary: FitbitSleepSummary) {
        val sleepDuration = omhDTO.sleepDuration2

        assertThat(sleepDuration?.sleepDuration?.typedUnit).isEqualTo(DurationUnit.MINUTE)
        assertThat(sleepDuration?.sleepDuration?.value?.longValueExact()).isEqualTo(sleepSummary.totalMinutesAsleep)

        val durationInterval = sleepDuration?.effectiveTimeFrame
        val expectedStartDateTime = sleepDate.atStartOfDay().atOffset(ZoneOffset.UTC)
        assertThat(durationInterval?.timeInterval?.startDateTime).isEqualTo(expectedStartDateTime)
        assertThat(durationInterval?.timeInterval?.duration?.typedUnit).isEqualTo(DurationUnit.DAY)
        assertThat(durationInterval?.timeInterval?.duration?.value?.intValueExact()).isEqualTo(1)
    }

    private fun prepareSleepLog() : FitbitSleepLog {
        return FitbitSleepLog(0,
                awakeDuration = 0,
                awakeningsCount = 17,
                dateOfSleep = sleepDate,
                duration = 30420000,
                efficiency = 96,
                endTime = LocalDateTime.of(sleepDate, LocalTime.of(7,13,30)),
                isMainSleep = true,
                logId = 26454176508,
                levels = FitbitSleepLevels(emptyList(), emptyList()),
                minutesAfterWakeup = 2,
                minutesAsleep = 488,
                minutesAwake = 19,
                minutesToFallAsleep = 11,
                startTime = LocalDateTime.of(LocalDate.of(2020,6,26), LocalTime.of(22,46,30)),
                timeInBed = 507,
                type = "stages",
                infoCode = 0
        )
    }
}
