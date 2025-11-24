package com.erp.laundry.routes

import com.erp.laundry.models.User
import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.* // Import Wajib untuk Status Code
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/api/users") {

        // 1. GET ALL (200 OK)
        get {
            if (LaundryRepository.userList.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, LaundryRepository.userList)
            } else {
                call.respond(HttpStatusCode.OK, "[]")
            }
        }

        // 2. GET BY ID (200 OK / 400 Bad Request / 404 Not Found)
        get("/{id}") {
            // Validasi: Apakah ID berupa angka?
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID harus berupa angka")
                return@get
            }

            // Cari User
            val user = LaundryRepository.userList.find { it.id == id }
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound, "User dengan ID $id tidak ditemukan")
            }
        }

        // 3. POST (201 Created / 409 Conflict / 400 Bad Request)
        post {
            try {
                val newUser = call.receive<User>()

                // Validasi: Cek Duplikasi Username
                if (LaundryRepository.userList.any { it.username == newUser.username }) {
                    call.respond(HttpStatusCode.Conflict, "Username '${newUser.username}' sudah digunakan")
                    return@post
                }

                LaundryRepository.userList.add(newUser)
                // Berhasil dibuat -> Return 201
                call.respond(HttpStatusCode.Created, newUser)

            } catch (e: Exception) {
                // JSON Error -> Return 400
                call.respond(HttpStatusCode.BadRequest, "Format data JSON salah atau tidak lengkap")
            }
        }

        // 4. PUT (200 OK / 404 Not Found / 400 Bad Request)
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID harus berupa angka")
                return@put
            }

            try {
                val updateData = call.receive<User>()
                val index = LaundryRepository.userList.indexOfFirst { it.id == id }

                if (index != -1) {
                    val oldUser = LaundryRepository.userList[index]
                    // Update data
                    LaundryRepository.userList[index] = oldUser.copy(
                        nama = updateData.nama,
                        noHp = updateData.noHp,
                        password = updateData.password
                    )
                    call.respond(HttpStatusCode.OK, mapOf("status" to "Sukses", "message" to "User berhasil diupdate"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "User tidak ditemukan")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Format JSON salah")
            }
        }

        // 5. DELETE (200 OK / 404 Not Found / 400 Bad Request)
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID harus berupa angka")
                return@delete
            }

            val removed = LaundryRepository.userList.removeIf { it.id == id }
            if (removed) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "Sukses", "message" to "User berhasil dihapus"))
            } else {
                call.respond(HttpStatusCode.NotFound, "User tidak ditemukan")
            }
        }
    }
}