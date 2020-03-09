package dtu.openhealth.integration.mapping

import dtu.openhealth.integration.data.*
import dtu.openhealth.integration.data.fitbit.*
import org.openmhealth.schema.domain.omh.*
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class FitbitMapper : ThirdPartyMapper {

    override fun mapData(thirdPartyData: ThirdPartyData): List<Measure> {
        if (thirdPartyData !is FitbitData) {
            throw IllegalArgumentException("Data is not FitbitData")
        }

        return mapFitbitData(thirdPartyData)
    }

    private fun mapFitbitData(fitbitData: FitbitData): List<Measure> {
        if (fitbitData is FitbitActivitiesCalories) { // Only temporary implementation. Use Activity Summary instead.
            return fitbitData.calories.map { mapFitbitCalories(it) }.toList()
        }

        if (fitbitData is FitbitActivitiesSummary) {
            return mapActivitySummary(fitbitData)
        }

        //if (fitbitData is SleepSummary)
        //if (fitbitData is HeartRate)
        //if (fitbitData is BodyWeight)

        return emptyList()
    }

    private fun mapActivitySummary(activitiesSummary: FitbitActivitiesSummary) : List<Measure> {
        val measureList = mutableListOf<Measure>()

        // Map data
        measureList.add(mapCalories(activitiesSummary.summary))
        measureList.add(mapStepcount(activitiesSummary.summary))

        activitiesSummary.activities.forEach { measureList.add(mapActivity(it)) }

        return measureList
    }

    private fun mapCalories(activitySummary: FitbitActivitySummary) : Measure {
        val timeInterval = getTimeIntervalFromFitbitDate(LocalDate.now()) // FIX: Parse date as parameter?
        val kcalUnitValue = KcalUnitValue(KcalUnit.KILOCALORIE, activitySummary.caloriesOut)
        return CaloriesBurned2.Builder(kcalUnitValue, timeInterval).build()
    }

    private fun mapStepcount(activitySummary: FitbitActivitySummary) : Measure {
        val timeInterval = getTimeIntervalFromFitbitDate(LocalDate.now())
        return StepCount2.Builder(activitySummary.steps, timeInterval).build()
    }

    private fun mapActivity(activity: FitbitActivity) : Measure {
        val calories = KcalUnitValue(KcalUnit.KILOCALORIE, activity.calories)
        val distance = LengthUnitValue(LengthUnit.KILOMETER, activity.distance)

        val builder = PhysicalActivity.Builder(activity.name)
                .setCaloriesBurned(calories)
                .setDistance(distance)

        return builder.build()
    }

    private fun mapFitbitCalories(fitbitCalories: FitbitCalories) : Measure {
        val timeInterval = getTimeIntervalFromFitbitDate(fitbitCalories.dateTime)
        val kcalUnitValue = KcalUnitValue(KcalUnit.KILOCALORIE, fitbitCalories.value)
        return CaloriesBurned2.Builder(kcalUnitValue, timeInterval).build()
    }

    private fun getTimeIntervalFromFitbitDate(dateTime: LocalDate) : TimeInterval {
        val startDateTime = OffsetDateTime.of(dateTime, LocalTime.MIDNIGHT, ZoneOffset.UTC)
        val endDateTime = startDateTime.plusDays(1)
        return TimeInterval.ofStartDateTimeAndEndDateTime(startDateTime, endDateTime)
    }
}