package dtu.openhealth.integration.shared.dto

import dtu.openhealth.integration.shared.util.serialization.OffsetDateTimeSerializer
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.openmhealth.schema.domain.omh.*

@Serializable
data class OmhDTO(
    val userId: String? = null,
    @ContextualSerialization val stepCount2: StepCount2? = null,
    @ContextualSerialization val bodyWeight: BodyWeight? = null,
    @ContextualSerialization val bodyMassIndex1: BodyMassIndex1? = null,
    @ContextualSerialization val bodyFatPercentage: BodyFatPercentage? = null,
    @ContextualSerialization val caloriesBurned2: CaloriesBurned2? = null,
    @ContextualSerialization val heartRate: HeartRate? = null,
    @ContextualSerialization val physicalActivity: PhysicalActivity? = null,
    @ContextualSerialization val respiratoryRate: RespiratoryRate? = null,
    @ContextualSerialization val sleepEpisode: SleepEpisode? = null,
    @ContextualSerialization val sleepDuration2: SleepDuration2? = null
)
