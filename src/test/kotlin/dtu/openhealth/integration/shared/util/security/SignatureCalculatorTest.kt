package dtu.openhealth.integration.shared.util.security

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SignatureCalculatorTest {

    @Test
    fun testHmac1Signature() {
        val key = "key123"
        val text = "Lorum ipsum"

        val signature = SignatureCalculator.hmacSha1(key, text)
        assertNotNull(signature)
        assertTrue(signature.isNotEmpty())
    }
}