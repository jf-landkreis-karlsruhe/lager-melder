package de.kordondev.lagermelder.core.juleika

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
    fun returnsNullWithoutExpireDate() {
        assertThat(RealJuleikaValidationService.parseExpireDate(null)).isNull()
    }
}
