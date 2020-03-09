package dtu.openhealth.integration.model.repository

import dtu.openhealth.integration.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<User, String> {

    @Transactional
    @Modifying
    @Query("update User u set u.token = :token, u.refreshToken = :refresh_token WHERE u.userId = :id")
    fun updateTokens(@Param("token") token: String,
                     @Param("refresh_token") refreshToken: String,
                     @Param("id") id: String)
}


