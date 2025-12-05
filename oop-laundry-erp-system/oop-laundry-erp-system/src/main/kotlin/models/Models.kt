package com.erp.laundry.models

// 1. ABSTRACTION & INHERITANCE: Superclass
// Semua data memiliki ID, di wariskan dari sini.
abstract class BaseEntity {
    abstract val id: Int
}

// 2. Subclass User
data class User(
    override val id: Int,
    val nama: String,
    val noHp: String,
    val username: String,
    var password: String,
    val role: String,
    var alamat: String = "-"
) : BaseEntity()

// 3. Subclass Inventory
data class Inventory(
    override val id: Int,
    val namaBarang: String,
    var qty: Int,
    val satuan: String
) : BaseEntity()

// 4. Subclass Service
data class Service(
    override val id: Int,
    val namaLayanan: String,
    var hargaPerKg: Long,
    val estimasiHari: Int
) : BaseEntity()

// 5. Subclass Transaction
data class Transaction(
    override val id: Int,
    val idUser: Int,
    val namaUser: String,
    val idService: Int,
    val namaService: String,
    var berat: Double,
    var totalHarga: Long,
    var status: String = "Diterima",
    val tanggalMasuk: String,
    val estimasiSelesai: String,
    var isPaid: Boolean = false,
    var paymentMethod: String = "-"
) : BaseEntity()