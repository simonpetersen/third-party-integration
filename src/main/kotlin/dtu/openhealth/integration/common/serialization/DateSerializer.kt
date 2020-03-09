package dtu.openhealth.integration.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("java.util.Date")

    private val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(df.format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        return df.parse(decoder.decodeString())
    }
}