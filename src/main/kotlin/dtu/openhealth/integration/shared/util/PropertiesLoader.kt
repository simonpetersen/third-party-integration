package dtu.openhealth.integration.shared.util

import java.io.IOException
import java.util.Properties

object PropertiesLoader {
    @Throws(IOException::class)
    fun loadProperties(): Properties {
        val configuration = Properties()
        val inputStream = PropertiesLoader::class.java
                .classLoader
                .getResourceAsStream("application.properties.example")
        configuration.load(inputStream)
        inputStream.close()
        return configuration
    }
}
