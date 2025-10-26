package com.polman.oop.diagram2code

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PersonCreationTest {

    @Test
    fun `membuat Member valid - showInfo menampilkan level dan id`() {
        val m = Member("M001", "Ani", MemberLevel.REGULAR)
        assertEquals("Ani", m.name)
        val info = m.showInfo()
        assertTrue(info.contains("Member"), "showInfo harus memuat kata 'Member'")
        assertTrue(info.contains("M001"), "showInfo harus memuat id")
        assertTrue(info.contains("REGULAR"), "showInfo harus memuat level")
    }

    @Test
    fun `setter name melakukan trim dan validasi panjang`() {
        val m = Member("M002", "Budi", MemberLevel.PLATINUM)
        m.name = " Budi Santoso "
        assertEquals("Budi Santoso", m.name)

        val exShort = assertThrows<IllegalArgumentException> {
            m.name = "A" // < 2 huruf
        }
        assertTrue(exShort.message?.contains("nama") == true)
    }

    @Test
    fun `membuat Librarian valid - staffCode nonblank dan fee selalu nol`() {
        val l = Librarian(id = "L001", name = "Sari", staffCode = "LIB-777")
        assertEquals("Sari", l.name)
        assertEquals(0.0, l.calculateFee(10), 0.000001)
        val info = l.showInfo()
        assertTrue(info.contains("Librarian"))
        assertTrue(info.contains("LIB-777"))
    }

    @Test
    fun `gagal membuat turunan Person dengan name blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            Member(id = "M003", name = " ", level = MemberLevel.GOLD)
        }
        assertTrue(ex.message?.contains("nama") == true)
    }

    @Test
    fun `gagal membuat turunan Person dengan id blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            Librarian(id = " ", name = "Dedi", staffCode = "SC-01")
        }
        assertTrue(ex.message?.contains("id") == true)
    }
}