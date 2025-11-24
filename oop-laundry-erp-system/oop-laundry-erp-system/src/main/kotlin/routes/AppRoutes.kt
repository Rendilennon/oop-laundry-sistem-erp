package com.erp.laundry.routes

import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.appRouting() {

    // === API INVENTORY ===
    route("/api/inventory") {
        // 200 OK
        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.inventoryList)
        }

        // 404 Not Found
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID salah")
                return@get
            }
            val item = LaundryRepository.inventoryList.find { it.id == id }
            if (item != null) call.respond(HttpStatusCode.OK, item)
            else call.respond(HttpStatusCode.NotFound, "Barang tidak ditemukan")
        }
    }

    // === API SERVICES ===
    route("/api/services") {
        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.serviceList)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID salah")
                return@get
            }
            val svc = LaundryRepository.serviceList.find { it.id == id }
            if (svc != null) call.respond(HttpStatusCode.OK, svc)
            else call.respond(HttpStatusCode.NotFound, "Layanan tidak ditemukan")
        }
    }

    // === API TRANSACTIONS ===
    route("/api/transactions") {
        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.transactionList)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID salah")
                return@get
            }

            val trx = LaundryRepository.transactionList.find { it.id == id }
            if (trx != null) {
                call.respond(HttpStatusCode.OK, trx)
            } else {
                call.respond(HttpStatusCode.NotFound, "Transaksi #$id tidak ditemukan")
            }
        }
    }
}