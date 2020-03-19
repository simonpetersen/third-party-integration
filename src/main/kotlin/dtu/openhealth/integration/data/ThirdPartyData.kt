package dtu.openhealth.integration.data

import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.Measure

@Serializable
abstract class ThirdPartyData {
    abstract fun mapToOMH(): List<Measure>
}
