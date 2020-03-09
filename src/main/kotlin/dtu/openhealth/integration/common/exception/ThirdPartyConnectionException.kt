package dtu.openhealth.integration.common.exception

class ThirdPartyConnectionException(private val exceptionMessage: String) : Exception(exceptionMessage)