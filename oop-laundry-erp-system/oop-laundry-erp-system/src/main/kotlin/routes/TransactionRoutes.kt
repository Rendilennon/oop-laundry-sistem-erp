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
            call.respond(HttpStatusCode.OK, LaundryRepository.transactionList)
        }

        // GET BY ID (Cek Resi)
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val trx = LaundryRepository.transactionList.find { it.id == id }
            if (trx != null) call.respond(HttpStatusCode.OK, trx)
            else call.respond(HttpStatusCode.NotFound, "Transaksi tidak ditemukan")
        }

        // GET BY USER ID (Riwayat User Tertentu)
        // URL: /api/transactions/user/2
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            val history = LaundryRepository.transactionList.filter { it.idUser == userId }
            call.respond(HttpStatusCode.OK, history)
        }
    }
}