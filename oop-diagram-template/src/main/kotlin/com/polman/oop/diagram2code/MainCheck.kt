package com.polman.oop.diagram2code

fun main() {
    println("====== TEMPLATE OOP: DIMULAI ======\n")

    // Membuat objek Member dan Librarian untuk test
    println("== Membuat objek Member dan Librarian ==")
    val m1 = Member("M001", "Ani", MemberLevel.REGULAR) // Member reguler
    val m2 = Member("M002", "Budi", MemberLevel.PLATINUM) // Member platinum
    val lib = Librarian("L001", "Sari", "LIB-777") // Pustakawan dengan kode staf

    // Menampilkan informasi masing-masing objek
    println("\n== Informasi Member dan Librarian ==")
    println(m1.showInfo())    // Menampilkan info member m1
    println(m2.showInfo())    // Menampilkan info member m2
    println(lib.showInfo())   // Menampilkan info pustakawan

    // Melakukan update nama pada objek m1
    println("\n== Update nama Member m1 ==")
    m1.name = " Ani Putri "   // Mengubah nama member m1
    println("Nama m1: '${m1.name}'") // Menampilkan nama baru

    // Menghitung fee keterlambatan selama 3 hari untuk masing-masing tipe pengguna
    println("\n== Perhitungan Fee untuk 3 hari ==")
    println("Fee REGULAR (3 hari): ${m1.calculateFee(3)}")
    println("Fee PLATINUM (3 hari): ${m2.calculateFee(3)}")
    println("Fee Librarian (3 hari): ${lib.calculateFee(3)}") // Librarian tidak dikenakan fee

    // Membuat objek buku dan menampilkan informasinya
    println("\n== Informasi Buku ==")
    val b1 = Book("B001", "Clean Code", "Robert C. Martin", 2008, 2)
    println(b1.info()) // Menampilkan detail buku

    // Simulasi peminjaman buku oleh member
    println("\n== Proses Peminjaman Buku ==")
    println("loan(m1) = ${b1.loan(m1)} -> stok=${b1.available()}") // Peminjaman pertama oleh m1
    println("loan(m2) = ${b1.loan(m2)} -> stok=${b1.available()}") // Peminjaman kedua oleh m2
    println("loan(m1) (habis) = ${b1.loan(m1)} -> stok=${b1.available()}") // Peminjaman gagal karena stok habis

    // Menampilkan stok buku saat ini
    println("\n== Cek Stok Buku ==")
    println("stok=${b1.available()}")

    // Simulasi pengembalian buku
    println("\n== Proses Pengembalian Buku ==")
    b1.returnOne() // Mengembalikan satu buku
    println("Setelah returnOne -> stok=${b1.available()}")

    // Simulasi pengembalian berlebih untuk memicu exception
    try {
        b1.returnOne() // Pengembalian kedua
        println("Stok sekarang ${b1.available()}. Coba kembalikan lagi...")
        b1.returnOne() // Pengembalian ketiga, melebihi kapasitas
    } catch (e: IllegalArgumentException) {
        println("OK: exception over-capacity -> ${e.message}") // Menangkap dan menampilkan pesan error
    }

    println("\n====== TEMPLATE OOP: SELESAI ======")
}