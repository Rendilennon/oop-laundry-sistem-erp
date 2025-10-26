package com.polman.oop.diagram2code

abstract class Person(
    val id: String,
    name: String
) {
    // Properti name dapat diubah, namun setiap perubahan akan divalidasi
    var name: String = name
        set(value) {
            val trimmedValue = value.trim() // Menghapus spasi di awal/akhir
            validateName(trimmedValue) // Validasi nama sesuai aturan
            field = trimmedValue // Menyimpan nama yang sudah valid
        }

    init {
        require(id.isNotBlank()) { "id tidak boleh kosong" } // Validasi agar ID tidak kosong
        validateName(this.name)                              // Validasi nama awal
    }

    // Fungsi validasi nama yang digunakan saat inisialisasi dan saat properti name diubah
    protected fun validateName(n: String) {
        val trimmedName = n.trim()
        require(trimmedName.isNotBlank()) { "nama tidak boleh kosong" } // Nama wajib diisi
        require(trimmedName.length in 2..100) { "nama harus 2..100 karakter" } // Panjang nama harus sesuai dengan format yang ada
    }

    // Fungsi abstrak untuk menampilkan informasi objek
    abstract fun showInfo(): String

    // Fungsi abstrak untuk menghitung denda keterlambatan (fee)
    abstract fun calculateFee(daysLate: Int): Double
}