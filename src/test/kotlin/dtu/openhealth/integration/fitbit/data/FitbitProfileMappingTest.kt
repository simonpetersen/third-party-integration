package dtu.openhealth.integration.fitbit.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.LengthUnit
import org.openmhealth.schema.domain.omh.MassUnit
import java.time.LocalDate

class FitbitProfileMappingTest {
    private val fitbitUserId = "djflaefl"
    private val unitType = "METRIC"
    private val weight = 84L
    private val height = 181L

    @Test
    fun testFitbitProfileMapping() {
        val profile = prepareFitbitProfile()
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId))
        val dto = profile.mapToOMH(parameters)

        assertThat(dto.extUserId).isEqualTo(fitbitUserId)
        assertThat(dto.date).isEqualTo(LocalDate.now())
        assertThat(dto.bodyHeight).isNotNull
        assertThat(dto.bodyWeight).isNotNull

        val bodyHeight = dto.bodyHeight?.bodyHeight
        assertThat(bodyHeight?.value).isEqualTo(height.toBigDecimal())
        assertThat(bodyHeight?.typedUnit).isEqualTo(LengthUnit.CENTIMETER)

        val bodyWeight = dto.bodyWeight?.bodyWeight
        assertThat(bodyWeight?.value).isEqualTo(weight.toBigDecimal())
        assertThat(bodyWeight?.typedUnit).isEqualTo(MassUnit.KILOGRAM)
    }

    private fun prepareFitbitProfile(): FitbitProfile {
        return FitbitProfile(
                FitbitProfileInfo(
                        age = 28,
                        ambassador = false,
                        autoStrideEnabled = true,
                        averageDailySteps = 3512,
                        clockTimeDisplayFormat = "24hour",
                        corporate = false,
                        corporateAdmin = false,
                        dateOfBirth = "1994-02-10",
                        displayName = "Tester T.",
                        displayNameSetting = "name",
                        distanceUnit = unitType,
                        encodedId = fitbitUserId,
                        familyGuidanceEnabled = false,
                        features = FitbitProfileFeatures(true),
                        firstName = "Tester",
                        foodsLocale = "en_US",
                        fullName = "Tester Testsen",
                        gender = "MALE",
                        glucoseUnit = unitType,
                        height = height,
                        heightUnit = unitType,
                        isChild = false,
                        isCoach = false,
                        lastName = "Testsen",
                        locale = "en_US",
                        memberSince = "2020-06-27",
                        mfaEnabled = false,
                        offsetFromUTCMillis = 7200000,
                        startDayOfWeek = "MONDAY",
                        strideLengthRunning = 124.2,
                        strideLengthRunningType = "default",
                        strideLengthWalking = 77.60000000000001,
                        strideLengthWalkingType = "default",
                        swimUnit = unitType,
                        timezone = "Europe/Copenhagen",
                        topBadges = emptyList(),
                        waterUnit = unitType,
                        waterUnitName = "ml",
                        weight = weight,
                        weightUnit = unitType
                )
        )
    }
}