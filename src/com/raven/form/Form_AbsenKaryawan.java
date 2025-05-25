/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author MI TA
 */
public class Form_AbsenKaryawan extends javax.swing.JPanel {

    private Connection conn;
    private Timer checkTimer;

    /**
     * Creates new form Form_AbsenKaryawan
     */
    public Form_AbsenKaryawan() {
        initComponents();
        initDatabase();
        setupInputListener();
        startPeriodicCheck();
        updateTitleBasedOnStatus();
    }

    private void initDatabase() {
        try {
            // Sesuaikan dengan konfigurasi database Anda
            String url = "jdbc:mysql://localhost:3306/db_kitchenshellnew";
            String username = "root"; // sesuaikan dengan username database Anda
            String password = ""; // sesuaikan dengan password database Anda
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + e.getMessage());
        }
    }

    private void setupInputListener() {
        inputRFID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rfid = inputRFID.getText().trim();
                if (!rfid.isEmpty()) {
                    prosesAbsen(rfid);
                }
            }
        });
    }

    private void prosesAbsen(String uid) {
        try {
            // Cek apakah user dengan UID tersebut ada
            int idUser = getIdUserByUID(uid);
            if (idUser == -1) {
                JOptionPane.showMessageDialog(this, "RFID tidak terdaftar!");
                inputRFID.setText("");
                return;
            }

            // Cek apakah sudah ada data absen hari ini
            LocalDateTime now = LocalDateTime.now();
            String todayDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            int idAbsen = getAbsenToday(idUser, todayDate);

            if (idAbsen == -1) {
                // Belum absen hari ini, buat data baru untuk absen masuk
                createNewAbsen(idUser, now);
            } else {
                // Sudah absen masuk, update untuk absen keluar
                updateAbsenKeluar(idAbsen, now);
            }

            inputRFID.setText("");
            updateTitleBasedOnStatus();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private int getIdUserByUID(String uid) throws SQLException {
        String query = "SELECT id_user FROM user WHERE uid = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, uid);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_user");
        }
        return -1;
    }

    private int getAbsenToday(int idUser, String date) throws SQLException {
        String query = "SELECT id_absensi FROM absensi WHERE id_user = ? AND tanggal = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, idUser);
        stmt.setString(2, date);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_absensi");
        }
        return -1;
    }

    private void createNewAbsen(int idUser, LocalDateTime now) throws SQLException {
        String insertQuery = "INSERT INTO absensi (id_user, tanggal, waktu_masuk, keterangan) VALUES (?, ?, ?, ?)";

        String tanggal = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String waktuMasuk = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Cek jam operasional
        String keterangan = getKeteranganMasuk(now.toLocalTime());

        PreparedStatement stmt = conn.prepareStatement(insertQuery);
        stmt.setInt(1, idUser);
        stmt.setString(2, tanggal);
        stmt.setString(3, waktuMasuk);
        stmt.setString(4, keterangan);

        int result = stmt.executeUpdate();
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Absen masuk berhasil!\nKeterangan: " + keterangan);
        }
    }

    private void updateAbsenKeluar(int idAbsen, LocalDateTime now) throws SQLException {
        String updateQuery = "UPDATE absensi SET waktu_keluar = ? WHERE id_absensi = ?";
        String waktuKeluar = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        PreparedStatement stmt = conn.prepareStatement(updateQuery);
        stmt.setString(1, waktuKeluar);
        stmt.setInt(2, idAbsen);

        int result = stmt.executeUpdate();
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Absen keluar berhasil!");
        }
    }

    private String getKeteranganMasuk(LocalTime waktuMasuk) throws SQLException {
        String query = "SELECT jam_masuk FROM jam_oprasional LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Time jamMasukOperasional = rs.getTime("jam_masuk");
            LocalTime jamOperasional = jamMasukOperasional.toLocalTime();

            if (waktuMasuk.isAfter(jamOperasional)) {
                long menitTelat = ChronoUnit.MINUTES.between(jamOperasional, waktuMasuk);
                return "Hadir dan telat " + menitTelat + " menit";
            } else {
                return "Hadir";
            }
        }
        return "Hadir";
    }

    private void startPeriodicCheck() {
        // Timer untuk mengecek setiap 1 menit apakah sudah waktunya menampilkan input lagi
        checkTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTitleBasedOnStatus();
            }
        });
        checkTimer.start();
    }

    private void updateTitleBasedOnStatus() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String todayDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Cek apakah sudah ada absen hari ini (semua user)
            boolean hasAbsenToday = checkAnyAbsenToday(todayDate);
            boolean shouldShowInput = shouldShowInputField(now.toLocalTime());

            if (hasAbsenToday && !shouldShowInput) {
                txtTitle.setText("Anda Sudah Melakukan Absen");
                inputRFID.setVisible(false);
            } else {
                txtTitle.setText("Anda Belum Absen");
                inputRFID.setVisible(true);
            }

        } catch (SQLException e) {
            System.err.println("Error updating title: " + e.getMessage());
        }
    }

    private boolean checkAnyAbsenToday(String date) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM absensi WHERE tanggal = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, date);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
        return false;
    }

    private boolean shouldShowInputField(LocalTime currentTime) throws SQLException {
        String query = "SELECT jam_keluar FROM jam_oprasional LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Time jamKeluarOperasional = rs.getTime("jam_keluar");
            LocalTime jamKeluar = jamKeluarOperasional.toLocalTime();

            // Tampilkan input jika sudah mencapai jam keluar
            return currentTime.isAfter(jamKeluar) || currentTime.equals(jamKeluar);
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtTitle = new javax.swing.JLabel();
        inputRFID = new com.raven.util.TextField();

        setMinimumSize(new java.awt.Dimension(770, 390));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        txtTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTitle.setText("Anda Belum Absen");
        add(txtTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 600, -1));
        add(inputRFID, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 180, 320, 50));
    }// </editor-fold>//GEN-END:initComponents
    // Method untuk cleanup ketika form ditutup

    public void cleanup() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.TextField inputRFID;
    private javax.swing.JLabel txtTitle;
    // End of variables declaration//GEN-END:variables
}
