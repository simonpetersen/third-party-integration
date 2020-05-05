package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.dto.OmhDTO

interface OmhService {
    fun saveNewestOmhData(dto: OmhDTO)
}