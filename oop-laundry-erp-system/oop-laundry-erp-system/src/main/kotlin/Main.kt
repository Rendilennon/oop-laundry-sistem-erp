package com.erp.laundry

import com.erp.laundry.models.*
import com.erp.laundry.repository.LaundryRepository
import com.erp.laundry.routes.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import java.util.Scanner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.File

fun Application.module() {
    install(ContentNegotiation) { jackson() }
    routing {
        userRouting(); inventoryRouting(); serviceRouting(); transactionRouting(); reportRouting()
    }
}

// Global Scanner
val scanner = Scanner(System.`in`)

// ==========================================
// 🏛️ OOP CONCEPTS
// ==========================================

abstract class BaseMenu(val currentUser: User?) {
    abstract fun show()
    fun logout() { println("👋 Logout berhasil. Sampai jumpa!") }
    protected fun getDateNow() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

// ==========================================
// 👑 ADMIN MENU
// ==========================================
class AdminMenu(admin: User) : BaseMenu(admin) {

    override fun show() {
        println("✅ Login Sukses sebagai Admin: ${currentUser?.nama}")
        while (true) {
            println("\n--- DASHBOARD ADMIN (FULL AKSES) ---")
            println("1. Kelola User")
            println("2. Kelola Inventory (Gudang)")
            println("3. Kelola Layanan (Harga)")
            println("4. Kelola Transaksi & Status")
            println("5. Laporan")
            println("0. Logout")
            print("Admin > ")

            when (scanner.nextLine()) {
                "1" -> manageUser()
                "2" -> manageInventory()
                "3" -> manageService()
                "4" -> manageTransaction()
                "5" -> generateReport()
                "0" -> { logout(); return }
                else -> println("❌ Menu tidak valid")
            }
        }
    }

    private fun manageUser() {
        println("\n--- KELOLA USER ---")
        println("1. Tambah User Baru")
        println("2. Hapus User")
        println("0. Kembali")
        print("Pilih > ")
        when(scanner.nextLine()) {
            "1" -> {
                print("Nama: "); val nama = scanner.nextLine()
                print("No HP: "); val hp = scanner.nextLine()
                print("Alamat: "); val addr = scanner.nextLine()
                print("Username: "); val user = scanner.nextLine()
                print("Password: "); val pass = scanner.nextLine()
                LaundryRepository.addUser(User(LaundryRepository.nextUserId(), nama, hp, user, pass, "user", addr))
                println("✅ User ditambahkan.")
            }
            "2" -> {
                print("Username dihapus: "); val target = scanner.nextLine()
                val user = LaundryRepository.findUserByUsername(target)
                if(user != null && user.role != "admin") {
                    LaundryRepository.removeUser(user)
                    println("✅ User dihapus.")
                } else println("❌ Gagal hapus (User tidak ketemu/Admin).")
            }
            "0" -> return
        }
    }

