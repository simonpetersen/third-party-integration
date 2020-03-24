package dtu.openhealth.integration.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.LocalDate

@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.LocalDate$")

    override fun serialize(encoder: Encoder, obj: LocalDate) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}