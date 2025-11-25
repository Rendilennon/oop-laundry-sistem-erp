package com.erp.laundry

import com.erp.laundry.models.*
import com.erp.laundry.repository.LaundryRepository
import com.erp.laundry.routes.appRouting
import com.erp.laundry.routes.userRouting
import com.erp.laundry.routes.inventoryRouting
import com.erp.laundry.routes.serviceRouting
import com.erp.laundry.routes.transactionRouting
import com.erp.laundry.routes.reportRouting
import com.erp.laundry.routes.userRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.jackson.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import java.util.Scanner
import java.time.LocalDateTime
import java.io.File

fun Application.module() {
    install(ContentNegotiation) { jackson() }
    routing {
        userRouting()
        inventoryRouting()
        serviceRouting()
        transactionRouting()
        reportRouting()
    }
}

fun main() {
    // 1. START SERVER
    println("🚀 Menyalakan Server API di http://localhost:8081 ...")
    embeddedServer(Netty, port = 8081, module = Application::module)
        .start(wait = false)

    // 2. START CLI
    val scanner = Scanner(System.`in`)

    // --- HELPER FUNCTIONS ---
    fun genUserId() = (LaundryRepository.userList.maxOfOrNull { it.id } ?: 0) + 1
    fun genInvId() = (LaundryRepository.inventoryList.maxOfOrNull { it.id } ?: 0) + 1
    fun genSvcId() = (LaundryRepository.serviceList.maxOfOrNull { it.id } ?: 0) + 1
    fun genTrxId() = (LaundryRepository.transactionList.maxOfOrNull { it.id } ?: 0) + 1
    fun getDateNow() = LocalDateTime.now().toString().take(16).replace("T", " ")

    // ================= FUNGSI USER CRUD =================
    fun registerUser() {
        println("\n=== 📝 Registrasi Akun Baru ===")
        print("Nama Lengkap: "); val nama = scanner.nextLine()
        print("No HP: "); val noHp = scanner.nextLine()
        print("Username: "); val user = scanner.nextLine()
        if (LaundryRepository.userList.any { it.username.equals(user, ignoreCase = true) }) {
            println("❌ Username sudah ada."); return
        }
        print("Password: "); val pass = scanner.nextLine()
        LaundryRepository.userList.add(User(genUserId(), nama, noHp, user, pass, role = "user"))
        println("✅ Akun berhasil dibuat.")
    }

    fun adminRegisterUser() {
        registerUser()
    }

    fun editUser() {
        println("\n=== ✏️ Edit User ===")
        print("Masukkan Username User yang mau diedit: ")
        val target = scanner.nextLine()
        val index = LaundryRepository.userList.indexOfFirst { it.username == target }

        if (index == -1) { println("❌ User tidak ditemukan."); return }

        val old = LaundryRepository.userList[index]
        println("User Ditemukan: ${old.nama} (Role: ${old.role})")
        print("Nama Baru (Enter utk skip): "); val nNama = scanner.nextLine()
        print("No HP Baru (Enter utk skip): "); val nHp = scanner.nextLine()
        print("Pass Baru (Enter utk skip): "); val nPass = scanner.nextLine()

        LaundryRepository.userList[index] = old.copy(
            nama = if (nNama.isBlank()) old.nama else nNama,
            noHp = if (nHp.isBlank()) old.noHp else nHp,
            password = if (nPass.isBlank()) old.password else nPass
        )
        println("✅ Data user diupdate.")
    }

    fun hapusUser() {
        print("Username yg mau dihapus: "); val target = scanner.nextLine()
        val targetUser = LaundryRepository.userList.find { it.username == target }

        if (targetUser == null) { println("❌ User tidak ditemukan."); return }
        if (targetUser.role == "admin") { println("❌ Tidak bisa menghapus Admin."); return }

        LaundryRepository.userList.remove(targetUser)
        println("✅ User dihapus.")
    }

    // ================= 1. KELOLA INVENTORY =================
    fun kelolaInventory() {
        while(true) {
            println("\n--- 📦 KELOLA INVENTORY ---")
            if (LaundryRepository.inventoryList.isEmpty()) println("(Gudang Kosong)")
            else LaundryRepository.inventoryList.forEach {
                println("[${it.id}] ${it.namaBarang} - Stok: ${it.qty} ${it.satuan}")
            }
            println("---------------------------")
            println("1. Tambah Stok (Restock)")
            println("2. Barang Baru")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    print("Masukkan ID Barang: "); val id = scanner.nextLine().toIntOrNull()
                    val item = LaundryRepository.inventoryList.find { it.id == id }
                    if(item != null) {
                        print("Tambah berapa ${item.satuan}?: "); val add = scanner.nextLine().toIntOrNull() ?: 0
                        item.qty += add
                        println("✅ Stok ditambah. Total: ${item.qty}")
                    } else println("❌ Barang tidak ditemukan.")
                }
                "2" -> {
                    print("Nama Barang: "); val nama = scanner.nextLine()
                    print("Stok Awal: "); val qty = scanner.nextLine().toIntOrNull() ?: 0
                    print("Satuan: "); val unit = scanner.nextLine()
                    LaundryRepository.inventoryList.add(Inventory(genInvId(), nama, qty, unit))
                    println("✅ Barang baru ditambahkan.")
                }
                "0" -> return
            }
        }
    }

    // ================= 2. KELOLA LAYANAN =================
    fun kelolaLayanan() {
        while(true) {
            println("\n--- 🛠 KELOLA LAYANAN ---")
            if (LaundryRepository.serviceList.isEmpty()) println("(Kosong)")
            else LaundryRepository.serviceList.forEach {
                println("[${it.id}] ${it.namaLayanan} - Rp ${it.hargaPerKg}/kg")
            }
            println("-------------------------")
            println("1. Tambah Layanan Baru")
            println("2. Update Harga")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    print("Nama Layanan: "); val nama = scanner.nextLine()
                    print("Harga per Kg: "); val harga = scanner.nextLine().toLongOrNull() ?: 0
                    print("Estimasi (hari): "); val hari = scanner.nextLine().toIntOrNull() ?: 1
                    LaundryRepository.serviceList.add(Service(genSvcId(), nama, harga, hari))
                    println("✅ Layanan ditambahkan.")
                }
                "2" -> {
                    print("ID Layanan: "); val id = scanner.nextLine().toIntOrNull()
                    val svc = LaundryRepository.serviceList.find { it.id == id }
                    if(svc != null) {
                        print("Harga Baru: "); val harga = scanner.nextLine().toLongOrNull() ?: svc.hargaPerKg
                        svc.hargaPerKg = harga
                        println("✅ Harga diupdate.")
                    }
                }
                "0" -> return
            }
        }
    }

    // ================= 3. KELOLA TRANSAKSI (KASIR) =================
    fun kelolaTransaksi() {
        while(true) {
            println("\n--- 💰 KELOLA TRANSAKSI ---")
            println("1. Buat Transaksi Baru (Kasir)")
            println("2. Lihat Riwayat Transaksi")
            println("3. Update Status Cucian")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    println("\n--- KASIR ---")
                    println("Pilih Pelanggan:")
                    LaundryRepository.userList.filter { it.role == "user" }.forEach { println("- ID: ${it.id} | ${it.nama}") }

                    print("ID User: "); val uid = scanner.nextLine().toIntOrNull()
                    val user = LaundryRepository.userList.find { it.id == uid }
                    if(user == null) { println("❌ User tidak ditemukan."); return }

                    println("\nPilih Layanan:")
                    LaundryRepository.serviceList.forEach { println("- ID: ${it.id} | ${it.namaLayanan} @ ${it.hargaPerKg}") }
                    print("ID Layanan: "); val sid = scanner.nextLine().toIntOrNull()
                    val svc = LaundryRepository.serviceList.find { it.id == sid }
                    if(svc == null) { println("❌ Layanan tidak ditemukan."); return }

                    print("Berat (Kg): "); val berat = scanner.nextLine().toDoubleOrNull() ?: 0.0
                    val total = (berat * svc.hargaPerKg).toLong()

                    println("Total: Rp $total. Simpan? (y/n)"); val cfm = scanner.nextLine()
                    if(cfm.lowercase() == "y") {
                        LaundryRepository.transactionList.add(Transaction(
                            genTrxId(), user.id, user.nama, svc.id, svc.namaLayanan, berat, total, "Diterima", getDateNow()
                        ))
                        println("✅ Transaksi Disimpan!")
                    }
                }
                "2" -> {
                    println("\n--- RIWAYAT ---")
                    LaundryRepository.transactionList.forEach {
                        println("#${it.id} [${it.tanggal}] ${it.namaUser} - ${it.status}")
                    }
                }
                "3" -> {
                    print("ID Transaksi: "); val tid = scanner.nextLine().toIntOrNull()
                    val trx = LaundryRepository.transactionList.find { it.id == tid }
                    if(trx != null) {
                        print("Status Baru (Dicuci/Selesai): "); val st = scanner.nextLine()
                        trx.status = st
                        println("✅ Status diupdate.")
                    } else println("❌ Tidak ditemukan.")
                }
                "0" -> return
            }
        }
    }

    // ================= 4. GENERATE LAPORAN (CSV EXCEL) =================
    fun generateLaporan() {
        println("\n--- 📊 GENERATE LAPORAN ---")

        // Cek data kosong
        if (LaundryRepository.transactionList.isEmpty()) {
            println("❌ Belum ada data transaksi untuk diekspor.")
            return
        }

        // Nama file otomatis pakai Tanggal-Jam biar unik
        // Contoh: Laporan_2024-05-20_14-30.csv
        val timestamp = getDateNow().replace(" ", "_").replace(":", "-")
        val fileName = "Laporan_Laundry_$timestamp.csv"
        val file = File(fileName)

        try {
            // Membuka file untuk ditulis
            file.bufferedWriter().use { out ->
                // 1. Tulis Header (Judul Kolom Excel)
                out.write("ID,TANGGAL,PELANGGAN,LAYANAN,BERAT(KG),TOTAL(RP),STATUS\n")

                // 2. Tulis Data Transaksi (Looping)
                var totalOmset = 0L
                LaundryRepository.transactionList.forEach { trx ->
                    out.write("${trx.id},${trx.tanggal},${trx.namaUser},${trx.namaService},${trx.berat},${trx.totalHarga},${trx.status}\n")
                    totalOmset += trx.totalHarga
                }

                // 3. Tulis Total Pendapatan di baris akhir
                out.write(",,,,TOTAL PENDAPATAN,${totalOmset},\n")
            }

            println("✅ SUKSES! File Laporan telah dibuat.")
            println("📂 Nama File: $fileName")
            println("👉 Silakan buka file tersebut menggunakan Microsoft Excel.")

        } catch (e: Exception) {
            println("❌ Gagal membuat file: ${e.message}")
        }
    }

    // ================= ADMIN MENU =================
    fun adminMenu() {
        println("\n=== 🔐 Login Admin ===")
        print("Username: "); val user = scanner.nextLine()
        print("Password: "); val pass = scanner.nextLine()

        val isAdmin = LaundryRepository.userList.any {
            it.username == user && it.password == pass && it.role == "admin"
        }

        if (!isAdmin) { println("❌ Login Gagal. Anda bukan admin."); return }

        println("✅ Login Sukses!")
        while (true) {
            println("\n--- DASHBOARD ADMIN ---")
            println("1. Kelola User (CRUD)")
            println("2. Kelola Inventory")
            println("3. Kelola Layanan")
            println("4. Kelola Transaksi (Kasir)")
            println("5. Laporan (Export Excel)")
            println("0. Logout")
            print("Admin > ")

            when (scanner.nextLine()) {
                "1" -> {
                    println("1. Add 2. Edit 3. Delete")
                    when(scanner.nextLine()) {
                        "1" -> adminRegisterUser()
                        "2" -> editUser()
                        "3" -> hapusUser()
                    }
                }
                "2" -> kelolaInventory()
                "3" -> kelolaLayanan()
                "4" -> kelolaTransaksi()
                "5" -> generateLaporan() // Panggil fungsi CSV
                "0" -> return
            }
        }
    }

    // ================= BUYER MENU =================
    fun buyerMenu(user: User) {
        println("\n👋 Selamat Datang, ${user.nama}!")
        while(true) {
            println("\n--- MENU PELANGGAN ---")
            println("1. Cek Daftar Harga")
            println("2. Cek Status Cucian Saya")
            println("0. Logout")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    println("\n--- DAFTAR HARGA ---")
                    LaundryRepository.serviceList.forEach {
                        println("- ${it.namaLayanan}: Rp ${it.hargaPerKg}/kg (${it.estimasiHari} hari)")
                    }
                }
                "2" -> {
                    println("\n--- RIWAYAT CUCIAN SAYA ---")
                    val myTrx = LaundryRepository.transactionList.filter { it.idUser == user.id }
                    if (myTrx.isEmpty()) println("Belum ada riwayat.")
                    else {
                        myTrx.forEach {
                            println("#${it.id} [${it.tanggal}] ${it.namaService} - ${it.status} (Rp ${it.totalHarga})")
                        }
                    }
                }
                "0" -> return
            }
        }
    }

    fun loginUser() {
        println("\n=== 👤 Login User ===")
        print("Username: "); val username = scanner.nextLine()
        print("Password: "); val password = scanner.nextLine()

        val authenticatedUser = LaundryRepository.userList.find {
            it.username == username && it.password == password && it.role == "user"
        }

        if (authenticatedUser != null) {
            println("✅ Login Berhasil!")
            buyerMenu(authenticatedUser)
        } else {
            println("❌ Login Gagal.")
        }
    }

    // ================= MAIN LOOP =================
    while (true) {
        println("\n=========================================")
        println("   🧺 LAUNDRY SYSTEM (CLI + API) 🧺     ")
        println("=========================================")
        println("1. Login Admin")
        println("2. Login User")
        println("3. Register")
        println("0. Exit")
        print("Pilih > ")
        when (scanner.nextLine()) {
            "1" -> adminMenu()
            "2" -> loginUser()
            "3" -> registerUser()
            "0" -> System.exit(0)
        }
    }
}