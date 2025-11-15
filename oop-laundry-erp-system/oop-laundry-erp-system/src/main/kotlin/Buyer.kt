package com.erp.laundry

class Buyer (
    id: Int,
    nama: String,
    noHp: String,
    username: String,
    password: String,
    var alamat: String
    ) : User(id, nama, noHp, username, password) {

        fun ubahStatus(): String {
            return "Status berhasil diubah oleh Buyer"
        }
}