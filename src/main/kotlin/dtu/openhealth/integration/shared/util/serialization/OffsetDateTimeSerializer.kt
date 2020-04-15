package dtu.openhealth.integration.shared.util.serialization

import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.OffsetDateTime

@Serializer(forClass = OffsetDateTime::class)
class OffsetDateTimeSerializer: KSerializer<OffsetDateTime> {

    private val LOGGER = LoggerFactory.getLogger(OffsetDateTimeSerializer::class.java)

    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.OffsetDateTime$")

    override fun serialize(encoder: Encoder, obj: OffsetDateTime) {
        LOGGER.info("Serializing offsetDateTime")
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        val offset = OffsetDateTime.parse(decoder.decodeString())
        LOGGER.info("$offset")
        return offset
    }
}
