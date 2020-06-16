package dtu.openhealth.integration.fitbit.data.profile

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.LengthUnit
import org.openmhealth.schema.domain.omh.MassUnit
import java.time.LocalDate

class FitbitProfileMappingTest {
    private val fitbitUserId = "djflaefl"
    private val metricUnitType = "METRIC"
    private val nonMetricUnitType = "OtherUnitType"
    private val weight = 84L
    private val height = 181L

    @Test
    fun testFitbitProfileMapping()
    {
        val profile = prepareFitbitProfile(metricUnitType)
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

    @Test
    fun testFitbitProfileMappingNonMetricUnitType()
    {
        val profile = prepareFitbitProfile(nonMetricUnitType)
        val parameters = mapOf(Pair(FitbitConstants.UserParameterTag, fitbitUserId))
        val dto = profile.mapToOMH(parameters)

        assertThat(dto.extUserId).isEqualTo(fitbitUserId)
        assertThat(dto.date).isEqualTo(LocalDate.now())
        assertThat(dto.bodyHeight).isNotNull
        assertThat(dto.bodyWeight).isNotNull

        val bodyHeight = dto.bodyHeight?.bodyHeight
        assertThat(bodyHeight?.value).isEqualTo(height.toBigDecimal())
        assertThat(bodyHeight?.typedUnit).isEqualTo(LengthUnit.INCH)

        val bodyWeight = dto.bodyWeight?.bodyWeight
        assertThat(bodyWeight?.value).isEqualTo(weight.toBigDecimal())
        assertThat(bodyWeight?.typedUnit).isEqualTo(MassUnit.POUND)
    }

    private fun prepareFitbitProfile(unitType: String): FitbitProfile
    {
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
                        topBadges = prepareFitbitTopBadge(),
                        waterUnit = unitType,
                        waterUnitName = "ml",
                        weight = weight,
                        weightUnit = unitType
                )
        )
    }

    private fun prepareFitbitTopBadge(): List<FitbitProfileTopBadge>
    {
        return listOf(
                FitbitProfileTopBadge (
                        badgeGradientEndColor = "badge",
                        badgeGradientStartColor = "color",
                        badgeType = "badgeType",
                        category = "badge",
                        dateTime = "10-12-1999",
                        description = "some description",
                        earnedMessage = "test",
                        encodedId = "id1",
                        image50px = "50px",
                        image75px = "75px",
                        image100px = "100px",
                        image125px = "125px",
                        image300px = "300px",
                        marketingDescription = "marketingDescription",
                        mobileDescription = "mobileDescription",
                        name = "Test Tester",
                        shareImage640px = "640px",
                        shareText = "Share away",
                        shortDescription = "description",
                        shortName = "badgeName",
                        timesAchieved = 10,
                        value = 20,
                        unit = "METRIC"
                )
        )
    }
}