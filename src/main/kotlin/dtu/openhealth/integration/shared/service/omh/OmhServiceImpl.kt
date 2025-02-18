package dtu.openhealth.integration.shared.service.omh

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.model.OmhData
import dtu.openhealth.integration.shared.service.data.omh.IOmhDataService
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.omh.publish.IOmhPublishService
import dtu.openhealth.integration.shared.util.OmhDataType
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import org.openmhealth.schema.domain.omh.Measure
import java.time.LocalDate

class OmhServiceImpl(
        private val userTokenDataService: IUserTokenDataService,
        private val omhDataService: IOmhDataService,
        private val omhPublishService: IOmhPublishService? = null
): IOmhService {

    private val logger = LoggerFactory.getLogger(OmhServiceImpl::class.java)

    override fun saveNewestOmhData(dto: OmhDTO)
    {
        if (dto.extUserId == null || dto.date == null) {
            logger.error("ExtUserId (${dto.extUserId}) or date (${dto.date}) is invalid for $dto")
            return
        }

        userTokenDataService.getUserIdByExtId(dto.extUserId) {
            userId -> userIdCallback(userId, dto.date, dto)
        }
    }

    private fun userIdCallback(userId: String, date: LocalDate, dto: OmhDTO)
    {
        omhDataService.getOmhDataOnDate(userId, date) {
            oldData -> checkAndSaveNewestData(oldData,dto,userId,date)
        }
    }

    fun checkAndSaveNewestData(oldOmhData: List<OmhData>, dto: OmhDTO, userId: String, date: LocalDate)
    {
        checkAndUpdateSingleMeasure(dto.stepCount2, oldOmhData, userId, date, OmhDataType.StepCount2)
        checkAndUpdateSingleMeasure(dto.bodyWeight, oldOmhData, userId, date, OmhDataType.BodyWeight)
        checkAndUpdateSingleMeasure(dto.bodyHeight, oldOmhData, userId, date, OmhDataType.BodyHeight)
        checkAndUpdateSingleMeasure(dto.bodyMassIndex1, oldOmhData, userId, date, OmhDataType.BodyMassIndex1)
        checkAndUpdateSingleMeasure(dto.bodyFatPercentage, oldOmhData, userId, date, OmhDataType.BodyFatPercentage)
        checkAndUpdateSingleMeasure(dto.heartRate, oldOmhData, userId, date, OmhDataType.HeartRate)
        checkAndUpdateSingleMeasure(dto.respiratoryRate, oldOmhData, userId, date, OmhDataType.RespiratoryRate)
        checkAndUpdateSingleMeasure(dto.sleepDuration2, oldOmhData, userId, date, OmhDataType.SleepDuration2)
        checkAndUpdateSingleMeasure(dto.caloriesBurned2, oldOmhData, userId, date, OmhDataType.CaloriesBurned2)

        checkAndUpdateMeasureList(dto.physicalActivities, oldOmhData, userId, date, OmhDataType.PhysicalActivity)
        checkAndUpdateMeasureList(dto.sleepEpisodes, oldOmhData, userId, date, OmhDataType.SleepEpisode)
    }

    private fun checkAndUpdateSingleMeasure(measure: Measure?, oldOmhData: List<OmhData>,
                                            userId: String, date: LocalDate, dataType: OmhDataType)
    {
        if (measure != null) {
            checkSingleMeasure(measure, oldOmhData, userId, date, dataType)
        }
    }

    private fun checkSingleMeasure(measure: Measure, oldOmhData: List<OmhData>,
                                   userId: String, date: LocalDate, dataType: OmhDataType)
    {
        val jsonObject = JsonObject.mapFrom(measure)
        val matchingMeasures = oldOmhData.filter { it.typeOfData == dataType }
        if (matchingMeasures.isEmpty()) {
            val newOmhData = OmhData(0, userId, dataType, date, jsonObject)
            logger.info("$newOmhData is saved in DB.")
            saveNewData(newOmhData)
        }
        else {
            checkAndUpdateSingleOmhData(matchingMeasures, jsonObject)
        }
    }

    private fun checkAndUpdateSingleOmhData(matchingMeasures: List<OmhData>, jsonObject: JsonObject)
    {
        val match = matchingMeasures.first()
        if (match.jsonData != jsonObject) {
            logger.info("OmhData with id = ${match.omhDataId} is updated in DB.")
            val omhData = OmhData(match.omhDataId, match.userId, match.typeOfData, match.date, jsonObject)
            updateData(omhData)
        }
    }

    private fun checkAndUpdateMeasureList(measureList: List<Measure>?, oldOmhData: List<OmhData>,
                                            userId: String, date: LocalDate, dataType: OmhDataType)
    {
        if (measureList != null) {
            checkAndUpdateOmhDataList(measureList, oldOmhData, userId, date, dataType)
        }
    }

    private fun checkAndUpdateOmhDataList(measureList: List<Measure>, oldOmhData: List<OmhData>,
                                 userId: String, date: LocalDate, dataType: OmhDataType)
    {
        val jsonMatchingMeasures = oldOmhData
                .filter { it.typeOfData == dataType }
                .map { JsonObject.mapFrom(it.jsonData) }

        measureList.mapNotNull { measure ->
            val jsonMeasure = JsonObject.mapFrom(measure)
            val noMatches = !jsonMatchingMeasures.any { it == jsonMeasure }
            if (noMatches) OmhData(0, userId, dataType, date, jsonMeasure) else null
        }.forEach {
            saveNewData(it)
        }
    }

    private fun saveNewData(omhData: OmhData)
    {
        omhDataService.insertOmhData(omhData)
        omhPublishService?.publishOmhData(omhData)
    }

    private fun updateData(omhData: OmhData)
    {
        omhDataService.updateOmhData(omhData.omhDataId, omhData.jsonData)
        omhPublishService?.publishOmhData(omhData)
    }
}