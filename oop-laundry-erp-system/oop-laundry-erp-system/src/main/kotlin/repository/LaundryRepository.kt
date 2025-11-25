package com.erp.laundry.repository

import com.erp.laundry.models.*

object LaundryRepository {
    val userList = mutableListOf(
        User(1, "Admin", "08123456789", "admin", "admin123", "admin")
    )

    val inventoryList = mutableListOf(
        Inventory(1, "Deterjen Cair", 10, "Liter"),
        Inventory(2, "Pewangi Sakura", 5, "Liter"),
        Inventory(3, "Plastik Packing", 50, "Pcs")
    )

    val serviceList = mutableListOf(
        Service(1, "Cuci Komplit (Reguler)", 7000, 2),
        Service(2, "Cuci Kilat (Express)", 10000, 1),
        Service(3, "Setrika Saja", 5000, 1)
    )

    val transactionList = mutableListOf<Transaction>()
}