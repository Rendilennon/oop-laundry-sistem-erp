package com.erp.laundry.models

// 1. Model User
data class User(
    val id: Int,
    val nama: String,
    val noHp: String,
    val username: String,
    var password: String,
    val role: String,
    var alamat: String
)

// 2. Model Inventory
data class Inventory(
    val id: Int,
    val namaBarang: String,
    var qty: Int,
    val satuan: String
)

// 3. Model Service
data class Service(
    val id: Int,
    val namaLayanan: String,
    var hargaPerKg: Long,
    val estimasiHari: Int
)

// 4. Model Transaction
data class Transaction(
    val id: Int,
    val idUser: Int,
    val namaUser: String,
    val idService: Int,
    val namaService: String,
    var berat: Double,
    var totalHarga: Long,
    var status: String = "Diterima",
    val tanggalMasuk: String,
    val estimasiSelesai: String, // 👈 Baru: Tanggal kelar
    var isPaid: Boolean = false, // 👈 Baru: Status Pembayaran
    var paymentMethod: String = "-" // 👈 Baru: Metode Bayar
)