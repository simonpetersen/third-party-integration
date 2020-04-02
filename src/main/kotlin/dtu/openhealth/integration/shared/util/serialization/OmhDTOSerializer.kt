package dtu.openhealth.integration.shared.util.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class OmhDTOSerializer : Serializer<Any?> {
    override fun serialize(s: String, o: Any?): ByteArray {
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
