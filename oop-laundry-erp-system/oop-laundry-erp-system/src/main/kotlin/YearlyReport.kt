package com.erp.laundry
import java.util.Date

    class YearlyReport(
        tanggal: Date,
        totalTransaksi: Int,
        totalPendapatan: Float,
        totalPelanggan: Int
    ) : Report(tanggal, totalTransaksi, totalPendapatan, totalPelanggan) {

        override fun generate() {
            println("=== Laporan Tahunan ===")
            println("Tanggal: $tanggal")
            println("Total Transaksi: $totalTransaksi")
            println("Total Pendapatan: $totalPendapatan")
            println("Total Pelanggan: $totalPelanggan\n")
        }
}