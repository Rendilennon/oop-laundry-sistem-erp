package com.polman.oop.diagram2code

class Book(
    val id: String,
    val title: String,
    val author: String,
    val year: Int,
    val totalCount: Int
) : Loanable {

    private var availableCount: Int = totalCount

    init {
        require(id.isNotBlank()) { "ID buku tidak boleh kosong" } // Sebuah validator untuk ID agar ID tidak diizinkan untuk memiliki null value
        require(title.isNotBlank()) { "Judul buku tidak boleh kosong" } // Sebuah validator untuk Judul agar Judul tidak diizinkan untuk memiliki null value
        require(author.isNotBlank()) { "Author buku tidak boleh kosong" } // Sebuah validator untuk Author agar Author tidak diizinkan untuk memiliki null value
        require(year in 1400..2100) { "Tahun harus antara 1400 dan 2100" } // Sebuah validator untuk Tahun agar Tahun tidak kurang dari 1400 dan 2400 bertujuan untuk input tipe data tanggal yang valid
        require(totalCount >= 0) { "Total Count harus 0 atau lebih" } // Validasi Stok Buku agar dari awal tidak boleh negatif
    }

    // Fungsi untuk mengecek apakah buku masih tersedia untuk dipinjam apa tidak
    fun inStock(): Boolean {
        return availableCount > 0
    }

    // Mengurangi stok jika buku tersedia dan mengembalikan true, jika tidak tersedia maka mengembalikan false
    override fun loan(to: Member): Boolean {
        if (inStock()) {
            availableCount--
            return true
        }
        return false
    }

    // Jika stok sudah penuh, maka kembalikan sebuah notifikasi bahwa stok sudah penuh (over-capacity)
    fun returnOne() {
        if (availableCount >= totalCount) {
            throw IllegalArgumentException("Stok sudah penuh (over-capacity)")
        }
        availableCount++
    }

    // Mengembalikan jumlah stok buku yang tersedia
    fun available(): Int {
        return availableCount
    }

    // Mengembalikan informasi lengkap buku dalam tipe data string
    fun info(): String {
        return "Book[id=$id, title=$title, author=$author, year=$year, stock=$availableCount/$totalCount]"
    }
}
