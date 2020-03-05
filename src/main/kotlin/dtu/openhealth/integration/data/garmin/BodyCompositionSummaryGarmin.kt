package dtu.openhealth.integration.data.garmin

import java.util.*

data class BodyCompositionSummaryGarmin(val userId: String,
                                        val userAccessToken: UUID,
                                        val summaryId: String,
                                        val measurementTimeInSeconds: Int,
                                        val measurementTimeOffsetInSeconds: Int,
                                        val muscleMassInGrams: Int,
                                        val boneMassInGrams: Int,
                                        val bodyWaterInPercent: Float,
                                        val bodyFatInPercent: Float,
                                        val bodyMassIndex: Float,
                                        val weightInGrams: Int
)
