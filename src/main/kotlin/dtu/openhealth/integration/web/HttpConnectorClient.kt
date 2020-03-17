package dtu.openhealth.integration.web

interface HttpConnectorClient {
    fun get(url: String, token : String) : String
    fun post(url: String)
}