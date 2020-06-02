package dtu.openhealth.integration.shared.service.omh.publish

import dtu.openhealth.integration.shared.model.OmhData

interface IOmhPublishService {
    fun publishOmhData(omhData: OmhData)
}