package dtu.openhealth.integration.model

import dtu.openhealth.integration.data.FitbitData
import kotlinx.serialization.KSerializer

data class RestEndpoint(val url: String, val serializer: KSerializer<FitbitData>)