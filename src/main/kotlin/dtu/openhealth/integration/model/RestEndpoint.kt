package dtu.openhealth.integration.model

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.web.RestUrl
import kotlinx.serialization.KSerializer

data class RestEndpoint(val url: RestUrl, val serializer: KSerializer<out ThirdPartyData>)