package dtu.openhealth.integration.shared.dto

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate

@Serializable
data class OmhDTO(
        val userId: String? = null,
        @ContextualSerialization val date: LocalDate? = null,
        @ContextualSerialization val stepCount2: StepCount2? = null,
        @ContextualSerialization val bodyWeight: BodyWeight? = null,
        @ContextualSerialization val bodyMassIndex1: BodyMassIndex1? = null,
        @ContextualSerialization val bodyFatPercentage: BodyFatPercentage? = null,
        @ContextualSerialization val caloriesBurned2: CaloriesBurned2? = null,
        @ContextualSerialization val heartRate: HeartRate? = null,
        val physicalActivities: List<@ContextualSerialization PhysicalActivity>? = null,
        @ContextualSerialization val respiratoryRate: RespiratoryRate? = null,
        val sleepEpisodes: List<@ContextualSerialization SleepEpisode>? = null,
        @ContextualSerialization val sleepDuration2: SleepDuration2? = null
)
