package de.kordondev.lagermelder.core.juleika

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.mock
import java.time.LocalDate

class RealJuleikaValidationServiceTest {
    @ParameterizedTest
    @ValueSource(strings = ["2026-12-08", "08.12.2026", " 08.12.2026 "])
    fun parsesIsoAndGermanExpireDates(date: String) {
        assertThat(RealJuleikaValidationService.parseExpireDate(date)).isEqualTo(LocalDate.of(2026, 12, 8))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "12/08/2026", "not a date"])
    fun returnsNullForUnknownExpireDates(date: String) {
        assertThat(RealJuleikaValidationService.parseExpireDate(date)).isNull()
    }

    @Test
    fun treatsValidCardWithoutKnownExpireDateAsInvalid() {
        val result = service().toResult(JuleikaApiResponse(status = "valid", validTill = "unknown"), "12340")

        assertThat(result).isEqualTo(JuleikaValidationResult(valid = false, expireDate = null))
    }

    @Test
    fun returnsExpireDateOfValidCard() {
        val result = service().toResult(JuleikaApiResponse(status = "valid", validTill = "08.12.2026"), "12340")

        assertThat(result).isEqualTo(JuleikaValidationResult(valid = true, expireDate = LocalDate.of(2026, 12, 8)))
    }

    private fun service() = RealJuleikaValidationService(JuleikaConfig(), mock())

    @Test
    fun returnsNullWithoutExpireDate() {
        assertThat(RealJuleikaValidationService.parseExpireDate(null)).isNull()
    }
}
