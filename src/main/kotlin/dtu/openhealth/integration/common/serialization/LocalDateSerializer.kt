package dtu.openhealth.integration.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.LocalDate

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.LocalDate$")

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}