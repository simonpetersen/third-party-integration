package dtu.openhealth.integration.shared.model

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import dtu.openhealth.integration.garmin.garmin.BodyCompositionSummaryGarmin
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
@TypeDefs(
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
data class User(
        @Id val userId: String = "",
        val extUserId: String = "",
        val token: String = "",
        val refreshToken: String? = null,
        val expireDateTime: LocalDateTime? = null,
        @Type(type = "jsonb")
        @Column(columnDefinition = "jsonb")
        var bodyCompositionSummaryGarmin: BodyCompositionSummaryGarmin? = null
)
