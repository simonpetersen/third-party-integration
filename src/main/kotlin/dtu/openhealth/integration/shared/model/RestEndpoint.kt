package dtu.openhealth.integration.shared.model

import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.web.RestUrl
import kotlinx.serialization.KSerializer

data class RestEndpoint(val url: RestUrl, val serializer: KSerializer<out ThirdPartyData>)