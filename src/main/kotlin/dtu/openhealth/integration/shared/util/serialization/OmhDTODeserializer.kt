package dtu.openhealth.integration.shared.util.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import dtu.openhealth.integration.shared.dto.OmhDTO
import org.apache.kafka.common.serialization.Deserializer
import java.time.OffsetDateTime

class OmhDTODeserializer: Deserializer<Any?> {
    override fun deserialize(s: String, bytes: ByteArray): OmhDTO? {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(OffsetDateTime::class.java, JacksonDeserializer())
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(module)
        var omhDTO: OmhDTO? = null
        try {
            omhDTO = mapper.readValue(bytes, OmhDTO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return omhDTO
    }
}
