package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.OmhData
import dtu.openhealth.integration.shared.service.BaseDataService
import dtu.openhealth.integration.shared.service.OmhDataService
import dtu.openhealth.integration.shared.util.OmhDataType
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import java.time.LocalDate

class OmhDataServiceImpl(vertx: Vertx) : BaseDataService(vertx), OmhDataService {

    private val logger = LoggerFactory.getLogger(OmhDataServiceImpl::class.java)

    override fun getOmhDataOnDate(userId: String, date: LocalDate, listCallback: (List<OmhData>) -> Unit) {
        val sql = "SELECT * FROM omhdata WHERE userid = '$userId' AND date = '$date'"
        executeQuery(sql).onComplete { ar ->
            if (ar.succeeded()) {
                val omhDataList = getOmhDataFromResultSet(ar.result())
                listCallback(omhDataList)
            }
            else {
                logger.error(ar.cause())
            }
        }
    }

    override fun insertOmhData(data: OmhData) {
        val sql = "INSERT INTO omhdata (userid,typeofdata,date,jsonData) VALUES " +
                "('${data.userId}','${data.typeOfData}','${data.date}','${data.jsonData}')"
        executeQuery(sql).onComplete {
            ar -> if (ar.failed()) { logger.error(ar.cause()) }
        }
    }

    override fun updateOmhData(id: Int, data: JsonObject) {
        val sql = "UPDATE omhdata SET jsonData = '$data' WHERE omhdataid = '$id'"
        executeQuery(sql).onComplete {
            ar -> if (ar.failed()) { logger.error(ar.cause()) }
        }
    }

    private fun getOmhDataFromResultSet(result: RowSet<Row>) : List<OmhData> {
        val omhList = mutableListOf<OmhData>()
        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val row = iterator.next()
            val omhDataId = row.getInteger("omhdataid")
            val userId = row.getString("userid")
            val typeOfData = row.getString("typeofdata")
            val date = row.getLocalDate("date")
            val jsonData = row.get(JsonObject::class.java, 4)

            omhList.add(OmhData(omhDataId, userId, OmhDataType.valueOf(typeOfData), date, jsonData))
        }

        return omhList
    }
}