package dtu.openhealth.integration.shared.util.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.logging.LoggerFactory
import org.apache.kafka.common.serialization.Serializer
import java.time.OffsetDateTime

class OmhDTOSerializer : Serializer<Any?> {

    private val LOGGER = LoggerFactory.getLogger(OmhDTOSerializer::class.java)

    override fun serialize(s: String, o: Any?): ByteArray {
        LOGGER.info("OMHDTOSerializer")
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addSerializer(OffsetDateTime::class.java, JacksonSerializer())
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(module)
        var retVal: ByteArray? = null
        try {
            retVal = mapper.writeValueAsString(o).toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retVal!!
    }
}
