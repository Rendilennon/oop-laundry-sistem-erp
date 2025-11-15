package com.erp.laundry

open class User(
    val id: Int,
    var nama: String,
    var noHp: String,
    var username: String,
    private var password: String
) {
    fun login(username: String, password: String): Boolean {
        return this.username == username && this.password == password
    }
}