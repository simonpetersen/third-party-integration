package dtu.openhealth.integration.shared.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*

@Serializable
data class OmhDTO(
        val userId: String? = null,
        @ContextualSerialization val stepCount2: StepCount2? = null,
        @ContextualSerialization val bodyWeight: BodyWeight? = null,
        @ContextualSerialization val bodyMassIndex1: BodyMassIndex1? = null,
        @ContextualSerialization val bodyFatPercentage: BodyFatPercentage? = null,
        val caloriesBurned2: List<@ContextualSerialization CaloriesBurned2>? = null,
        @ContextualSerialization val heartRate: HeartRate? = null,
        var physicalActivities: List<@ContextualSerialization PhysicalActivity>? = null,
        @ContextualSerialization val respiratoryRate: RespiratoryRate? = null,
        val sleepEpisodes: List<@ContextualSerialization SleepEpisode>? = null,
        @ContextualSerialization val sleepDuration2: SleepDuration2? = null
)
