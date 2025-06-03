/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.popup;

import com.raven.swing.ModernScrollBarUI;
import config.DatabaseConfig;
import config.Session;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import raven.glasspanepopup.GlassPanePopup;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author MI TA
 */
public class OpnamePopup extends javax.swing.JPanel {

    Connection con = null;
    List<String> items;

    /**
     * Creates new form OpnamePopup
     */
    public OpnamePopup() {
        initComponents();
        getCon();
        loadBahanBaku();
        styling(cmboxBahanBaku, items);
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBahanBaku() {
        items = new ArrayList<String>();

        try {
            String q = "SELECT * FROM bahanbaku";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            cmboxBahanBaku.removeAllItems();
            cmboxBahanBaku.addItem("Pilih Bahan Baku");
            cmboxBahanBaku.setSelectedIndex(0);
            while (rs.next()) {
                String kodeBahanBaku = rs.getString("kode_bahanbaku");
                String namaBahanBaku = rs.getString("nama_bahanbaku");
                cmboxBahanBaku.addItem(kodeBahanBaku + " - " + namaBahanBaku);
                items.add(kodeBahanBaku + " - " + namaBahanBaku);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                if (combo.getItemCount() <= 0) {
                    return;
                }
                editor.setText(input); // keep the text
                combo.showPopup();
            }
        });
        combo.addActionListener(e -> {
            String selected = (String) combo.getSelectedItem();
            int selectedIndex = combo.getSelectedIndex();
            String[] selectedSplit = selected.split("\\ ");
            if (selected != null && selectedIndex != 0) {
                getStok(selectedSplit[0]);
            } else {
                inputStokSistem.setText("");
            }
        });
    }

    private void getStok(String kode_bahanbaku) {
        try {
            String q = "SELECT stok_bahanbaku FROM bahanbaku WHERE kode_bahanbaku = ? LIMIT 1";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, kode_bahanbaku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                inputStokSistem.setText(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOpnameStok() {
        try {
            // Validasi input
            if (cmboxBahanBaku.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Pilih bahan baku terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inputStokGudang.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Stok gudang harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inputKeterangan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keterangan harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil data dari form
            String selectedBahanBaku = (String) cmboxBahanBaku.getSelectedItem();
            String[] selectedSplit = selectedBahanBaku.split(" - ");
            String kodeBahanBaku = selectedSplit[0];
            System.out.println(selectedSplit[0] + " Proses");
            
            int stokSistem = Integer.parseInt(inputStokSistem.getText());
            int stokFisik = Integer.parseInt(inputStokGudang.getText());
            int selisih = stokSistem - stokFisik;
            String keterangan = inputKeterangan.getText().trim();

            // Ambil user ID dari session
            String userId = Session.getId();

            // Format tanggal sekarang
            LocalDateTime now = LocalDateTime.now();
            String tglOpname = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Begin transaction
            con.setAutoCommit(false);

            try {
                // 1. Insert data ke tabel opname_stok
                String insertOpnameQuery = "INSERT INTO opname_stok (kode_bahanbaku, id_user, tgl_opname, stok_sistem, stok_fisik, selisih, keterangan, waktu_input) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psInsert = con.prepareStatement(insertOpnameQuery);
                psInsert.setString(1, kodeBahanBaku);
                psInsert.setString(2, userId);
                psInsert.setString(3, tglOpname);
                psInsert.setInt(4, stokSistem);
                psInsert.setInt(5, stokFisik);
                psInsert.setInt(6, selisih);
                psInsert.setString(7, keterangan);
                psInsert.setTimestamp(8, Timestamp.valueOf(now));

                int insertResult = psInsert.executeUpdate();

                if (insertResult > 0) {
                    // 2. Update stok bahan baku (kurangi dengan selisih)
                    String updateStokQuery = "UPDATE bahanbaku SET stok_bahanbaku = stok_bahanbaku - ? WHERE kode_bahanbaku = ?";
                    PreparedStatement psUpdate = con.prepareStatement(updateStokQuery);
                    psUpdate.setInt(1, selisih);
                    psUpdate.setString(2, kodeBahanBaku);

                    int updateResult = psUpdate.executeUpdate();

                    if (updateResult > 0) {
                        // Commit transaction
                        con.commit();

                        JOptionPane.showMessageDialog(this,
                                "Data opname stok berhasil disimpan!\n"
                                + "Kode Bahan Baku: " + kodeBahanBaku + "\n"
                                + "Stok Sistem: " + stokSistem + "\n"
                                + "Stok Fisik: " + stokFisik + "\n"
                                + "Selisih: " + selisih + "\n"
                                + "Stok bahan baku telah diperbarui.",
                                "Sukses",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Reset form
                        resetForm();

                        // Close popup
                        GlassPanePopup.closePopupAll();

                    } else {
                        // Rollback jika update gagal
                        con.rollback();
                        JOptionPane.showMessageDialog(this, "Gagal mengupdate stok bahan baku!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Rollback jika insert gagal
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data opname stok!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                // Rollback on error
                con.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Set autocommit back to true
                con.setAutoCommit(true);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok gudang harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

// Fungsi untuk reset form
    private void resetForm() {
        cmboxBahanBaku.setSelectedIndex(0);
        inputStokSistem.setText("");
        inputStokGudang.setText("");
        inputKeterangan.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputFilter1 = new com.raven.util.InputFilter();
        panelRound1 = new com.raven.swing.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmboxBahanBaku = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        inputStokSistem = new com.raven.util.TextField();
        jLabel4 = new javax.swing.JLabel();
        inputStokGudang = new com.raven.util.TextField();
        jLabel5 = new javax.swing.JLabel();
        inputKeterangan = new com.raven.util.TextField();
        btnSimpan = new com.raven.util.Button();
        btnBatal = new com.raven.util.Button();

        setOpaque(false);

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("STOK OPNAME");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Kode Bahan Baku");

        cmboxBahanBaku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmboxBahanBakuMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Stok Sistem");

        inputStokSistem.setToolTipText("Stok Sistem");
        inputStokSistem.setEnabled(false);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Stok Gudang");

        inputStokGudang.setToolTipText("Stok Sistem");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Keterangan");

        inputKeterangan.setToolTipText("Stok Sistem");

        btnSimpan.setBackground(new java.awt.Color(97, 131, 175));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("SIMPAN");
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(208, 90, 90));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("BATAL");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmboxBahanBaku, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputStokSistem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(inputStokGudang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(inputKeterangan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(0, 0, 0)
                .addComponent(cmboxBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(inputStokSistem, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(inputStokGudang, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(inputKeterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        saveOpnameStok();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void cmboxBahanBakuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmboxBahanBakuMouseClicked

    }//GEN-LAST:event_cmboxBahanBakuMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnSimpan;
    private javax.swing.JComboBox<String> cmboxBahanBaku;
    private com.raven.util.InputFilter inputFilter1;
    private com.raven.util.TextField inputKeterangan;
    private com.raven.util.TextField inputStokGudang;
    private com.raven.util.TextField inputStokSistem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private com.raven.swing.PanelRound panelRound1;
    // End of variables declaration//GEN-END:variables
}
