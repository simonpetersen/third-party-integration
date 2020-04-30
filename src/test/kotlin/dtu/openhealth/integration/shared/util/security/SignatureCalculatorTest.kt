package dtu.openhealth.integration.shared.util.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.exp

class SignatureCalculatorTest {

    @Test
    fun testHmac1() {
        val authString = "POST&https%3A%2F%2Fconnectapi.garmin.com%2Foauth-service%2Foauth%2Frequest_token&oauth_consumer_key%3Dcb60d7f5-4173-7bcd-ae02-e5a52a6940ac%26oauth_nonce%3Dkbki9sCGRwU%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1484837456%26oauth_version%3D1.0"
        val secret = "3LFNjTLbGk5QqWVoypl8S2wAYcSL586E285&"

        val signature = SignatureCalculator.calculateHmacSHA1(secret, authString)

        //val expectedSignature = "%2BHlCpVX8Qgdw5Djfw0W30s7pfrY%3D"
        val expectedSignature = "%2BHlCpVX8Qgdw5Djfw0W30s7pfrY%3D"
        assertThat(signature).isEqualTo(expectedSignature)
    }

    @Test
    fun testTwitter() {
        val baseString = "POST&https%3A%2F%2Fapi.twitter.com%2F1.1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521"
        val secret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"

        val signature = SignatureCalculator.calculateHmacSHA1(secret, baseString)
        val expectedSignature = "hCtSmYh+iHYCEqBWrE7C7hYmtUk="

        assertThat(signature).isEqualTo(expectedSignature)
    }
}