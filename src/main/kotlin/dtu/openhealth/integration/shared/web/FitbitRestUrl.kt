package dtu.openhealth.integration.shared.web

data class FitbitRestUrl(override var uri: String) : RestUrl("api.fitbit.com", uri)