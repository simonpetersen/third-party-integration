package dtu.openhealth.integration.shared.data

import kotlinx.serialization.Serializable
import org.openmhealth.schema.domain.omh.Measure

@Serializable
abstract class ThirdPartyData {
    abstract fun mapToOMH(): List<Measure>
}
