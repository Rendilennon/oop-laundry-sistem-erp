package com.polman.oop.diagram2code

class Librarian(
    id: String,
    name: String,
    val staffCode: String
) : Person(id, name) {

    init {
        require(staffCode.isNotBlank()) { "staffCode tidak boleh kosong" } // Validasi agar staffCode tidak kosong
    }

    // Override fungsi showInfo untuk menampilkan informasi pustakawan
    override fun showInfo(): String {
        return "Librarian[id=$id, name=$name, staffCode=$staffCode]"
    }

    // Override fungsi calculateFee untuk menghitung denda keterlambatan
    // Pustakawan tidak dikenakan denda, sehingga selalu mengembalikan 0.0
    override fun calculateFee(daysLate: Int): Double {
        return 0.0
    }
}