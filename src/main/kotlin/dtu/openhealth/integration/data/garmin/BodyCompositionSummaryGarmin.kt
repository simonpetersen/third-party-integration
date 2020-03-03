package dtu.openhealth.integration.data.garmin

import java.util.*

class BodyCompositionSummaryGarmin(private val userId: String, private val userAccessToken: UUID,
                                   private val summaryId: String, private val measurementTimeInSeconds: Int,
                                   private val measurementTimeOffsetInSeconds: Int, private val muscleMassInGrams: Int,
                                   private val boneMassInGrams: Int, private val bodyWaterInPercent: Float,
                                   private val bodyFatInPercent: Float, private val bodyMassIndex: Float,
                                   private val weightInGrams: Int
)
