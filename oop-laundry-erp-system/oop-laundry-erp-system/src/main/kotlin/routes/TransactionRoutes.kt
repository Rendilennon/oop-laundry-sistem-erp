package com.erp.laundry.routes

import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.transactionRouting() {
    route("/api/transactions") {

        // GET ALL
        get {
            // FIX: Gunakan getTransactions()
            call.respond(HttpStatusCode.OK, LaundryRepository.getTransactions())
        }

        // GET BY ID (Cek Resi)
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: 0
            val trx = LaundryRepository.findTransactionById(id)

            if (trx != null) call.respond(HttpStatusCode.OK, trx)
            else call.respond(HttpStatusCode.NotFound, "Transaksi tidak ditemukan")
        }

        // GET BY USER ID (Riwayat User Tertentu)
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            val history = LaundryRepository.getTransactions().filter { it.idUser == userId }
            call.respond(HttpStatusCode.OK, history)
        }
    }
}