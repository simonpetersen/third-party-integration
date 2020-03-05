package dtu.openhealth.integration.mapping

import dtu.openhealth.integration.data.*
import org.openmhealth.schema.domain.omh.*
import java.lang.IllegalArgumentException
import java.time.ZoneOffset
import java.util.*

class FitbitMapper : ThirdPartyMapper {

    override fun mapData(thirdPartyData: ThirdPartyData): List<Measure> {
        if (thirdPartyData !is FitbitData) {
            throw IllegalArgumentException("Data is not FitbitData")
        }

        return mapFitbitData(thirdPartyData)
    }

    private fun mapFitbitData(fitbitData: FitbitData): List<Measure> {
        if (fitbitData is FitbitActivitiesCalories) { // Only temporary implementation. Use Activity Summary instead.
            return fitbitData.calories.map { mapCalories(it) }.toList()
        }

        if (fitbitData is FitbitActivitiesSummary) {
            return mapActivitySummary(fitbitData)
        }

        //if (fitbitData is SleepSummary)
        //if (fitbitData is BodyWeight)

        return emptyList()
    }

    private fun mapActivitySummary(activitiesSummary: FitbitActivitiesSummary) : List<Measure> {
        return emptyList()
    }

    private fun mapCalories(fitbitCalories: FitbitCalories) : Measure {
        val timeInterval = getTimeIntervalFromFitbitDate(fitbitCalories.dateTime)
        val kcalUnitValue = KcalUnitValue(KcalUnit.KILOCALORIE, fitbitCalories.value)
        return CaloriesBurned2.Builder(kcalUnitValue, timeInterval).build()
    }

    private fun getTimeIntervalFromFitbitDate(dateTime: Date) : TimeInterval {
        val startDateTime = dateTime.toInstant().atOffset(ZoneOffset.UTC)
        val endDateTime = startDateTime.plusDays(1)
        return TimeInterval.ofStartDateTimeAndEndDateTime(startDateTime, endDateTime)
    }
}