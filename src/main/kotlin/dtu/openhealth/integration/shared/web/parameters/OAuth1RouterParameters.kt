package dtu.openhealth.integration.shared.web.parameters

import com.github.scribejava.core.builder.api.DefaultApi10a

data class OAuth1RouterParameters(
        val callbackUri : String,
        val returnUri : String,
        val host: String,
        val consumerKey: String,
        val consumerSecret: String,
        val api: DefaultApi10a)