    private fun manageInventory() {
        while(true) {
            println("\n--- 📦 KELOLA INVENTORY ---")
            LaundryRepository.getInventory().forEach {
                println("[${it.id}] ${it.namaBarang} | Stok: ${it.qty} ${it.satuan}")
            }
            println("---------------------------")
            println("1. Tambah Barang Baru")
            println("2. Edit Stok/Nama")
            println("3. Hapus Barang")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    print("Nama Barang: "); val nama = scanner.nextLine()
                    print("Stok Awal: "); val qty = scanner.nextLine().toIntOrNull() ?: 0
                    print("Satuan: "); val unit = scanner.nextLine()
                    LaundryRepository.addInventory(Inventory(LaundryRepository.nextInvId(), nama, qty, unit))
                    println("✅ Barang berhasil ditambahkan.")
                }
                "2" -> {
                    print("ID Barang yg diedit: "); val id = scanner.nextLine().toIntOrNull() ?: 0
                    val item = LaundryRepository.findInventoryById(id)
                    if(item != null) {
                        print("Nama Baru (Enter skip): "); val nNama = scanner.nextLine()
                        print("Tambah Stok (cth: 5 atau -2): "); val addQty = scanner.nextLine().toIntOrNull() ?: 0
                        item.qty += addQty
                        println("✅ Stok diupdate.")
                    } else println("❌ Barang tidak ditemukan.")
                }
                "3" -> {
                    print("ID Barang dihapus: "); val id = scanner.nextLine().toIntOrNull() ?: 0
                    val item = LaundryRepository.findInventoryById(id)
                    if(item != null) {
                        LaundryRepository.removeInventory(item)
                        println("✅ Barang dihapus.")
                    }
                }
                "0" -> return
            }
        }
    }

    private fun manageService() {
        while(true) {
            println("\n--- 🛠 KELOLA LAYANAN ---")
            LaundryRepository.getServices().forEach {
                println("[${it.id}] ${it.namaLayanan} | Rp ${it.hargaPerKg}/kg")
            }
            println("-------------------------")
            println("1. Tambah Layanan Baru")
            println("2. Edit Layanan (Ubah Nama/Harga)")
            println("3. Hapus Layanan")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    print("Nama Layanan: "); val nama = scanner.nextLine()
                    print("Harga: "); val harga = scanner.nextLine().toLongOrNull() ?: 0
                    print("Estimasi (Hari): "); val hari = scanner.nextLine().toIntOrNull() ?: 1
                    LaundryRepository.addService(Service(LaundryRepository.nextSvcId(), nama, harga, hari))
                    println("✅ Layanan baru aktif.")
                }
                "2" -> {
                    print("ID Layanan: "); val id = scanner.nextLine().toIntOrNull() ?: 0
                    val svc = LaundryRepository.findServiceById(id)
                    if(svc != null) {
                        print("Nama Baru (Enter skip): "); val nNama = scanner.nextLine()
                        print("Harga Baru (Enter skip): "); val nHarga = scanner.nextLine().toLongOrNull()
                        if(nNama.isNotBlank()) svc.namaLayanan = nNama
                        if(nHarga != null) svc.hargaPerKg = nHarga
                        println("✅ Layanan berhasil diupdate!")
                    }
                }
                "3" -> {
                    print("ID Layanan dihapus: "); val id = scanner.nextLine().toIntOrNull() ?: 0
                    val svc = LaundryRepository.findServiceById(id)
                    if(svc != null) {
                        LaundryRepository.removeService(svc)
                        println("✅ Layanan dihapus.")
                    }
                }
                "0" -> return
            }
        }
    }

    // 🔥🔥 FITUR UPDATE STATUS DENGAN LIST TRANSAKSI 🔥🔥
    private fun manageTransaction() {
        while(true) {
            println("\n--- KASIR & STATUS CUCIAN ---")
            println("1. Proses Pesanan Masuk (Input Berat)")
            println("2. Update Status Cucian (Progress)")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    // Logic Input Berat
                    val pending = LaundryRepository.getTransactions().filter { it.berat == 0.0 }
                    if(pending.isEmpty()) { println("Tidak ada pesanan baru."); return }

                    pending.forEach { println("ID: ${it.id} | ${it.namaUser} | ${it.namaService}") }
                    print("ID Trx: "); val id = scanner.nextLine().toIntOrNull()
                    val trx = LaundryRepository.findTransactionById(id ?: 0)

                    if(trx != null) {
                        val svc = LaundryRepository.findServiceById(trx.idService)!!
                        print("Berat (Kg): "); val berat = scanner.nextLine().toDoubleOrNull() ?: 0.0
                        trx.berat = berat
                        trx.totalHarga = (berat * svc.hargaPerKg).toLong()
                        trx.status = "Diproses"
                        println("✅ Berat diinput. Total Tagihan: Rp ${trx.totalHarga}")
                    }
                }
                "2" -> {
                    // Logic Update Status Cucian
                    println("\n--- UPDATE STATUS ---")

                    // 👇 TAMPILKAN LIST TRANSAKSI DI SINI
                    val allTrx = LaundryRepository.getTransactions()
                    if (allTrx.isEmpty()) {
                        println("Belum ada data transaksi.")
                        return
                    }

                    println("Daftar Transaksi:")
                    allTrx.forEach {
                        println("ID: ${it.id} | ${it.namaUser} | ${it.namaService} [${it.status}]")
                    }
                    println("-------------------------")

                    print("Masukkan ID Transaksi: "); val id = scanner.nextLine().toIntOrNull()
                    val trx = LaundryRepository.findTransactionById(id ?: 0)

                    if (trx != null) {
                        println("Status Saat Ini: ${trx.status}")
                        println("Pilih Status Baru:")
                        println("1. Sedang Dicuci")
                        println("2. Selesai (Siap Diambil)")
                        println("3. Sudah Diambil")
                        println("4. Custom (Ketik Sendiri)")
                        print("Pilih > ")

                        val input = scanner.nextLine()
                        val newStatus = when(input) {
                            "1" -> "Sedang Dicuci"
                            "2" -> "Selesai"
                            "3" -> "Sudah Diambil"
                            "4" -> {
                                print("Ketik status baru: ")
                                scanner.nextLine()
                            }
                            else -> null
                        }

                        if (newStatus != null) {
                            trx.status = newStatus
                            println("✅ Status transaksi #${trx.id} diubah menjadi: '$newStatus'")
                        } else {
                            println("❌ Batal ubah status.")
                        }
                    } else {
                        println("❌ Transaksi tidak ditemukan.")
                    }
                }
                "0" -> return
            }
        }
    }

    private fun generateReport() {
        println("\n--- 📊 GENERATE LAPORAN ---")
        if (LaundryRepository.getTransactions().isEmpty()) {
            println("❌ Belum ada data transaksi.")
            return
        }

        val timestamp = getDateNow().replace("/", "-")
        val fileName = "Laporan_Laundry_$timestamp.csv"
        val file = File(fileName)

        try {
            file.bufferedWriter().use { out ->
                out.write("ID,TANGGAL,PELANGGAN,LAYANAN,BERAT(KG),TOTAL(RP),STATUS,PEMBAYARAN\n")
                var totalOmset = 0L
                LaundryRepository.getTransactions().forEach { trx ->
                    val statusBayar = if(trx.isPaid) "LUNAS" else "BELUM BAYAR"
                    out.write("${trx.id},${trx.tanggalMasuk},${trx.namaUser},${trx.namaService},${trx.berat},${trx.totalHarga},${trx.status},$statusBayar\n")
                    totalOmset += trx.totalHarga
                }
                out.write(",,,,TOTAL PENDAPATAN,${totalOmset},,\n")
            }
            println("✅ SUKSES! File: $fileName")
        } catch (e: Exception) {
            println("❌ Gagal: ${e.message}")
        }
    }
}

