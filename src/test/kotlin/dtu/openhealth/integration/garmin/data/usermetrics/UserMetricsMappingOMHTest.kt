package dtu.openhealth.integration.garmin.data.usermetrics

import dtu.openhealth.integration.shared.util.exception.NoMappingFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserMetricsMappingOMHTest {

    private val userMetricsSummary = UserMetricsSummaryGarmin("4aacafe82427c251df9c9592d0c06768",
            "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", "EXAMPLE_678901", "1970-01-01", 1F, 1)

    @Test
    fun testMappingToOMH() {
        assertThrows<NoMappingFoundException> { userMetricsSummary.mapToOMH() }
    }

}
