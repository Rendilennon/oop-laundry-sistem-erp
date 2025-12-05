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
            call.respond(HttpStatusCode.OK, LaundryRepository.getServices())
        }

        post {
            val newSvc = call.receive<Service>()
            LaundryRepository.addService(newSvc)
            call.respond(HttpStatusCode.Created, newSvc)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: 0
            val updateData = call.receive<Service>()

            val svc = LaundryRepository.findServiceById(id)

            if (svc != null) {
                svc.hargaPerKg = updateData.hargaPerKg // Update property
                call.respond(HttpStatusCode.OK, "Layanan diupdate")
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/{id}") {
            call.respond(HttpStatusCode.NotImplemented, "Fitur Delete via API butuh update Repo")
        }
    }
}