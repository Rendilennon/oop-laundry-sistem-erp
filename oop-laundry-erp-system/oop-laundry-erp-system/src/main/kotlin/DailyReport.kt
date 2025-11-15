package com.erp.laundry
import java.util.Date

class DailyReport(
        tanggal: Date,
        totalTransaksi: Int,
        totalPendapatan: Float,
        totalPelanggan: Int
):Report(tanggal, totalTransaksi, totalPendapatan, totalPelanggan) {
    override fun generate() {
        println("=== Laporan Harian ===")
        println("Tanggal: $tanggal")
        println("Total Transaksi: $totalTransaksi")
        println("Total Pendapatan: $totalPendapatan")
        println("Total Pelanggan: $totalPelanggan\n")
    }
}
