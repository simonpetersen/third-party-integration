package dtu.openhealth.integration.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
open class User(
        @Id open val userId: String = "",
        open val token: String = "",
        open val refreshToken: String = ""
)
