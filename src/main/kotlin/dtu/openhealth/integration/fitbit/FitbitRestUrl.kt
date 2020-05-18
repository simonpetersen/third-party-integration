package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.shared.web.RestUrl

data class FitbitRestUrl(override var uri: String) : RestUrl(FitbitConstants.Host, uri)