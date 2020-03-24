package dtu.openhealth.integration.common.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.LocalTime

class LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.time.LocalTime$")

    override fun serialize(encoder: Encoder, obj: LocalTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString())
    }
}