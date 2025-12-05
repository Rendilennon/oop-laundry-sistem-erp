package com.erp.laundry.routes

import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.reportRouting() {
    route("/api/reports") {

        // 1. DASHBOARD
        get("/dashboard") {
            val totalTrx = LaundryRepository.transactionList.size
            val totalOmset = LaundryRepository.transactionList.sumOf { it.totalHarga }
            val totalUser = LaundryRepository.userList.count { it.role == "user" }
            val totalStokItem = LaundryRepository.inventoryList.size

            val dashboardData = mapOf(
                "total_transaksi" to totalTrx,
                "total_pendapatan" to totalOmset,
                "total_pelanggan" to totalUser,
                "total_jenis_barang" to totalStokItem
            )
            call.respond(HttpStatusCode.OK, dashboardData)
        }

        // 2. DAILY REPORT (Harian)
        get("/daily") {
            val dateParam = call.request.queryParameters["date"]
            if (dateParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter date (yyyy-MM-dd)")
                return@get
            }

            // 👇 PERBAIKAN: Gunakan 'tanggalMasuk'
            val dailyTrx = LaundryRepository.transactionList.filter { it.tanggalMasuk.startsWith(dateParam) }
            val totalIncome = dailyTrx.sumOf { it.totalHarga }

            call.respond(HttpStatusCode.OK, mapOf(
                "periode" to "Harian ($dateParam)",
                "jumlah_transaksi" to dailyTrx.size,
                "total_pendapatan" to totalIncome,
                "detail" to dailyTrx
            ))
        }

        // 3. MONTHLY REPORT (Bulanan)
        get("/monthly") {
            val monthParam = call.request.queryParameters["month"]
            if (monthParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter month (yyyy-MM)")
                return@get
            }

            // 👇 PERBAIKAN: Gunakan 'tanggalMasuk'
            val monthlyTrx = LaundryRepository.transactionList.filter { it.tanggalMasuk.startsWith(monthParam) }
            val totalIncome = monthlyTrx.sumOf { it.totalHarga }

            call.respond(HttpStatusCode.OK, mapOf(
                "periode" to "Bulanan ($monthParam)",
                "jumlah_transaksi" to monthlyTrx.size,
                "total_pendapatan" to totalIncome,
                "detail" to monthlyTrx
            ))
        }

        // 4. YEARLY REPORT (Tahunan)
        get("/yearly") {
            val yearParam = call.request.queryParameters["year"]
            if (yearParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter year (yyyy)")
                return@get
            }

            // 👇 PERBAIKAN: Gunakan 'tanggalMasuk'
            val yearlyTrx = LaundryRepository.transactionList.filter { it.tanggalMasuk.startsWith(yearParam) }
            val totalIncome = yearlyTrx.sumOf { it.totalHarga }

            call.respond(HttpStatusCode.OK, mapOf(
                "periode" to "Tahunan ($yearParam)",
                "jumlah_transaksi" to yearlyTrx.size,
                "total_pendapatan" to totalIncome,
                "detail" to yearlyTrx
            ))
        }
    }
}