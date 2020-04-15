package dtu.openhealth.integration.shared.util.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.apache.kafka.common.serialization.Serializer
import org.openmhealth.schema.serializer.Rfc3339OffsetDateTimeSerializer
import java.time.OffsetDateTime

class OmhDTOSerializer : Serializer<Any?> {
    override fun serialize(s: String, o: Any?): ByteArray {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addSerializer(OffsetDateTime::class.java, JacksonSerializer())
        mapper.registerModule(module)
        var retVal: ByteArray? = null
        val objectMapper = ObjectMapper()
        try {
            retVal = objectMapper.writeValueAsString(o).toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retVal!!
    }
}
