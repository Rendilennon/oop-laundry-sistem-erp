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
            call.respond(HttpStatusCode.OK, LaundryRepository.getInventory())
        }

        // POST: Tambah Barang Baru
        post {
            try {
                val newItem = call.receive<Inventory>()
                LaundryRepository.addInventory(newItem)
                call.respond(HttpStatusCode.Created, newItem)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Format salah: ${e.localizedMessage}")
            }
        }

        // PUT: Update Stok/Nama Barang
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updateData = call.receive<Inventory>()

            // FIX: Cari index di list public dulu
            val index = LaundryRepository.getInventory().indexOfFirst { it.id == id }

            if (index != -1) {
                val item = LaundryRepository.findInventoryById(id!!)
                if (item != null) {
                    item.qty = updateData.qty // Update property langsung
                    // item.namaBarang = updateData.namaBarang
                    call.respond(HttpStatusCode.OK, "Barang diupdate")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Barang tidak ditemukan")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "Barang tidak ditemukan")
            }
        }

        // DELETE: Hapus Barang
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            // FIX: Gunakan find lalu remove
            val item = if(id != null) LaundryRepository.findInventoryById(id) else null

            if (item != null) {
                call.respond(HttpStatusCode.NotImplemented, "Fitur Delete via API perlu update Repository")
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}