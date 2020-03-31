package dtu.openhealth.integration.web

data class FitbitRestUrl(override var uri: String) : RestUrl("api.fitbit.com", uri)