package dtu.openhealth.integration.model

import dtu.openhealth.integration.data.ThirdPartyData
import kotlinx.serialization.KSerializer

data class RestEndpoint(val urls: List<String>, val serializer: KSerializer<out ThirdPartyData>)