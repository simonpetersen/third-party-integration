package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.model.ThirdPartyData
import kotlinx.serialization.KSerializer

data class ApiResponse(val responseJson: String, val serializer: KSerializer<out ThirdPartyData>, val parameters: Map<String,String>)