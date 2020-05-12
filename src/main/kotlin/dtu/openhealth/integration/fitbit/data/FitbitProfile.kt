package dtu.openhealth.integration.fitbit.data

import dtu.openhealth.integration.shared.dto.OmhDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate

@Serializable
data class FitbitProfile(val user: FitbitProfileInfo) : FitbitData() {
    override fun mapToOMH(parameters: Map<String,String>): OmhDTO {
        val fitbitUserId = parameters[FitbitConstants.UserParameterTag]
        return user.mapToOMH(fitbitUserId)
    }
}

@Serializable
data class FitbitProfileInfo (
        // Personal info
        val age: Long,
        val dateOfBirth: String? = null,
        val displayName: String? = null,
        val displayNameSetting: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val fullName: String? = null,
        val gender: String,
        // Height
        val height: Long,
        val heightUnit: String,
        // Weight
        val weight: Long,
        val weightUnit: String,
        // Stride length
        val strideLengthRunning: Double,
        val strideLengthRunningType: String,
        val strideLengthWalking: Double,
        val strideLengthWalkingType: String,
        // Avatar
        val avatar: String? = null,
        val avatar150: String? = null,
        val avatar640: String? = null,
        // Steps
        val averageDailySteps: Long,
        // Settings
        val ambassador: Boolean,
        val autoStrideEnabled: Boolean,
        val clockTimeDisplayFormat: String? = null,
        val corporate: Boolean = false,
        val corporateAdmin: Boolean = false,
        val distanceUnit: String,
        val encodedId: String,
        val familyGuidanceEnabled: Boolean = false,
        val features: FitbitProfileFeatures? = null,
        val foodsLocale: String,
        val glucoseUnit: String,
        val isChild: Boolean = false,
        val isCoach: Boolean = false,
        val locale: String,
        val memberSince: String,
        val mfaEnabled: Boolean,
        val offsetFromUTCMillis: Long,
        val startDayOfWeek: String,
        val swimUnit: String,
        val timezone: String,
        val topBadges: List<FitbitProfileTopBadge>,
        val waterUnit: String,
        val waterUnitName: String
) {
    fun mapToOMH(fitbitUserId: String?): OmhDTO {
        val date = LocalDate.now()
        val bodyWeight = BodyWeight.Builder(MassUnitValue(massUnit(weightUnit), weight)).build()
        val bodyHeight = BodyHeight.Builder(LengthUnitValue(lengthUnit(heightUnit), height)).build()
        return OmhDTO(extUserId = fitbitUserId, date = date, bodyWeight = bodyWeight, bodyHeight = bodyHeight)
    }

    private fun massUnit(unit: String): MassUnit {
        return unit(unit, MassUnit.KILOGRAM, MassUnit.POUND)
    }

    private fun lengthUnit(unit: String): LengthUnit {
        return unit(unit, LengthUnit.CENTIMETER, LengthUnit.INCH)
    }

    private fun <T> unit(unit: String, metricUnit: T, defaultUnit: T): T {
        if (unit == FitbitConstants.MetricType) {
            return metricUnit
        }

        return defaultUnit
    }
}

@Serializable
data class FitbitProfileFeatures (
        val exerciseGoal: Boolean
)

@Serializable
data class FitbitProfileTopBadge (
        val badgeGradientEndColor: String,
        val badgeGradientStartColor: String,
        val badgeType: String,
        val category: String,
        val cheers: JsonArray,
        val dateTime: String,
        val description: String,
        val earnedMessage: String,
        val encodedId: String,
        val image100px: String,
        val image125px: String,
        val image300px: String,
        val image50px: String,
        val image75px: String,
        val marketingDescription: String,
        val mobileDescription: String,
        val name: String,
        val shareImage640px: String,
        val shareText: String,
        val shortDescription: String,
        val shortName: String,
        val timesAchieved: Long,
        val value: Long,
        val unit: String? = null
)
