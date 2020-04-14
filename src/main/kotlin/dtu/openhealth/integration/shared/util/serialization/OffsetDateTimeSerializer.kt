package dtu.openhealth.integration.shared.util.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.OffsetDateTime

@Serializer(forClass = OffsetDateTime::class)
class OffsetDateTimeSerializer: KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.OffsetDateTime$")

    override fun serialize(encoder: Encoder, obj: OffsetDateTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }
}