// ==========================================
// 👤 CUSTOMER MENU
// ==========================================
class CustomerMenu(customer: User) : BaseMenu(customer) {

    override fun show() {
        println("👋 Selamat Datang, ${currentUser?.nama}!")
        while(true) {
            println("\n--- MENU PELANGGAN (OOP) ---")
            println("1. Buat Pesanan (Order)")
            println("2. Cek Status & Tagihan")
            println("3. Bayar Tagihan 💲")
            println("0. Logout")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> makeOrder()
                "2" -> checkStatus()
                "3" -> payBill()
                "0" -> { logout(); return }
                else -> println("❌ Menu tidak valid")
            }
        }
    }

    private fun makeOrder() {
        println("\n--- PILIH LAYANAN ---")
        LaundryRepository.getServices().forEach {
            println("${it.id}. ${it.namaLayanan} (Rp ${it.hargaPerKg}/kg)")
        }
        print("Pilih ID > "); val sid = scanner.nextLine().toIntOrNull()
        val svc = LaundryRepository.findServiceById(sid ?: 0)

        if(svc != null) {
            println("Alamat Jemput: ${currentUser?.alamat}")
            print("Ganti alamat? (y/n): "); val ganti = scanner.nextLine()
            if(ganti == "y") {
                print("Alamat Baru: "); currentUser?.alamat = scanner.nextLine()
            }

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val estDate = LocalDateTime.now().plusDays(svc.estimasiHari.toLong()).format(formatter)

            LaundryRepository.addTransaction(Transaction(
                id = LaundryRepository.nextTrxId(),
                idUser = currentUser!!.id,
                namaUser = currentUser.nama,
                idService = svc.id,
                namaService = svc.namaLayanan,
                berat = 0.0, totalHarga = 0, status = "Menunggu Penjemputan",
                tanggalMasuk = getDateNow(),
                estimasiSelesai = estDate
            ))
            println("✅ Pesanan dibuat!")
        }
    }

    private fun checkStatus() {
        val myTrx = LaundryRepository.getTransactions().filter { it.idUser == currentUser?.id }
        if (myTrx.isEmpty()) println("Belum ada riwayat.")
        else {
            myTrx.forEach {
                val bayar = if(it.isPaid) "✅ LUNAS" else "❌ BELUM BAYAR"
                println("#${it.id} ${it.namaService}: ${it.status} | Rp ${it.totalHarga} ($bayar)")
            }
        }
    }

    private fun payBill() {
        val unpaidTrx = LaundryRepository.getTransactions().filter {
            it.idUser == currentUser?.id && !it.isPaid && it.totalHarga > 0
        }

        if (unpaidTrx.isEmpty()) {
            println("✅ Tidak ada tagihan aktif yang siap dibayar.")
            return
        }

        println("\n--- DAFTAR TAGIHAN ---")
        unpaidTrx.forEach {
            println("ID: ${it.id} | Rp ${it.totalHarga} | ${it.namaService}")
        }

        print("\nMasukkan ID Tagihan yang mau dibayar: ")
        val tid = scanner.nextLine().toIntOrNull()
        val trx = LaundryRepository.findTransactionById(tid ?: 0)

        if (trx != null && trx.idUser == currentUser?.id && !trx.isPaid) {
            val dummyInv = "INV/${LocalDateTime.now().year}/TRX-${trx.id}"
            println("\n========================================")
            println("           TAGIHAN LAUNDRY")
            println("========================================")
            println("No Invoice       : $dummyInv")
            println("Nama             : ${trx.namaUser}")
            println("Total            : Rp. ${trx.totalHarga}")
            println("\nSILAHKAN PILIH METODE PEMBAYARAN")
            println("1. VA (Virtual Account)")
            print("Pilih > ")

            if (scanner.nextLine() == "1") {
                val vaNumber = "3333${currentUser?.noHp}"
                println("\nNO Virtual Account ($vaNumber)")
                println("Simulasi: Masukkan No. Referensi / Bukti Transfer:")
                print("Input > ")

                val inputRef = scanner.nextLine()

                if (inputRef.isNotBlank()) {
                    trx.isPaid = true
                    trx.paymentMethod = "VA ($inputRef)"
                    trx.status = "Proses Cuci"
                    println("\n✅ PEMBAYARAN BERHASIL!")
                } else {
                    println("❌ Pembayaran dibatalkan.")
                }
            } else {
                println("❌ Metode belum tersedia.")
            }
        } else {
            println("❌ ID Tagihan salah.")
        }
    }
}

