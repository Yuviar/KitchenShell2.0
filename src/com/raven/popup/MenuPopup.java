/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.popup;

import com.raven.event.DataChangeListener;
import com.raven.swing.ModernScrollBarUI;
import config.DatabaseConfig;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;
import raven.glasspanepopup.GlassPanePopup;

/**
 *
 * @author rayag
 */
public class MenuPopup extends javax.swing.JPanel {

    private Connection con = null;
    private boolean isEditMode = false;
    private String editKodeMenu = null;
    private DataChangeListener dataChangeListener;
    private List<String> itemBahan, itemKateg;

    public MenuPopup() {
        initComponents();
        getCon();
        loadKategori();
        loadBahanBaku();
        styling(cmbBahan, itemBahan);
        styling(cmbKategori, itemKateg);
        prepareTambah(); // default
        cmbKategori.setLightWeightPopupEnabled(false);
        cmbBahan.setLightWeightPopupEnabled(false);
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Kode Bahan", "Nama Bahan", "Jumlah"}, 0
        );
        tblBahan.setModel(model);
        btnBatalResep.setVisible(false);
        tblBahan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tblBahan.getSelectedRow() != -1) {
                    btnBatalResep.setVisible(true);
                }
            }
        });

        tblBahan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnBatalResep.setVisible(tblBahan.getSelectedRow() != -1);
            }
        });

    }

    public void setMenuListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareTambah() {
        isEditMode = false;
        txtKodeMenu.setText(generateKodeMenu());
        txtKodeMenu.setEnabled(false);
    }

    public void setEditMode(String kodeMenu) {
        isEditMode = true;
        editKodeMenu = kodeMenu;

        try {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM menu WHERE kode_menu=?");
            pst.setString(1, kodeMenu);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtKodeMenu.setText(rs.getString("kode_menu"));
                txtNamaMenu.setText(rs.getString("nama_menu"));
                txtHargaBikin.setText(rs.getString("harga"));
                txtHargaBikin.setText(rs.getString("harga_bikin"));
                cmbKategori.setSelectedItem(rs.getString("kode_kategori"));
            }

            // Load detail bahan
            DefaultTableModel model = (DefaultTableModel) tblBahan.getModel();
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT * FROM detail_menu WHERE kode_menu=?");
            pst.setString(1, kodeMenu);
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_bahanbaku"),
                    getNamaBahanBaku(rs.getString("kode_bahanbaku")),
                    rs.getInt("jumlah")
                });
            }

            lblTitle.setText("EDIT MENU");
            btnSubmit.setText("Update");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            if (cmbKategori.getSelectedItem() == null) {
                return;
            }
            String kodeMenu = txtKodeMenu.getText().trim();
            String namaMenu = txtNamaMenu.getText().trim();
            String kodeKategori = cmbKategori.getSelectedItem().toString().split(" - ")[0];
            BigDecimal harga = new BigDecimal(txtHargaBikin.getText().trim());
            BigDecimal hargaBikin = new BigDecimal(txtHargaBikin.getText().trim());

            if (namaMenu.isEmpty()) {
                throw new Exception("Nama menu tidak boleh kosong!");
            }

            con.setAutoCommit(false);

            if (isEditMode) {
                PreparedStatement pst = con.prepareStatement(
                        "UPDATE menu SET kode_kategori=?, nama_menu=?, harga=?, harga_bikin=? WHERE kode_menu=?");
                pst.setString(1, kodeKategori);
                pst.setString(2, namaMenu);
                pst.setBigDecimal(3, harga);
                pst.setBigDecimal(4, hargaBikin);
                pst.setString(5, kodeMenu);
                pst.executeUpdate();

                pst = con.prepareStatement("DELETE FROM detail_menu WHERE kode_menu=?");
                pst.setString(1, kodeMenu);
                pst.executeUpdate();
            } else {
                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO menu (kode_menu, kode_kategori, nama_menu, harga, harga_bikin) VALUES (?, ?, ?, ?, ?)");
                pst.setString(1, kodeMenu);
                pst.setString(2, kodeKategori);
                pst.setString(3, namaMenu);
                pst.setBigDecimal(4, harga);
                pst.setBigDecimal(5, hargaBikin);
                pst.execute();
            }

            // Insert bahan baku
            for (int i = 0; i < tblBahan.getRowCount(); i++) {
                String kodeBahan = tblBahan.getValueAt(i, 0).toString();
                int jumlah = Integer.parseInt(tblBahan.getValueAt(i, 2).toString());

                PreparedStatement pst = con.prepareStatement(
                        "INSERT INTO detail_menu (kode_menu, kode_bahanbaku, jumlah) VALUES (?, ?, ?)");
                pst.setString(1, kodeMenu);
                pst.setString(2, kodeBahan);
                pst.setInt(3, jumlah);
                pst.execute();
            }

            con.commit();
            JOptionPane.showMessageDialog(this, (isEditMode ? "Menu berhasil diperbarui!" : "Menu berhasil ditambahkan!"));
            GlassPanePopup.closePopupLast();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void styling(JComboBox combo, List<String> items) {
        // Ganti font & warna
        // styling dasar
        combo.setFont(new Font("Arial", Font.BOLD, 14));
        combo.setForeground(new Color(50, 45, 20));
        combo.setBackground(Color.WHITE);
        combo.setOpaque(true);

        // override hanya arrow button & background area
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▼");
//                arrow.setBorder(BorderFactory.createEmptyBorder());
                arrow.setForeground(Color.gray);
                arrow.setBackground(new Color(255, 255, 255));
                arrow.setSize(34, 34);
                return arrow;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(combo.getBackground());
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);

                JScrollPane scrollPane = (JScrollPane) popup.getComponents()[0];
                JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();

                // Ubah style scrollbar
                vScrollBar.setUI(new ModernScrollBarUI());

                return popup;
            }
        });
        combo.setLightWeightPopupEnabled(false);

        combo.setEditable(true);

        JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
        editor.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        editor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = editor.getText();
                combo.hidePopup();
                combo.removeAllItems();

                for (String item : items) {
                    if (item.toLowerCase().contains(input.toLowerCase())) {
                        combo.addItem(item);
                    }
                }
                if(combo.getItemCount() <= 0)
                    return;
                editor.setText(input); // keep the text
                combo.showPopup();
            }
        });

    }

    private String generateKodeMenu() {
        try {
            String sql = "SELECT RIGHT(kode_menu, 3) AS nomor FROM menu ORDER BY kode_menu DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int nextNumber = 1;
            if (rs.next() && rs.getString("nomor") != null) {
                nextNumber = Integer.parseInt(rs.getString("nomor")) + 1;
            }

            return String.format("MNU%03d", nextNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return "MNU001";
        }
    }

    private String getNamaBahanBaku(String kode) throws SQLException {
        PreparedStatement pst = con.prepareStatement("SELECT nama_bahanbaku FROM bahanbaku WHERE kode_bahanbaku=?");
        pst.setString(1, kode);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        }
        return "";
    }

    private void loadBahanBaku() {
        try {
            itemBahan = new ArrayList<String>();
            PreparedStatement pst = con.prepareStatement("SELECT kode_bahanbaku, nama_bahanbaku FROM bahanbaku");
            ResultSet rs = pst.executeQuery();
            cmbBahan.removeAllItems();
            cmbBahan.addItem("-- Pilih Bahan Baku --");
            while (rs.next()) {
                String kode = rs.getString("kode_bahanbaku");
                String nama = rs.getString("nama_bahanbaku");
                cmbBahan.addItem(kode + " - " + nama); // atau hanya nama jika lebih ringkas
                itemBahan.add(kode + " - " + nama);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data bahan baku");
        }
    }

    private void loadKategori() {
        try {
            itemKateg = new ArrayList<String>();
            cmbKategori.addItem("--Pilih Kategori--");
            PreparedStatement pst = con.prepareStatement("SELECT kode_kategori, nama_kategori FROM kategori");
            ResultSet rs = pst.executeQuery();
            cmbKategori.removeAllItems();
            cmbKategori.addItem("-- Pilih Kategori --");
            while (rs.next()) {
                String kode = rs.getString("kode_kategori");
                String nama = rs.getString("nama_kategori");
                cmbKategori.addItem(kode + " - " + nama);
                itemKateg.add(kode + " - " + nama);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data kategori");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new com.raven.swing.PanelRound();
        lblTitle = new javax.swing.JLabel();
        txtNamaMenu = new com.raven.util.TextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnBatal = new com.raven.util.Button();
        btnSubmit = new com.raven.util.Button();
        txtKodeMenu = new com.raven.util.TextField();
        cmbBahan = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnTambah = new com.raven.util.Button();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtHargaBikin = new com.raven.util.TextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBahan = new com.raven.swing.TableColumn();
        cmbKategori = new javax.swing.JComboBox<>();
        btnBatalResep = new com.raven.util.Button();

        setOpaque(false);

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("TAMBAH MENU");
        lblTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        lblTitle.setFocusable(false);
        lblTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtNamaMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Kode Menu");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Kategori");

        btnBatal.setBackground(new java.awt.Color(97, 131, 175));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnSubmit.setBackground(new java.awt.Color(97, 131, 175));
        btnSubmit.setForeground(new java.awt.Color(255, 255, 255));
        btnSubmit.setText("Selesai");
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        txtKodeMenu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        cmbBahan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Beras", "Daging Ayam" }));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nama Menu");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Bahan Baku");

        btnTambah.setBackground(new java.awt.Color(97, 131, 175));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Resep :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Harga");

        txtHargaBikin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        tblBahan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblBahan);

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Beras", "Daging Ayam" }));

        btnBatalResep.setBackground(new java.awt.Color(97, 131, 175));
        btnBatalResep.setForeground(new java.awt.Color(255, 255, 255));
        btnBatalResep.setText("Batal");
        btnBatalResep.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatalResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalResepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(218, 218, 218)
                .addComponent(jLabel3))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(txtKodeMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(txtNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel4)
                .addGap(217, 217, 217)
                .addComponent(jLabel8))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(cmbBahan, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(txtHargaBikin, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel7)
                .addGap(321, 321, 321)
                .addComponent(btnBatalResep, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel6))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(390, 390, 390)
                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(12, 12, 12)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtKodeMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(cmbBahan, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtHargaBikin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel7))
                    .addComponent(btnBatalResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(0, 0, 0)
                .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        saveData();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        if (cmbBahan.getSelectedItem() == null) {
            return;
        }
        String selected = cmbBahan.getSelectedItem().toString(); // contoh: "BB001 - Gula"
        String kode = selected.split(" - ")[0];
        String nama = selected.split(" - ")[1];

        String jumlahStr = JOptionPane.showInputDialog(this, "Masukkan jumlah bahan:");
        if (jumlahStr != null && !jumlahStr.isEmpty()) {
            try {
                int jumlah = Integer.parseInt(jumlahStr);
                DefaultTableModel model = (DefaultTableModel) tblBahan.getModel();
                model.addRow(new Object[]{kode, nama, jumlah});
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka");
            }
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnBatalResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalResepActionPerformed
        int selectedRow = tblBahan.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin membatalkan bahan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) tblBahan.getModel();
                model.removeRow(selectedRow);

                // Jika tidak ada data lagi, sembunyikan tombol
                if (tblBahan.getRowCount() == 0) {
                    btnBatalResep.setVisible(false);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris bahan terlebih dahulu.");
        }
    }//GEN-LAST:event_btnBatalResepActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnBatalResep;
    private com.raven.util.Button btnSubmit;
    private com.raven.util.Button btnTambah;
    private javax.swing.JComboBox<String> cmbBahan;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTitle;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.TableColumn tblBahan;
    private com.raven.util.TextField txtHargaBikin;
    private com.raven.util.TextField txtKodeMenu;
    private com.raven.util.TextField txtNamaMenu;
    // End of variables declaration//GEN-END:variables
}
