package com.polman.oop.diagram2code

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FeePolicyPolymorphismTest {

    @Test
    fun `fee member REGULAR, GOLD, PLATINUM dan librarian`() {
        val reg = Member("M10", "Citra", MemberLevel.REGULAR)
        val gold = Member("M11", "Dodi", MemberLevel.GOLD)
        val plat = Member("M12", "Eka", MemberLevel.PLATINUM)
        val lib = Librarian("L10", "Rina", "SC-01")

        // Asumsi tarif: R=2000, G=1500, P=1000 per hari
        assertEquals(0.0, reg.calculateFee(0), 0.000001)
        assertEquals(6000.0, reg.calculateFee(3), 0.000001)

        assertEquals(0.0, gold.calculateFee(0), 0.000001)
        assertEquals(4500.0, gold.calculateFee(3), 0.000001)

        assertEquals(0.0, plat.calculateFee(0), 0.000001)
        assertEquals(3000.0, plat.calculateFee(3), 0.000001)

        assertEquals(0.0, lib.calculateFee(0), 0.000001)
        assertEquals(0.0, lib.calculateFee(3), 0.000001)
    }
}