// ================= MAIN FUNCTION =================
fun main() {
    println("🚀 Menyalakan Server API di http://localhost:8081 ...")
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = false)

    while (true) {
        println("\n=========================================")
        println("   🧺 LAUNDRY SYSTEM (OOP EDITION) 🧺   ")
        println("=========================================")
        println("1. Login")
        println("2. Register")
        println("0. Exit")
        print("Pilih > ")

        when (scanner.nextLine()) {
            "1" -> {
                print("Username: "); val uname = scanner.nextLine()
                print("Password: "); val pass = scanner.nextLine()
                val user = LaundryRepository.findUserByUsername(uname)
                if (user != null && user.password == pass) {
                    val menu: BaseMenu = if (user.role == "admin") AdminMenu(user) else CustomerMenu(user)
                    menu.show()
                } else {
                    println("❌ Login Gagal.")
                }
            }
            "2" -> {
                println("Register User Baru...")
                print("Nama: "); val n = scanner.nextLine()
                print("User: "); val u = scanner.nextLine()
                print("Pass: "); val p = scanner.nextLine()
                LaundryRepository.addUser(User(LaundryRepository.nextUserId(), n, "-", u, p, "user", "-"))
                println("✅ Register Berhasil.")
            }
            "0" -> System.exit(0)
        }
    }
}