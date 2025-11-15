package com.erp.laundry
import java.util.Date

fun main() {
    val admin = Admin(1, "Jovan", "08123456789", "admin1", "1234")
    val buyer = Buyer(2, "Sinta", "08129876543", "sinta", "abcd", "Bandung")

    val service = Service(1, "Cuci Setrika", 7000f, 3)
    val transaction = Transaction(
        1, buyer, service, 5.0f, 0f, "Diproses", Date(), service.getEstimasi()
    )

    transaction.harga = transaction.hitungTotal()
    println("Total harga transaksi: Rp${transaction.harga}")

    val report = DailyReport(Date(), 12, 500_000f, 8)
    report.generate()

    println(admin.ubahStatus())
}