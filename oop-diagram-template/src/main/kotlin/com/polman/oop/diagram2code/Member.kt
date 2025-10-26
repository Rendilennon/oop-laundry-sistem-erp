package com.polman.oop.diagram2code

class Member(
    id: String,
    name: String,
    val level: MemberLevel
) : Person(id, name) {

    // Override fungsi showInfo untuk menampilkan informasi member dalam format string
    override fun showInfo(): String {
        return "Member[id=$id, name=$name, level=$level]"
    }

    // Override fungsi calculateFee untuk menghitung denda keterlambatan berdasarkan level member
    override fun calculateFee(daysLate: Int): Double {
        if (daysLate <= 0) return 0.0 // Tidak ada denda jika tidak terlambat

        // Menentukan tarif denda per hari berdasarkan level keanggotaan
        val feePerDay = when (level) {
            MemberLevel.REGULAR -> 2000.0     // Tarif REGULAR: 2000 per hari
            MemberLevel.GOLD -> 1500.0        // Tarif GOLD: 1500 per hari
            MemberLevel.PLATINUM -> 1000.0    // Tarif PLATINUM: 1000 per hari
        }

        // Menghitung total denda berdasarkan jumlah hari keterlambatan
        return daysLate * feePerDay
    }
}