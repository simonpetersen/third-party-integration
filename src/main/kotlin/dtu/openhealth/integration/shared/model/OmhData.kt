package dtu.openhealth.integration.shared.model

import dtu.openhealth.integration.shared.util.OmhDataType
import io.vertx.core.json.JsonObject
import java.time.LocalDate

data class OmhData(
        val omhDataId: Int,
        val userId: String,
        val typeOfData: OmhDataType,
        val date: LocalDate,
        val jsonData: JsonObject
)