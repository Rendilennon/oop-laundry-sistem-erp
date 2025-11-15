package com.erp.laundry
import java.util.Date

class Transaction(
    val id: Int,
    val pelanggan: Buyer,
    val layanan: Service,
    var berat: Float,
    var harga: Float,
    var status: String,
    val tanggalMasuk: Date,
    var estimasiSelesai: Date
) {
    fun hitungTotal(): Float {
        return layanan.hargaPerKg * berat
    }

    fun updateStatus(newStatus: String) {
        status = newStatus
    }
}