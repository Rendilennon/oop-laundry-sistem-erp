package com.erp.laundry
import java.util.Date

abstract class Report(
    val tanggal: Date,
    var totalTransaksi: Int,
    var totalPendapatan: Float,
    var totalPelanggan: Int
) {
    abstract fun generate()
}