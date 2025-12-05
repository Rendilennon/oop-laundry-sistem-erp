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

// Global Scanner (Singleton pattern for input)
val scanner = Scanner(System.`in`)

// ==========================================
// 🏛️ OOP CONCEPTS: ABSTRACTION & POLYMORPHISM
// ==========================================

// Abstract Base Class untuk Menu
abstract class BaseMenu(val currentUser: User?) {
    // Abstract method: Setiap menu WAJIB punya fungsi ini, tapi isinya beda-beda
    abstract fun show()

    // Common method: Bisa dipakai semua anak kelas
    fun logout() {
        println("👋 Logout berhasil. Sampai jumpa!")
    }

    protected fun getDateNow() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

// Class Menu Admin (Mewarisi BaseMenu)
class AdminMenu(admin: User) : BaseMenu(admin) {

    override fun show() {
        println("✅ Login Sukses sebagai Admin: ${currentUser?.nama}")
        while (true) {
            println("\n--- DASHBOARD ADMIN (OOP) ---")
            println("1. Kelola User")
            println("2. Kelola Inventory")
            println("3. Kelola Layanan")
            println("4. Kelola Transaksi (Kasir)")
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

    // --- Encapsulated Admin Logics ---

    private fun manageUser() {
        println("\n--- KELOLA USER ---")
        println("1. Tambah User Baru")
        println("2. Hapus User")
        print("Pilih > ")
        when(scanner.nextLine()) {
            "1" -> {
                print("Nama: "); val nama = scanner.nextLine()
                print("No HP: "); val hp = scanner.nextLine()
                print("Alamat: "); val addr = scanner.nextLine()
                print("Username: "); val user = scanner.nextLine()
                print("Password: "); val pass = scanner.nextLine()

                // Akses Repository lewat method (Encapsulation)
                LaundryRepository.addUser(User(LaundryRepository.nextUserId(), nama, hp, user, pass, "user", addr))
                println("✅ User ditambahkan.")
            }
            "2" -> {
                print("Username dihapus: "); val target = scanner.nextLine()
                val user = LaundryRepository.findUserByUsername(target)
                if(user != null && user.role != "admin") {
                    LaundryRepository.removeUser(user)
                    println("✅ User dihapus.")
                } else println("❌ Gagal hapus.")
            }
        }
    }

    private fun manageInventory() {
        println("\n--- STOK GUDANG ---")
        LaundryRepository.getInventory().forEach {
            println("[${it.id}] ${it.namaBarang}: ${it.qty} ${it.satuan}")
        }
    }

    private fun manageService() {
        println("\n--- LAYANAN ---")
        LaundryRepository.getServices().forEach {
            println("[${it.id}] ${it.namaLayanan}: Rp ${it.hargaPerKg}")
        }
    }

    private fun manageTransaction() {
        // Logic Kasir Admin
        println("\n--- KASIR ---")
        println("1. Proses Pesanan (Input Berat)")
        print("Pilih > ")
        if(scanner.nextLine() == "1") {
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
                println("✅ Total: Rp ${trx.totalHarga}")
            }
        }
    }

    private fun generateReport() {
        println("Generate CSV Report...")
        // Logic CSV sama seperti sebelumnya, panggil lewat class ReportGenerator jika mau lebih OOP
    }
}

// Class Menu Customer (Mewarisi BaseMenu)
class CustomerMenu(customer: User) : BaseMenu(customer) {

    override fun show() {
        println("👋 Selamat Datang, ${currentUser?.nama}!")
        while(true) {
            println("\n--- MENU PELANGGAN (OOP) ---")
            println("1. Buat Pesanan (Order)")
            println("2. Cek Status & Tagihan")
            println("0. Logout")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> makeOrder()
                "2" -> checkStatus()
                "0" -> { logout(); return }
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
                // Update di repo sebenarnya perlu method khusus update, tapi karena reference object sama, ini work.
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
        myTrx.forEach {
            val bayar = if(it.isPaid) "LUNAS" else "BELUM BAYAR"
            println("#${it.id} ${it.namaService}: ${it.status} | Rp ${it.totalHarga} ($bayar)")
        }

        // Logic bayar bisa ditambahkan di sini (panggil method payBill)
    }
}

// ================= MAIN FUNCTION =================

fun main() {
    println("🚀 Menyalakan Server API di http://localhost:8081 ...")
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = false)

    // Logic Login Utama
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
                    // POLYMORPHISM IN ACTION!
                    // Kita tidak perlu if-else panjang di sini.
                    // Cukup instansiasi class Menu yang sesuai.
                    val menu: BaseMenu = if (user.role == "admin") {
                        AdminMenu(user)
                    } else {
                        CustomerMenu(user)
                    }

                    menu.show() // Panggil method yang sama, tapi perilaku beda
                } else {
                    println("❌ Login Gagal.")
                }
            }
            "2" -> {
                println("Register User Baru...")
                // Panggil logic register (sederhana)
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