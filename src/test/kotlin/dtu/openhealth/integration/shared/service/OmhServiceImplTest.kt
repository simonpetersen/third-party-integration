package dtu.openhealth.integration.shared.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.model.OmhData
import dtu.openhealth.integration.shared.service.impl.OmhServiceImpl
import dtu.openhealth.integration.shared.util.OmhDataType
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Test
import org.openmhealth.schema.domain.omh.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class OmhServiceImplTest {

    private val userId = "abc123-def456"
    private val day = LocalDate.of(2020,4,28)
    private val omhDataId = 7

    @Test
    fun testStepCount_NoOldData() {
        val dateTime = LocalDateTime.of(day, LocalTime.of(14,0,0))
        val startDateTime = dateTime.atOffset(ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))

        val stepCount = StepCount2.Builder(2345L, timeInterval).build()
        val dto = OmhDTO(userId, day, stepCount2 = stepCount)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(emptyList(), dto, userId, day)

        val jsonObject = JsonObject.mapFrom(stepCount)
        val expectedOmhData = OmhData(0, userId, OmhDataType.StepCount2, day, jsonObject)
        verify(omhDataService).insertOmhData(eq(expectedOmhData))
    }

    @Test
    fun testStepCount_NoNewData() {
        val stepCount = initStepCount(2345L)
        val dto = OmhDTO(userId, day, stepCount2 = stepCount)

        val jsonObject = JsonObject.mapFrom(stepCount)
        val omhData = OmhData(0, userId, OmhDataType.StepCount2, day, jsonObject)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(listOf(omhData), dto, userId, day)

        verify(omhDataService, times(0)).insertOmhData(any())
    }

    @Test
    fun testStepCount_UpdateData() {
        val oldStepCount = initStepCount(2345L)
        val jsonObject = JsonObject.mapFrom(oldStepCount)
        val oldOmhData = OmhData(omhDataId, userId, OmhDataType.StepCount2, day, jsonObject)

        val newStepCount = initStepCount(4690L)
        val dto = OmhDTO(userId, day, stepCount2 = newStepCount)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(listOf(oldOmhData), dto, userId, day)

        val newJsonObject = JsonObject.mapFrom(newStepCount)
        verify(omhDataService).updateOmhData(eq(omhDataId), eq(newJsonObject))
    }

    @Test
    fun testPhysicalActivity_NoOldData() {
        val activity1 = initPhysicalActivity("Running",
                LocalTime.of(10,0,0), 5.6)
        val activity2 = initPhysicalActivity("Biking",
                LocalTime.of(16,5,0), 7.9)
        val dto = OmhDTO(userId, day, physicalActivities = listOf(activity1, activity2))

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(emptyList(), dto, userId, day)

        val jsonObject1 = JsonObject.mapFrom(activity1)
        val expectedOmhData1 = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonObject1)
        verify(omhDataService).insertOmhData(eq(expectedOmhData1))

        val jsonObject2 = JsonObject.mapFrom(activity2)
        val expectedOmhData2 = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonObject2)
        verify(omhDataService).insertOmhData(eq(expectedOmhData2))
    }

    @Test
    fun testPhysicalActivity_OneNewActivity() {
        val activityRunning = initPhysicalActivity("Running",
                LocalTime.of(10,0,0), 5.6)
        val activityBiking = initPhysicalActivity("Biking",
                LocalTime.of(16,5,0), 7.9)
        val dto = OmhDTO(userId, day, physicalActivities = listOf(activityRunning, activityBiking))

        val jsonRunning = JsonObject.mapFrom(activityRunning)
        val omhDataRunning = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonRunning)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(listOf(omhDataRunning), dto, userId, day)

        val jsonBiking = JsonObject.mapFrom(activityBiking)
        val expectedOmhDataBiking = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonBiking)
        verify(omhDataService).insertOmhData(eq(expectedOmhDataBiking))
    }

    @Test
    fun testPhysicalActivity_NoNewActivity() {
        val activityRunning = initPhysicalActivity("Running",
                LocalTime.of(10,0,0), 5.6)
        val activityBiking = initPhysicalActivity("Biking",
                LocalTime.of(16,5,0), 7.9)
        val dto = OmhDTO(userId, day, physicalActivities = listOf(activityRunning, activityBiking))

        val jsonRunning = JsonObject.mapFrom(activityRunning)
        val omhDataRunning = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonRunning)
        val jsonBiking = JsonObject.mapFrom(activityBiking)
        val expectedOmhDataBiking = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonBiking)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(listOf(omhDataRunning, expectedOmhDataBiking), dto, userId, day)

        val invocations = 0
        verify(omhDataService, times(invocations)).insertOmhData(any())
    }

    @Test
    fun testPhysicalActivity_OnlyNewActivity() {
        val activityRunning = initPhysicalActivity("Running",
                LocalTime.of(10,0,0), 5.6)
        val activityBiking = initPhysicalActivity("Biking",
                LocalTime.of(16,5,0), 7.9)
        val dto = OmhDTO(userId, day, physicalActivities = listOf(activityBiking))

        val jsonRunning = JsonObject.mapFrom(activityRunning)
        val omhDataRunning = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonRunning)

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(listOf(omhDataRunning), dto, userId, day)

        val jsonBiking = JsonObject.mapFrom(activityBiking)
        val expectedOmhDataBiking = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonBiking)
        verify(omhDataService).insertOmhData(eq(expectedOmhDataBiking))
    }

    @Test
    fun testPhysicalActivity_MultipleNewActivities() {
        val activityRunning = initPhysicalActivity("Running",
                LocalTime.of(10,0,0), 5.6)
        val activityBiking = initPhysicalActivity("Biking",
                LocalTime.of(16,5,0), 7.9)
        val dto = OmhDTO(userId, day, physicalActivities = listOf(activityBiking, activityRunning))

        val userTokensDataService: UserDataService = mock()
        val omhDataService: OmhDataService = mock()
        val omhService = OmhServiceImpl(userTokensDataService, omhDataService)
        omhService.checkAndSaveNewestData(emptyList(), dto, userId, day)

        // Verify biking activity is inserted
        val jsonBiking = JsonObject.mapFrom(activityBiking)
        val expectedOmhDataBiking = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonBiking)
        verify(omhDataService).insertOmhData(eq(expectedOmhDataBiking))

        // Verify running activity is inserted
        val jsonRunning = JsonObject.mapFrom(activityRunning)
        val expectedOmhDataRunning = OmhData(0, userId, OmhDataType.PhysicalActivity, day, jsonRunning)
        verify(omhDataService).insertOmhData(eq(expectedOmhDataRunning))
    }

    private fun initStepCount(stepCount: Long): StepCount2 {
        val dateTime = LocalDateTime.of(day, LocalTime.of(14,0,0))
        val startDateTime = dateTime.atOffset(ZoneOffset.UTC)
        val timeInterval = TimeInterval
                .ofStartDateTimeAndDuration(startDateTime, DurationUnitValue(DurationUnit.DAY,1))

        return StepCount2.Builder(stepCount, timeInterval).build()
    }

    private fun initPhysicalActivity(activityName: String, startTime: LocalTime, distance: Double) : PhysicalActivity{
        val duration = 180000L
        val calories = 347L
        val startDateTime = LocalDateTime.of(day, startTime)
        val timeInterval = TimeInterval.ofStartDateTimeAndDuration(
                startDateTime.atOffset(ZoneOffset.UTC), DurationUnitValue(DurationUnit.MILLISECOND, duration))

        return PhysicalActivity.Builder(activityName)
                .setCaloriesBurned(KcalUnitValue(KcalUnit.KILOCALORIE, calories))
                .setEffectiveTimeFrame(timeInterval)
                .setDistance(LengthUnitValue(LengthUnit.KILOMETER, distance))
                .build()
    }
}