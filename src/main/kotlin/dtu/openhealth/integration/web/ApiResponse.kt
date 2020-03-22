package dtu.openhealth.integration.web

import dtu.openhealth.integration.data.ThirdPartyData
import kotlinx.serialization.KSerializer

data class ApiResponse(val responseJson: String, val serializer: KSerializer<out ThirdPartyData>)