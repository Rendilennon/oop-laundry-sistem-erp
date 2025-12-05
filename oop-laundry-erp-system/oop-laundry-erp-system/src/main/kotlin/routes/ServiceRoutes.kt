package com.erp.laundry.routes

import com.erp.laundry.models.Service
import com.erp.laundry.repository.LaundryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.serviceRouting() {
    route("/api/services") {

        get {
            call.respond(HttpStatusCode.OK, LaundryRepository.serviceList)
        }

        post {
            val newSvc = call.receive<Service>()
            LaundryRepository.serviceList.add(newSvc)
            call.respond(HttpStatusCode.Created, newSvc)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val updateData = call.receive<Service>()
            val index = LaundryRepository.serviceList.indexOfFirst { it.id == id }

            if (index != -1) {
                LaundryRepository.serviceList[index] = updateData
                call.respond(HttpStatusCode.OK, "Layanan diupdate")
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val removed = LaundryRepository.serviceList.removeIf { it.id == id }
            if (removed) call.respond(HttpStatusCode.OK, "Layanan dihapus")
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}