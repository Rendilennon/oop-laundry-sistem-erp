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
            // FIX: Gunakan Getter semua
            val totalTrx = LaundryRepository.getTransactions().size
            val totalOmset = LaundryRepository.getTransactions().sumOf { it.totalHarga }
            val totalUser = LaundryRepository.getUsers().count { it.role == "user" }
            val totalStokItem = LaundryRepository.getInventory().size

            val dashboardData = mapOf(
                "total_transaksi" to totalTrx,
                "total_pendapatan" to totalOmset,
                "total_pelanggan" to totalUser,
                "total_jenis_barang" to totalStokItem
            )
            call.respond(HttpStatusCode.OK, dashboardData)
        }

        // 2. DAILY REPORT
        get("/daily") {
            val dateParam = call.request.queryParameters["date"]
            if (dateParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter date")
                return@get
            }

            val dailyTrx = LaundryRepository.getTransactions().filter { it.tanggalMasuk.startsWith(dateParam) }
            val totalIncome = dailyTrx.sumOf { it.totalHarga }

            call.respond(HttpStatusCode.OK, mapOf(
                "periode" to "Harian ($dateParam)",
                "jumlah_transaksi" to dailyTrx.size,
                "total_pendapatan" to totalIncome,
                "detail" to dailyTrx
            ))
        }

        // 3. MONTHLY REPORT
        get("/monthly") {
            val monthParam = call.request.queryParameters["month"]
            if (monthParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter month")
                return@get
            }

            val monthlyTrx = LaundryRepository.getTransactions().filter { it.tanggalMasuk.startsWith(monthParam) }
            val totalIncome = monthlyTrx.sumOf { it.totalHarga }

            call.respond(HttpStatusCode.OK, mapOf(
                "periode" to "Bulanan ($monthParam)",
                "jumlah_transaksi" to monthlyTrx.size,
                "total_pendapatan" to totalIncome,
                "detail" to monthlyTrx
            ))
        }

        // 4. YEARLY REPORT
        get("/yearly") {
            val yearParam = call.request.queryParameters["year"]
            if (yearParam == null) {
                call.respond(HttpStatusCode.BadRequest, "Harap masukkan parameter year")
                return@get
            }

            val yearlyTrx = LaundryRepository.getTransactions().filter { it.tanggalMasuk.startsWith(yearParam) }
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