package com.raven.form;

import com.raven.swing.ModernScrollBarUI;
import config.DatabaseConfig;
import config.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import print.StrukManager;
import print.model.ParameterStruk;

public class Form_Transaksi extends javax.swing.JPanel {

    Connection con = null;
    DefaultTableModel tableModel;
    DefaultTableModel tableModelMenu;
    private static double totalBayar = 0;
    private static int stokGlobal = 0;
    private static double subTotal = 0;
    private String kodeMember = "";
    private boolean isMember = false;
    private String RFIDId = "";
    private long lastTime = 0;
    private final long RFID_THRESHOLD = 100;

    public Form_Transaksi() {
        initComponents();
        getCon();
        setModel();
        setOpaque(false);
        loadData();
        if (!isMember) {
            indikatorMember.setVisible(false);
            poin.setVisible(false);
        }
        clearAll();
        jScrollPane3.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setModel() {
        String[] judul = {"Kode Menu", "Nama Menu", "Jumlah", "Harga", "Harga Total", "Aksi"};
        String[] judulMenu = {"Nama Menu", "Stok"};
        tableModel = new DefaultTableModel(judul, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModelMenu = new DefaultTableModel(judulMenu, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPesanan.setModel(tableModel);
        tblMenu.setModel(tableModelMenu);
    }

    private void loadData() {
        try {
            StrukManager.getIntance().compileStruk();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (con != null) {
            try {
                String q = "SELECT nama_menu, jumlah FROM v_porsi_harian";
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String[] data = {rs.getString(1), rs.getString(2)};
                    tableModelMenu.addRow(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchData() {
        String kodeMenu = inputKode.getText();
        if (con != null) {
            try {
                String qCari = "SELECT * FROM v_porsi_harian WHERE kode_menu = ? LIMIT 1";
                PreparedStatement ps = con.prepareStatement(qCari);
                ps.setString(1, kodeMenu);
                ResultSet rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        inputQty.setText("1");
                        String namaMenu = rs.getString(2);
                        int stok = rs.getInt(4);
                        stokGlobal = rs.getInt(4);
                        int jumlah = Integer.parseInt(inputQty.getText());
                        double harga = Double.parseDouble(rs.getString(3));
                        double totalHarga = jumlah * harga;
                        inputMenu.setText(rs.getString("nama_menu"));
                        boolean cekKode = false;
                        boolean cekStok = false; //cek stok saat melakukan tambah pesanan
                        if (stok >= 0) {
                            //cek apakah ada kode menu yang sama
                            int row = tblPesanan.getRowCount();
                            for (int i = 0; i < row; i++) {
                                //jika ada akan menambahkan jumlah menu tersebut
                                int stokTabel = (int) tblPesanan.getValueAt(i, 2);
                                if (tblPesanan.getValueAt(i, 0).equals(kodeMenu)) {
                                    if (stokTabel < stok) {
                                        int jumlahBaru = Integer.parseInt(tblPesanan.getValueAt(i, 2).toString());
                                        tblPesanan.setValueAt(jumlahBaru + jumlah, i, 2);
                                        double total = (jumlahBaru + jumlah) * harga;
                                        tblPesanan.setValueAt(total, i, 4);
                                        cekKode = true;
                                        totalBayar += totalHarga;
                                        subTotal += totalHarga;
                                        break;
                                    } else {
                                        cekStok = true;
                                    }
                                }
                            }
                            //jika tidak ada menu yang sama, maka akan menambahkan baris baru
                            if (!cekKode) {
                                if (!cekStok) {
                                    tableModel.addRow(new Object[]{kodeMenu, namaMenu, jumlah, harga, totalHarga});
                                    totalBayar += totalHarga;
                                    subTotal += totalHarga;
                                } else {
                                    JOptionPane.showMessageDialog(null, "Stok tidak mencukupi!");
                                }
                            }
                            //set total
                            txtTotal.setText(String.valueOf(totalBayar));
                            inputSub.setText(String.valueOf(subTotal));
                        } else {
                            JOptionPane.showMessageDialog(null, "Stok Habis!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Menu Tidak Terdaftar!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAll() {
        totalBayar = 0;
        subTotal = 0;
        inputBayar.setText("0");
        inputKembalian.setText("");
        inputKode.setText("");
        inputQty.setText("");
        inputSub.setText("");
        txtTotal.setText("");
        tableModel.setRowCount(0);
        pakePoin.setSelected(false);
        poinField.setText("");
        indikatorMember.setVisible(false);
        poin.setVisible(false);
        member.setText("");
        inputMenu.setText("");
    }

    public static String generateKodeTransaksi() {
        Connection conn = DatabaseConfig.getConnection();
        String kodeTransaksi = "";

        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                String query = "SELECT kode_transaksi FROM transaksi ORDER BY kode_transaksi DESC LIMIT 1";
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    String lastKode = resultSet.getString("kode_transaksi");
                    int getYear = Calendar.getInstance().get(Calendar.YEAR);
                    int year = getYear % 100;
                    int kodeNum = Integer.parseInt(lastKode.substring(5)) + 1;
                    kodeTransaksi = String.format("TR" + year + "%04d", kodeNum);
                }

                resultSet.close();
                statement.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return kodeTransaksi;
    }

// LANGKAH 1: Ganti method prosesTransaksi() yang kosong dengan implementasi lengkap
    private void prosesTransaksi() {
        String kodeTransaksi, idAdmin, namaPelanggan;
        double point, bayar, kembalian, diskon = 0, dapatPoint = 0;
        boolean isMemberTransaction = false;
        boolean gunakanPoint = pakePoin.isSelected();
        tableModelMenu.setRowCount(0);

        // Validasi input
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada item dalam pesanan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (inputBayar.getText().isEmpty() || Double.parseDouble(inputBayar.getText()) < totalBayar) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran tidak cukup!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Inisialisasi data transaksi
        kodeTransaksi = generateKodeTransaksi();
        idAdmin = Session.getId();
        namaPelanggan = member.getText().trim();
        bayar = Double.parseDouble(inputBayar.getText());

        // Cek apakah menggunakan member
        if (!namaPelanggan.isEmpty() && isMember) {
            isMemberTransaction = true;
        }

        // Hitung point dan diskon
        if (isMemberTransaction) {
            if (gunakanPoint) {
                // Jika menggunakan point, set diskon dan point yang didapat = 0
                double pointSekarang = Double.parseDouble(poinField.getText());
                diskon = Math.floor(pointSekarang / 500) * 500; // Kelipatan 500 sebagai diskon
                dapatPoint = 0; // Tidak dapat point baru
            } else {
                // Jika tidak menggunakan point, set diskon = 0 dan hitung point baru
                diskon = 0;
                dapatPoint = totalBayar * 0.1; // 10% dari total transaksi
            }
        }

        // Hitung kembalian setelah diskon
        double totalSetelahDiskon = totalBayar - diskon;
        kembalian = bayar - totalSetelahDiskon;

        try {
            con.setAutoCommit(false); // Mulai transaction

            // 1. Cek ketersediaan stok semua menu
            if (!cekKetersediaanStok()) {
                con.rollback();
                return;
            }

            // 2. Insert ke tabel transaksi dengan kolom baru
            String queryTransaksi = "INSERT INTO transaksi (kode_transaksi, id_user, kode_member, tgl_transaksi, nama_pelanggan, total_transaksi, bayar, kembalian, diskon, dapat_point) VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)";
            PreparedStatement psTransaksi = con.prepareStatement(queryTransaksi);
            psTransaksi.setString(1, kodeTransaksi);
            psTransaksi.setString(2, idAdmin);

            if (isMemberTransaction) {
                // Ambil kode member dari database berdasarkan nama atau nomor telepon
                String kodeMemberDB = getKodeMemberFromDB(namaPelanggan);
                psTransaksi.setString(3, kodeMemberDB);
            } else {
                psTransaksi.setNull(3, java.sql.Types.VARCHAR);
            }

            psTransaksi.setString(4, namaPelanggan);
            psTransaksi.setDouble(5, totalBayar);
            psTransaksi.setDouble(6, bayar);
            psTransaksi.setDouble(7, kembalian);
            psTransaksi.setDouble(8, diskon);
            psTransaksi.setDouble(9, dapatPoint);
            psTransaksi.executeUpdate();

            // 3. Insert ke tabel detail_transaksi dan update stok
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String kodeMenu = (String) tableModel.getValueAt(i, 0);
                int jumlahPesan = (Integer) tableModel.getValueAt(i, 2);

                // Insert detail transaksi
                String queryDetail = "INSERT INTO detail_transaksi (kode_transaksi, kode_menu, jumlah) VALUES (?, ?, ?)";
                PreparedStatement psDetail = con.prepareStatement(queryDetail);
                psDetail.setString(1, kodeTransaksi);
                psDetail.setString(2, kodeMenu);
                psDetail.setInt(3, jumlahPesan);
                psDetail.executeUpdate();

                // Update porsi_harian
                updatePorsiHarian(kodeMenu, jumlahPesan);

                // Update bahan_baku berdasarkan detail_menu
                updateBahanBaku(kodeMenu, jumlahPesan);
            }

            // 4. Update point member
            if (isMemberTransaction) {
                updatePointMember(namaPelanggan, dapatPoint, gunakanPoint, diskon);
            }

            con.commit(); // Commit transaction

            // Tampilkan pesan sukses dan reset form
            String pesanSukses = "Transaksi berhasil!\n"
                    + "Kode Transaksi: " + kodeTransaksi + "\n"
                    + "Total: Rp " + totalBayar + "\n";

            if (diskon > 0) {
                pesanSukses += "Diskon Point: Rp " + diskon + "\n"
                        + "Total Setelah Diskon: Rp " + totalSetelahDiskon + "\n";
            }

            pesanSukses += "Bayar: Rp " + bayar + "\n"
                    + "Kembalian: Rp " + kembalian;

            if (isMemberTransaction) {
                if (gunakanPoint) {
                    pesanSukses += "\nPoint digunakan: " + diskon;
                } else {
                    pesanSukses += "\nPoint diperoleh: " + dapatPoint;
                }
            }

            JOptionPane.showMessageDialog(this, pesanSukses, "Sukses", JOptionPane.INFORMATION_MESSAGE);

            clearAll();
            loadData(); // Refresh data menu

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat memproses transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

// LANGKAH 2: Tambahkan method-method pendukung setelah method prosesTransaksi()
    private boolean cekKetersediaanStok() {
    try {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String kodeMenu = (String) tableModel.getValueAt(i, 0);
            int jumlahPesan = (Integer) tableModel.getValueAt(i, 2);

            // Debug: Print kode menu yang dicari
            System.out.println("Mengecek stok untuk kode menu: " + kodeMenu);

            // PERBAIKAN: Gunakan view v_porsi_harian yang sama dengan loadData()
            String queryStok = "SELECT jumlah FROM v_porsi_harian WHERE kode_menu = ?";
            PreparedStatement psStok = con.prepareStatement(queryStok);
            psStok.setString(1, kodeMenu);
            ResultSet rsStok = psStok.executeQuery();

            if (rsStok.next()) {
                int stokTersedia = rsStok.getInt("jumlah");
                
                // Debug: Print stok yang ditemukan
                System.out.println("Stok tersedia untuk " + kodeMenu + ": " + stokTersedia);
                System.out.println("Jumlah pesanan: " + jumlahPesan);
                
                if (stokTersedia < jumlahPesan) {
                    JOptionPane.showMessageDialog(this,
                            "Stok tidak mencukupi untuk menu: " + tableModel.getValueAt(i, 1)
                            + "\nStok tersedia: " + stokTersedia
                            + "\nJumlah pesanan: " + jumlahPesan,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                System.out.println("Menu tidak ditemukan di v_porsi_harian: " + kodeMenu);
                JOptionPane.showMessageDialog(this, "Menu tidak ditemukan: " + kodeMenu, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Tutup resources
            rsStok.close();
            psStok.close();
        }
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error saat mengecek stok: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}


    private String getKodeMemberFromDB(String identifier) throws SQLException {
        // Cek apakah identifier adalah nomor telepon atau nama
        String query;
        if (isNumeric(new JTextField(identifier))) {
            query = "SELECT kode_member FROM member WHERE no_telp_member = ?";
        } else {
            query = "SELECT kode_member FROM member WHERE nama_member = ?";
        }

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, identifier);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("kode_member");
        }
        throw new SQLException("Member tidak ditemukan: " + identifier);
    }

    private void updatePorsiHarian(String kodeMenu, int jumlahPesan) throws SQLException {
        String queryUpdate = "UPDATE porsi_harian SET jumlah = jumlah - ? WHERE kode_menu = ?";
        PreparedStatement psUpdate = con.prepareStatement(queryUpdate);
        psUpdate.setInt(1, jumlahPesan);
        psUpdate.setString(2, kodeMenu);
        int rowsAffected = psUpdate.executeUpdate();

        if (rowsAffected == 0) {
            throw new SQLException("Gagal update porsi_harian untuk menu: " + kodeMenu);
        }
    }

    private void updateBahanBaku(String kodeMenu, int jumlahPesan) throws SQLException {
        // Ambil semua bahan baku yang dibutuhkan untuk menu ini
        String queryBahan = "SELECT kode_bahanbaku, jumlah FROM detail_menu WHERE kode_menu = ?";
        PreparedStatement psBahan = con.prepareStatement(queryBahan);
        psBahan.setString(1, kodeMenu);
        ResultSet rsBahan = psBahan.executeQuery();

        while (rsBahan.next()) {
            String kodeBahanBaku = rsBahan.getString("kode_bahanbaku");
            int jumlahPerMenu = rsBahan.getInt("jumlah");
            int totalBahanDibutuhkan = jumlahPerMenu * jumlahPesan;

            // Update stok bahan baku
            String queryUpdateBahan = "UPDATE bahanbaku SET stok_bahanbaku = stok_bahanbaku - ? WHERE kode_bahanbaku = ?";
            PreparedStatement psUpdateBahan = con.prepareStatement(queryUpdateBahan);
            psUpdateBahan.setInt(1, totalBahanDibutuhkan);
            psUpdateBahan.setString(2, kodeBahanBaku);

            int rowsAffected = psUpdateBahan.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Gagal update bahan baku: " + kodeBahanBaku);
            }
        }
    }

    private void updatePointMember(String namaPelanggan, double pointBaru, boolean gunakanPoint, double pointDigunakan) throws SQLException {
        String kodeMember = getKodeMemberFromDB(namaPelanggan);

        if (gunakanPoint) {
            // Kurangi point yang digunakan
            double pointSekarang = Double.parseDouble(poinField.getText());
            double sisaPoint = pointSekarang - pointDigunakan;

            String queryUpdatePoint = "UPDATE member SET point = ? WHERE kode_member = ?";
            PreparedStatement psUpdatePoint = con.prepareStatement(queryUpdatePoint);
            psUpdatePoint.setDouble(1, sisaPoint);
            psUpdatePoint.setString(2, kodeMember);
            psUpdatePoint.executeUpdate();
        } else {
            // Tambah point baru
            String queryUpdatePoint = "UPDATE member SET point = point + ? WHERE kode_member = ?";
            PreparedStatement psUpdatePoint = con.prepareStatement(queryUpdatePoint);
            psUpdatePoint.setDouble(1, pointBaru);
            psUpdatePoint.setString(2, kodeMember);
            psUpdatePoint.executeUpdate();
        }
    }

    private boolean validasiPembayaran() {
        try {
            if (inputBayar.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            double bayar = Double.parseDouble(inputBayar.getText());

            // Hitung total setelah diskon jika ada
            double totalSetelahDiskon = totalBayar;
            if (pakePoin.isSelected() && isMember && !member.getText().trim().isEmpty()) {
                double pointSekarang = Double.parseDouble(poinField.getText());
                double diskon = Math.floor(pointSekarang / 500) * 500;
                totalSetelahDiskon = totalBayar - diskon;
            }

            if (bayar < totalSetelahDiskon) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah pembayaran kurang!\n"
                        + "Total: Rp " + totalBayar + "\n"
                        + (totalSetelahDiskon != totalBayar ? "Total Setelah Diskon: Rp " + totalSetelahDiskon + "\n" : "")
                        + "Bayar: Rp " + bayar,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format pembayaran tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void printStruk() {
        if (con != null) {
            try {
                String query = "SELECT nama_user, tanggal_transaksi, kode_transaksi, nama_pelanggan, total_transaksi, menggunakan_member, diskon, nominal_bayar, kembalian, point_didapat FROM `v_cetak_struk` LIMIT 1; ";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ParameterStruk dataPrint = new ParameterStruk(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10));
                    StrukManager.getIntance().printStruk(dataPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new com.raven.swing.PanelRound();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelRound2 = new com.raven.swing.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        inputKode = new com.raven.util.TextField();
        inputMenu = new com.raven.util.TextField();
        inputSub = new com.raven.util.TextField();
        inputQty = new com.raven.util.TextField();
        jLabel14 = new javax.swing.JLabel();
        inputBayar = new com.raven.util.TextField();
        inputKembalian = new com.raven.util.TextField();
        btnBatal = new com.raven.util.Button();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblMenu = new com.raven.swing.TableColumn();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPesanan = new com.raven.swing.TableColumn();
        indikatorMember = new javax.swing.JLabel();
        member = new com.raven.util.TextField();
        poin = new javax.swing.JPanel();
        poinField = new com.raven.util.TextField();
        jLabel6 = new javax.swing.JLabel();
        pakePoin = new javax.swing.JCheckBox();
        btnSelesai = new com.raven.util.Button();
        jLabel1 = new javax.swing.JLabel();

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("KODE MENU");
        panelRound1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("NAMA PELANGGAN");
        panelRound1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 129, -1, -1));

        panelRound2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("TOTAL:");
        panelRound2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, 35));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel10.setText("Rp. 0");
        panelRound2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(593, 6, -1, 85));

        txtTotal.setFont(new java.awt.Font("Bahnschrift", 1, 64)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtTotal.setText("0");
        panelRound2.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 400, -1));

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 15, 490, 100));

        inputKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKodeActionPerformed(evt);
            }
        });
        inputKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputKodeKeyPressed(evt);
            }
        });
        panelRound1.add(inputKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 31, 244, -1));

        inputMenu.setEnabled(false);
        panelRound1.add(inputMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 88, 198, -1));

        inputSub.setEnabled(false);
        panelRound1.add(inputSub, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 150, -1));

        inputQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputQtyKeyPressed(evt);
            }
        });
        panelRound1.add(inputQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 88, 44, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("QTY");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 72, -1, -1));

        inputBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputBayarKeyPressed(evt);
            }
        });
        panelRound1.add(inputBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 500, 150, -1));

        inputKembalian.setFocusable(false);
        inputKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKembalianActionPerformed(evt);
            }
        });
        panelRound1.add(inputKembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 500, 150, -1));

        btnBatal.setBackground(new java.awt.Color(97, 131, 175));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("BATAL");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        panelRound1.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 500, 130, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("NAMA MENU");
        panelRound1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 72, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("SUBTOTAL");
        panelRound1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("BAYAR");
        panelRound1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 480, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("KEMBALIAN");
        panelRound1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 480, -1, -1));

        jScrollPane3.setBorder(null);

        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NAMA MENU", "STOK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblMenu);

        panelRound1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, 210, 320));

        jScrollPane2.setBorder(null);

        tblPesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "KODE", "NAMA MENU", "HARGA", "QTY", "SUBTOTAL", "AKSI"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPesanan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblPesanan);
        if (tblPesanan.getColumnModel().getColumnCount() > 0) {
            tblPesanan.getColumnModel().getColumn(1).setPreferredWidth(135);
            tblPesanan.getColumnModel().getColumn(3).setPreferredWidth(40);
        }

        panelRound1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 198, 530, 270));

        indikatorMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/person.png"))); // NOI18N
        indikatorMember.setToolTipText("Member");
        panelRound1.add(indikatorMember, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 157, -1, -1));

        member.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberActionPerformed(evt);
            }
        });
        member.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                memberKeyTyped(evt);
            }
        });
        panelRound1.add(member, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 150, 240, -1));

        poin.setBackground(new java.awt.Color(33, 53, 85));
        poin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        poinField.setEditable(false);
        poinField.setEnabled(false);
        poinField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                poinFieldActionPerformed(evt);
            }
        });
        poinField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                poinFieldKeyTyped(evt);
            }
        });
        poin.add(poinField, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 260, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("POIN");
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 0));
        poin.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pakePoin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pakePoin.setForeground(new java.awt.Color(255, 255, 255));
        pakePoin.setText("Gunakan Poin");
        pakePoin.setBorder(null);
        pakePoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pakePoinActionPerformed(evt);
            }
        });
        poin.add(pakePoin, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 100, -1));

        panelRound1.add(poin, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

        btnSelesai.setBackground(new java.awt.Color(97, 131, 175));
        btnSelesai.setForeground(new java.awt.Color(255, 255, 255));
        btnSelesai.setText("SELESAI");
        btnSelesai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelesaiActionPerformed(evt);
            }
        });
        panelRound1.add(btnSelesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 500, 130, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(33, 53, 85));
        jLabel1.setText("TRANSAKSI");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 780, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputKodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKodeKeyPressed
        if (evt.getKeyChar() == '\n') {
            searchData();
        }
    }//GEN-LAST:event_inputKodeKeyPressed

    private void memberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memberActionPerformed

    private void cariMember(boolean isRFID) {
        try {
            String query = "";
            if (isRFID) {
                query = "SELECT * FROM member WHERE uid = ? LIMIT 1";
            } else {
                query = "SELECT * FROM member WHERE no_telp_member = ? LIMIT 1";
            }
            String rfid = RFIDId;
            try (PreparedStatement ps = con.prepareStatement(query)) {
                if (isRFID) {
                    ps.setString(1, rfid);
                } else {
                    ps.setString(1, member.getText());
                }

                ResultSet hasil = ps.executeQuery();
                if (hasil.next()) {
                    member.setText(hasil.getString("nama_member"));
                    poinField.setText(hasil.getDouble("point") + "");
                    isMember = true;
                    indikatorMember.setVisible(true);
                    poin.setVisible(true);
                } else {
                    if (isRFID) {
                        JOptionPane.showMessageDialog(this, "RFID Tidak terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Nomor Tidak terdaftar!", "Error", JOptionPane.PLAIN_MESSAGE);
                    }
                    member.setText("");
                    poinField.setText("");
                    pakePoin.setSelected(false);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void memberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_memberKeyTyped
        long currentTime = System.currentTimeMillis();
        char c = evt.getKeyChar();

        // Reset RFIDId kalau inputnya lambat
        if (lastTime != 0 && (currentTime - lastTime) > RFID_THRESHOLD) {
            RFIDId = "";
        }

        RFIDId += c;
        lastTime = currentTime;

        if (c == '\n' || c == '\r') {
            RFIDId = RFIDId.replace("\n", "");
            RFIDId = RFIDId.replace("\r", "");
            kodeMember = member.getText();
            if (RFIDId.length() >= 9) {
                System.out.println("Scan RFID Terdeteksi: " + RFIDId);
                cariMember(true);
            } else {
                //                System.out.println("RFID tidak valid, panjang kurang dari 9 karakter.");
                if (isNumeric(member)) {
                    cariMember(false);
                } else {
                    pakePoin.setSelected(false);
                    poinField.setText("");
                    indikatorMember.setVisible(false);
                    poin.setVisible(false);
                }
            }
            // Kosongkan RFIDId setelah pemrosesan
            RFIDId = "";
            //userInput.setText("");
        }
    }//GEN-LAST:event_memberKeyTyped

    private void poinFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_poinFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_poinFieldActionPerformed

    private void poinFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_poinFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_poinFieldKeyTyped

    private void inputKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKodeActionPerformed

    private void pakePoinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pakePoinActionPerformed
        double point;

        point = Double.parseDouble(poinField.getText());
        double diskon = Math.floor(point / 500) * 500;
        if (pakePoin.isSelected()) {
            totalBayar -= diskon;
            txtTotal.setText(totalBayar + "");
        } else {
            totalBayar += diskon;
            txtTotal.setText(totalBayar + "");
        }
    }//GEN-LAST:event_pakePoinActionPerformed

    private void inputBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputBayarKeyPressed
        if (evt.getKeyChar() == '\n') {
            double bayar = Double.parseDouble(inputBayar.getText());
            if (bayar >= totalBayar) {
                double kembalian = bayar - totalBayar;
                inputKembalian.setText("" + kembalian);
            } else {
                JOptionPane.showMessageDialog(this, "Nominal tidak cukup!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_inputBayarKeyPressed

    private void inputKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKembalianActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        clearAll();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void inputQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputQtyKeyPressed

    }//GEN-LAST:event_inputQtyKeyPressed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        if (!validasiPembayaran()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Konfirmasi transaksi?\n"
                + "Total: Rp " + totalBayar + "\n"
                + "Bayar: Rp " + inputBayar.getText() + "\n"
                + "Kembalian: Rp " + inputKembalian.getText(),
                "Konfirmasi Transaksi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            prosesTransaksi();
            printStruk();
        }
    }//GEN-LAST:event_btnSelesaiActionPerformed

    public static boolean isNumeric(JTextField textField) {
        String text = textField.getText();
        if (text.isEmpty()) {
            return false; // or handle empty case as needed
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnSelesai;
    private javax.swing.JLabel indikatorMember;
    private com.raven.util.TextField inputBayar;
    private com.raven.util.TextField inputKembalian;
    private com.raven.util.TextField inputKode;
    private com.raven.util.TextField inputMenu;
    private com.raven.util.TextField inputQty;
    private com.raven.util.TextField inputSub;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.raven.util.TextField member;
    private javax.swing.JCheckBox pakePoin;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.PanelRound panelRound2;
    private javax.swing.JPanel poin;
    private com.raven.util.TextField poinField;
    private com.raven.swing.TableColumn tblMenu;
    private com.raven.swing.TableColumn tblPesanan;
    private javax.swing.JLabel txtTotal;
    // End of variables declaration//GEN-END:variables
}
