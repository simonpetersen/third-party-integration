package dtu.openhealth.integration.garmin.mapping.omh

import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException
import dtu.openhealth.integration.garmin.data.StressDetailSummaryGarmin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class StressMappingOMHTest {

    private val stressDetails = StressDetailSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", 1, 1,
    1, "1970-01-01", null, null)

    @Test
    fun testMappingToOMH() {
        assertThrows<NoMappingFoundException> { stressDetails.mapToOMH() }
    }

}
