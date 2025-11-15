package com.erp.laundry
import java.util.Date
import java.util.Calendar

class Service (
    val id: Int,
    var nama: String,
    var hargaPerKg: Float,
    var durasiHari: Int
    ) {
        fun getEstimasi(): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, durasiHari)
            return calendar.time
        }
}