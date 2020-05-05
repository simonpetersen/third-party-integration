package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.OmhData
import io.vertx.core.json.JsonObject
import java.time.LocalDate

interface OmhDataService {
    fun getOmhDataOnDate(userId: String, date: LocalDate, listCallback: (List<OmhData>) -> Unit)
    fun insertOmhData(data: OmhData)
    fun updateOmhData(id: Int, data: JsonObject)
}