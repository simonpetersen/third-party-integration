package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.model.AThirdPartyData
import kotlinx.serialization.KSerializer

data class ApiResponse(
        val responseJson: String,
        val serializer: KSerializer<out AThirdPartyData>,
        val parameters: Map<String,String>
)