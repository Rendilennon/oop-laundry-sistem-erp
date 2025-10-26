package com.polman.oop.diagram2code

interface Loanable {

    // Fungsi loan menerima parameter berupa objek Member yang melakukan peminjaman.
    fun loan(to: Member): Boolean // Mengembalikan nilai Boolean: true jika peminjaman berhasil, false jika gagal.
}