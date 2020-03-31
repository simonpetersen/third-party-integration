package dtu.openhealth.integration.model

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
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
        @Id open val userId: String = "",
        open val token: String = "",
        open val refreshToken: String = "",
        @Type(type = "jsonb")
        @Column(columnDefinition = "jsonb")
        var bodyCompositionSummaryGarmin: BodyCompositionSummaryGarmin? = null
)
