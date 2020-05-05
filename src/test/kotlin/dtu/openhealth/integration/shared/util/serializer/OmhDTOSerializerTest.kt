package dtu.openhealth.integration.shared.util.serializer

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.serialization.OmhDTODeserializer
import dtu.openhealth.integration.shared.util.serialization.OmhDTOSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OmhDTOSerializerTest {

    @Test
    fun testOmhDTOSerializationTest() {
        val omhDTO = prepareOmhDTO()
        val serialized = OmhDTOSerializer().serialize("omh-data", omhDTO)
        val deserialized = OmhDTODeserializer().deserialize("omh-data", serialized)

        assertThat(deserialized).isInstanceOf(OmhDTO::class.java)
        assertThat(omhDTO.extUserId).isEqualTo(deserialized?.extUserId)
        assertThat(omhDTO.stepCount2).isEqualTo(deserialized?.stepCount2)
    }

    private fun prepareOmhDTO() : OmhDTO {

        val userId = "userIdOfUser"

        val steCount2 = StepCount2.Builder(
                4000.toBigDecimal(),
                TimeInterval.ofStartDateTimeAndDuration(
                        OffsetDateTime.of(Instant.ofEpochSecond(1000.toLong()).atZone(ZoneOffset.UTC).toLocalDateTime(),
                                ZoneOffset.ofTotalSeconds(100)),
                        DurationUnitValue((DurationUnit.SECOND), 100.toBigDecimal())))
                .build()

        return OmhDTO(
                extUserId = userId,
                stepCount2 = steCount2
        )
    }

}
