package dtu.openhealth.integration.shared.util.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.LocalDateTime

@Serializer(forClass = LocalDateTime::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.LocalDateTime$")

    override fun serialize(encoder: Encoder, obj: LocalDateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}
