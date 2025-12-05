package com.erp.laundry.routes

import com.erp.laundry.models.Inventory
import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.inventoryRouting() {
    route("/api/inventory") {

        // GET: Lihat semua stok
        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.inventoryList)
        }

        // POST: Tambah Barang Baru
        post {
            try {
                val newItem = call.receive<Inventory>()
                LaundryRepository.inventoryList.add(newItem)
                call.respond(HttpStatusCode.Created, newItem)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Format salah")
            }
        }

        // PUT: Update Stok/Nama Barang
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updateData = call.receive<Inventory>()
            val index = LaundryRepository.inventoryList.indexOfFirst { it.id == id }

            if (index != -1) {
                LaundryRepository.inventoryList[index] = updateData
                call.respond(HttpStatusCode.OK, "Barang diupdate")
            } else {
                call.respond(HttpStatusCode.NotFound, "Barang tidak ditemukan")
            }
        }

        // DELETE: Hapus Barang
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val removed = LaundryRepository.inventoryList.removeIf { it.id == id }
            if (removed) call.respond(HttpStatusCode.OK, "Barang dihapus")
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}