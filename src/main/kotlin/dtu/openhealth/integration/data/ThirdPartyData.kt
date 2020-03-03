package dtu.openhealth.integration.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
open class ThirdPartyData

@Serializable
data class FitbitData(
        @SerialName("activities-calories") val calories: List<FitbitCalories>?
) : ThirdPartyData()

@Serializable
data class FitbitCalories(val dateTime: String, val value: Int)