package com.erp.laundry.repository

import com.erp.laundry.models.*

// ENCAPSULATION: Menyembunyikan data asli (private) dan menyediakan akses yang dapat dicontrol (public functions)
object LaundryRepository {

    // Private Properties (Hanya bisa diakses di file ini)
    private val _users = mutableListOf(
        User(1, "Admin Utama", "08123456789", "admin", "admin123", "admin", "Kantor Pusat Laundry, Jakarta"),
    )

    private val _inventory = mutableListOf(
        Inventory(1, "Deterjen Cair", 10, "Liter"),
        Inventory(2, "Pewangi Sakura", 5, "Liter")
    )

    private val _services = mutableListOf(
        Service(1, "Cuci Komplit", 7000, 2),
        Service(2, "Cuci Kilat", 10000, 1),
        Service(3, "Setrika Saja", 5000, 1)
    )

    private val _transactions = mutableListOf<Transaction>()

    // --- Public Accessors (Getter) ---
    fun getUsers(): List<User> = _users
    fun getInventory(): List<Inventory> = _inventory
    fun getServices(): List<Service> = _services
    fun getTransactions(): List<Transaction> = _transactions

    // --- Public Modifiers (Setter/Methods) ---

    // User Methods
    fun addUser(user: User) { _users.add(user) }
    fun updateUser(index: Int, user: User) { _users[index] = user }
    fun removeUser(user: User) { _users.remove(user) }
    fun findUserById(id: Int) = _users.find { it.id == id }
    fun findUserByUsername(username: String) = _users.find { it.username == username }

    // Inventory Methods
    fun addInventory(item: Inventory) { _inventory.add(item) }
    fun findInventoryById(id: Int) = _inventory.find { it.id == id }

    // Service Methods
    fun addService(service: Service) { _services.add(service) }
    fun findServiceById(id: Int) = _services.find { it.id == id }

    // Transaction Methods
    fun addTransaction(trx: Transaction) { _transactions.add(trx) }
    fun findTransactionById(id: Int) = _transactions.find { it.id == id }

    // ID Generator (Helper)
    fun nextUserId() = (_users.maxOfOrNull { it.id } ?: 0) + 1
    fun nextInvId() = (_inventory.maxOfOrNull { it.id } ?: 0) + 1
    fun nextSvcId() = (_services.maxOfOrNull { it.id } ?: 0) + 1
    fun nextTrxId() = (_transactions.maxOfOrNull { it.id } ?: 0) + 1

    // Inventory Helpers
    fun removeInventory(item: Inventory) { _inventory.remove(item) }

    // Service Helpers
    fun removeService(service: Service) { _services.remove(service) }

    fun updateService(index: Int, service: Service) {
        if(index in _services.indices) _services[index] = service
    }
}