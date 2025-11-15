package com.erp.laundry

class Admin (
    id: Int,
    nama: String,
    noHp: String,
    username: String,
    password: String
    ) : User(id, nama, noHp, username, password) {

        fun ubahStatus(): String {
            return "Status berhasil diubah oleh Admin"
        }
}