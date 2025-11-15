package com.erp.laundry
import java.util.Date

class Inventory(
    val id: Int,
    var name: String,
    var category: Category,
    var stok: Int,
    var price: Int,
    var lastUpdate: Date
){
        fun tambahStok(jumlah: Int) {
            stok += jumlah
            lastUpdate = Date()
        }

        fun cekStok(): Int {
            return stok
        }

    enum class Category {
        DETERJEN, PARFUM, PLASTIK
    }
}