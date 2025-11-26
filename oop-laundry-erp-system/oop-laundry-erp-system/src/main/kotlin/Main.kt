package com.erp.laundry

import com.erp.laundry.models.*
import com.erp.laundry.repository.LaundryRepository
import com.erp.laundry.routes.appRouting
import com.erp.laundry.routes.userRouting
import com.erp.laundry.routes.inventoryRouting
import com.erp.laundry.routes.serviceRouting
import com.erp.laundry.routes.transactionRouting
import com.erp.laundry.routes.reportRouting
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

fun main() {
    println("🚀 Menyalakan Server API di http://localhost:8081 ...")
    embeddedServer(Netty, port = 8081, module = Application::module).start(wait = false)

    val scanner = Scanner(System.`in`)

    // --- HELPER ---
    fun genUserId() = (LaundryRepository.userList.maxOfOrNull { it.id } ?: 0) + 1
    fun genInvId() = (LaundryRepository.inventoryList.maxOfOrNull { it.id } ?: 0) + 1
    fun genSvcId() = (LaundryRepository.serviceList.maxOfOrNull { it.id } ?: 0) + 1
    fun genTrxId() = (LaundryRepository.transactionList.maxOfOrNull { it.id } ?: 0) + 1
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    fun getDateNow() = LocalDateTime.now().format(formatter)
    fun getEstimasi(hari: Int) = LocalDateTime.now().plusDays(hari.toLong()).format(formatter)

    // ================= USER CRUD (Update Alamat) =================
    fun registerUser() {
        println("\n=== 📝 Registrasi Akun Baru ===")
        print("Nama Lengkap: "); val nama = scanner.nextLine()
        print("No HP: "); val noHp = scanner.nextLine()
        print("Alamat Lengkap: "); val alamat = scanner.nextLine() // 👈 Input Alamat
        print("Username: "); val user = scanner.nextLine()

        if (LaundryRepository.userList.any { it.username.equals(user, ignoreCase = true) }) {
            println("❌ Username sudah ada."); return
        }
        print("Password: "); val pass = scanner.nextLine()

        LaundryRepository.userList.add(User(genUserId(), nama, noHp, user, pass, "user", alamat))
        println("✅ Akun berhasil dibuat.")
    }

    fun adminRegisterUser() { registerUser() }
    fun editUser() { println("⚠️ Fitur Edit User Admin disembunyikan."); scanner.nextLine() }
    fun hapusUser() { println("⚠️ Fitur Hapus User Admin disembunyikan."); scanner.nextLine() }

    // ================= 1. KELOLA INVENTORY =================
    fun kelolaInventory() {
        println("⚠️ (Fitur Inventory sama seperti sebelumnya)")
        scanner.nextLine()
    }

    // ================= 2. KELOLA LAYANAN =================
    fun kelolaLayanan() {
        println("⚠️ (Fitur Layanan sama seperti sebelumnya)")
        scanner.nextLine()
    }

    // ================= 3. KELOLA TRANSAKSI (ADMIN: INPUT BERAT) =================
    fun kelolaTransaksi() {
        while(true) {
            println("\n--- 💰 KELOLA TRANSAKSI (ADMIN) ---")
            println("1. Proses Pesanan Masuk (Input Berat)") // 👈 Admin memproses order User
            println("2. Buat Transaksi Manual (Walk-in)")
            println("3. Update Status Cucian")
            println("0. Kembali")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> {
                    // Cari transaksi yg dibuat user (Berat = 0)
                    val pendingTrx = LaundryRepository.transactionList.filter { it.berat == 0.0 }

                    if(pendingTrx.isEmpty()) {
                        println("✅ Tidak ada pesanan baru yang perlu ditimbang.")
                    } else {
                        println("\n--- PESANAN MASUK (MENUNGGU DITIMBANG) ---")
                        pendingTrx.forEach {
                            println("ID: ${it.id} | User: ${it.namaUser} | Layanan: ${it.namaService} | Tgl: ${it.tanggalMasuk}")
                        }

                        print("\nMasukkan ID Transaksi untuk diproses: ")
                        val tid = scanner.nextLine().toIntOrNull()
                        val trx = pendingTrx.find { it.id == tid }

                        if (trx != null) {
                            // Ambil info harga layanan lagi
                            val svc = LaundryRepository.serviceList.find { it.id == trx.idService }
                            if(svc == null) return

                            println("Pesanan: ${trx.namaUser} - ${trx.namaService}")
                            print("Masukkan Berat (Kg): "); val berat = scanner.nextLine().toDoubleOrNull() ?: 0.0

                            // Update Data Transaksi
                            trx.berat = berat
                            trx.totalHarga = (berat * svc.hargaPerKg).toLong()
                            trx.status = "Diproses" // Status naik

                            println("✅ Berat diinput. Total Tagihan: Rp ${trx.totalHarga}")
                            println("Status diubah menjadi 'Diproses'. User bisa melihat tagihan sekarang.")
                        } else println("❌ ID Salah.")
                    }
                }
                "2" -> { println("Fitur Manual (Sama kayak sebelumnya)"); scanner.nextLine() } // Skip biar ringkas
                "3" -> {
                    print("Masukkan ID Transaksi: "); val tid = scanner.nextLine().toIntOrNull()
                    val trx = LaundryRepository.transactionList.find { it.id == tid }
                    if(trx != null) {
                        print("Status Baru (Disetrika/Selesai): "); val st = scanner.nextLine()
                        trx.status = st
                        println("✅ Status diupdate.")
                    }
                }
                "0" -> return
            }
        }
    }

    fun generateLaporan() { println("📊 Fitur Laporan."); scanner.nextLine() }

    // ================= 4. FITUR USER (ORDER & UPDATE ALAMAT) =================

    // Fungsi User Membuat Order
    fun buatPesananUser(user: User) {
        println("\n--- DAFTAR HARGA ---")
        LaundryRepository.serviceList.forEach {
            println("${it.id}. ${it.namaLayanan}: Rp ${it.hargaPerKg}/kg (${it.estimasiHari} hari)")
        }

        print("\nPilih Layanan (Nomor) > ")
        val sid = scanner.nextLine().toIntOrNull()
        val svc = LaundryRepository.serviceList.find { it.id == sid }

        if (svc == null) {
            println("❌ Layanan tidak ditemukan.")
            return
        }

        // --- VERIFIKASI ALAMAT ---
        println("\n--------------------------------")
        println("Alamat Jemput : ${user.alamat}")
        println("--------------------------------")
        println("Apakah akan ganti alamat?")
        println("1. Iya")
        println("2. Tidak (Lanjut)")
        print("Pilih > ")

        if (scanner.nextLine() == "1") {
            print("\nSilahkan masukkan alamat baru: ")
            val newAddr = scanner.nextLine()
            if (newAddr.isNotBlank()) {
                // Update alamat di object User
                user.alamat = newAddr
                // Update di Repository juga (cari indexnya)
                val idx = LaundryRepository.userList.indexOfFirst { it.id == user.id }
                if (idx != -1) {
                    LaundryRepository.userList[idx] = user.copy(alamat = newAddr)
                }
                println("✅ 1. Simpan") // Simulasi menu simpan
                println("✅ Alamat berhasil terupdate menjadi: $newAddr")
            } else {
                println("❌ Alamat gagal terupdate (kosong).")
                return
            }
        }

        // --- BUAT TRANSAKSI (DRAFT) ---
        // Berat = 0, Harga = 0 (Karena belum ditimbang admin)
        val tglMasuk = getDateNow()
        val tglSelesai = getEstimasi(svc.estimasiHari)

        LaundryRepository.transactionList.add(Transaction(
            id = genTrxId(),
            idUser = user.id,
            namaUser = user.nama,
            idService = svc.id,
            namaService = svc.namaLayanan,
            berat = 0.0, // Menunggu admin
            totalHarga = 0, // Menunggu admin
            status = "Menunggu Penjemputan & Penimbangan",
            tanggalMasuk = tglMasuk,
            estimasiSelesai = tglSelesai
        ))

        println("\n✅ Pesanan Berhasil Dibuat!")
        println("Kurir akan menjemput cucian di: ${user.alamat}")
        println("Mohon tunggu update harga setelah cucian ditimbang.")
    }

    fun bayarTagihan(user: User) {
        // Hanya tampilkan tagihan yang SUDAH DITIMBANG (TotalHarga > 0) dan BELUM BAYAR
        val unpaidTrx = LaundryRepository.transactionList.filter { it.idUser == user.id && !it.isPaid && it.totalHarga > 0 }

        if (unpaidTrx.isEmpty()) {
            println("✅ Tidak ada tagihan aktif.")
            return
        }

        println("\n========================================")
        println("           TAGIHAN LAUNDRY")
        println("========================================")
        // Ambil tagihan terakhir/pertama
        val trx = unpaidTrx.first()

        println("Nama             : ${trx.namaUser}")
        println("Berat            : ${trx.berat} kg")
        println("Total            : Rp. ${trx.totalHarga}")
        println("Tanggal Masuk    : ${trx.tanggalMasuk}")
        println("Estimasi Selesai : ${trx.estimasiSelesai}")
        println("\nSILAHKAN PILIH METODE PEMBAYARAN")
        println("1. VA (Virtual Account)")
        print("Pilih > ")

        if (scanner.nextLine() == "1") {
            println("\nNO Virtual Account (3333${user.noHp}) (DI COPY PASTE KE INVOICE)")
            print("\nINV : "); scanner.nextLine()
            println("\n[1] SELESAI")
            print("Konfirmasi > ")
            if(scanner.nextLine() == "1") {
                trx.isPaid = true
                trx.paymentMethod = "VA"
                println("\n✅ Pembayaran Selesai.")
            }
        }
    }

    // ================= BUYER MENU (SESUAI REQUEST) =================
    fun buyerMenu(user: User) {
        println("\n👋 Selamat Datang, ${user.nama}!")
        while(true) {
            println("\n--- MENU USER ---")
            println("1. Tambah transaksi (Buat Pesanan)") // 👈 Menu Baru
            println("2. Cek Status Cucian Saya")
            println("3. Bayar Tagihan")
            println("0. Logout")
            print("Pilih > ")

            when(scanner.nextLine()) {
                "1" -> buatPesananUser(user) // Masuk ke Flow Alamat
                "2" -> {
                    val myTrx = LaundryRepository.transactionList.filter { it.idUser == user.id }
                    if (myTrx.isEmpty()) println("Belum ada riwayat.")
                    else {
                        myTrx.forEach {
                            println("#${it.id} - ${it.namaService} (${it.status})")
                            if(it.berat == 0.0) println("   (Menunggu ditimbang...)")
                            else println("   Total: Rp ${it.totalHarga} (Lunas: ${it.isPaid})")
                        }
                    }
                }
                "3" -> bayarTagihan(user)
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
        } else { println("❌ Gagal.") }
    }

    // ================= ADMIN MENU =================
    fun adminMenu() {
        println("\n=== 🔐 Login Admin ===")
        print("Username: "); val user = scanner.nextLine()
        print("Password: "); val pass = scanner.nextLine()
        val isAdmin = LaundryRepository.userList.any { it.username == user && it.password == pass && it.role == "admin" }
        if (!isAdmin) { println("❌ Gagal."); return }

        println("✅ Login Sukses!")
        while (true) {
            println("\n--- DASHBOARD ADMIN ---")
            println("1. Kelola User")
            println("2. Kelola Inventory")
            println("3. Kelola Layanan")
            println("4. Kelola Transaksi (Input Berat/Kasir)") // 👈 Tempat proses pesanan user
            println("5. Laporan")
            println("0. Logout")
            print("Admin > ")

            when (scanner.nextLine()) {
                "1" -> adminRegisterUser()
                "2" -> kelolaInventory()
                "3" -> kelolaLayanan()
                "4" -> kelolaTransaksi()
                "5" -> generateLaporan()
                "0" -> return
            }
        }
    }

    // MAIN LOOP
    while (true) {
        println("\n=========================================")
        println("   LAUNDRY    ")
        println("=========================================")
        println("1. Login Admin")
        println("2. Login User")
        println("3. Register User Baru")
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