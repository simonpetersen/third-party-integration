package dtu.openhealth.integration.shared.model

data class ThirdPartyNotification(val parameters: Map<String, String>, val dataTypeParam: String, val userParam: String)