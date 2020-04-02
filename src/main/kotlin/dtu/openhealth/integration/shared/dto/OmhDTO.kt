package dtu.openhealth.integration.shared.dto

import org.openmhealth.schema.domain.omh.Measure

class OmhDTO(var userId: String, var measures: List<Measure>, var startTime: Long, var endTime: Long)
