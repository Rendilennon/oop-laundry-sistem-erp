package com.erp.laundry.routes

import com.erp.laundry.models.User
import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/api/users") {

        // ================= JALUR KHUSUS ADMIN =================
        // URL: http://localhost:8081/api/users/admin
        route("/admin") {
            // GET: Hanya ambil yang role-nya "admin"
            get {
                val admins = LaundryRepository.userList.filter { it.role == "admin" }
                call.respond(HttpStatusCode.OK, admins)
            }

            // POST: Tambah Admin Baru
            post {
                try {
                    val newUser = call.receive<User>()
                    // Paksa role jadi admin, alamat akan ikut dari input JSON
                    val adminUser = newUser.copy(role = "admin")

                    if (LaundryRepository.userList.any { it.username == adminUser.username }) {
                        call.respond(HttpStatusCode.Conflict, "Username sudah ada")
                        return@post
                    }
                    LaundryRepository.userList.add(adminUser)
                    call.respond(HttpStatusCode.Created, adminUser)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Format JSON salah: ${e.localizedMessage}")
                }
            }
        }

        // ================= JALUR KHUSUS USER (BUYER) =================
        // URL: http://localhost:8081/api/users/buyer
        route("/buyer") {
            // GET: Hanya ambil yang role-nya "user"
            get {
                val buyers = LaundryRepository.userList.filter { it.role == "user" }
                call.respond(HttpStatusCode.OK, buyers)
            }

            // POST: Tambah Pembeli Baru (Register)
            post {
                try {
                    val newUser = call.receive<User>()
                    // Paksa role jadi user
                    val buyerUser = newUser.copy(role = "user")

                    if (LaundryRepository.userList.any { it.username == buyerUser.username }) {
                        call.respond(HttpStatusCode.Conflict, "Username sudah ada")
                        return@post
                    }
                    LaundryRepository.userList.add(buyerUser)
                    call.respond(HttpStatusCode.Created, buyerUser)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Format JSON salah: ${e.localizedMessage}")
                }
            }
        }

        // ================= OPERASI UMUM (BY ID) =================

        // GET BY ID: Lihat detail user spesifik
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val user = LaundryRepository.userList.find { it.id == id }
            if (user != null) call.respond(HttpStatusCode.OK, user)
            else call.respond(HttpStatusCode.NotFound, "User tidak ditemukan")
        }

        // PUT: Update User (Penting untuk Ganti Alamat/Password via API)
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            try {
                val updateData = call.receive<User>()
                val index = LaundryRepository.userList.indexOfFirst { it.id == id }

                if (index != -1) {
                    val oldUser = LaundryRepository.userList[index]
                    // Update data (termasuk Alamat baru jika ada di JSON)
                    LaundryRepository.userList[index] = oldUser.copy(
                        nama = updateData.nama,
                        noHp = updateData.noHp,
                        password = updateData.password,
                        alamat = updateData.alamat // Update alamat
                    )
                    call.respond(HttpStatusCode.OK, "User berhasil diupdate")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User tidak ditemukan")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Format JSON salah")
            }
        }

        // DELETE: Hapus User
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID harus angka")
                return@delete
            }
            val removed = LaundryRepository.userList.removeIf { it.id == id }
            if (removed) call.respond(HttpStatusCode.OK, "Akun berhasil dihapus")
            else call.respond(HttpStatusCode.NotFound, "Akun tidak ditemukan")
        }

        // GET ALL (Gabungan)
        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.userList)
        }
    }
}