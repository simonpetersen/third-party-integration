package dtu.openhealth.integration.web

interface HttpConnector {
    fun get(url: String, token : String) : String
    fun post(url: String)
}