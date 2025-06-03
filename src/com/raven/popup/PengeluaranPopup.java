/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.popup;

import config.DatabaseConfig;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import raven.glasspanepopup.GlassPanePopup;

public class PengeluaranPopup extends javax.swing.JPanel {

    private Connection con;
    private DefaultTableModel model;

    public PengeluaranPopup() {
        initComponents();
        getCon();
        initTable();
        loadBahanBaku();
        toggleInputMode();
        boxBahanBaku.setPopupVisible(false); // reset popup
        boxBahanBaku.setLightWeightPopupEnabled(false);
        
    }

      private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   private void initTable() {
        model = new DefaultTableModel(new Object[]{"No.", "Kode", "Nama Bahan Baku", "Jumlah", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBahan.setModel(model);
    }

    private void loadBahanBaku() {
        try {
            String sql = "SELECT kode_bahanbaku, nama_bahanbaku FROM bahanbaku";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boxBahanBaku.removeAllItems();
            while (rs.next()) {
                String kode = rs.getString("kode_bahanbaku");
                String nama = rs.getString("nama_bahanbaku");
                boxBahanBaku.addItem(kode + " - " + nama);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleInputMode() {
        boolean isBahanBaku = radioBahanBaku.isSelected();
        boxBahanBaku.setEnabled(isBahanBaku);
        tblBahan.setEnabled(isBahanBaku);
        btnTambah.setEnabled(isBahanBaku);
        btnBatal.setEnabled(isBahanBaku);
        txtNama.setEnabled(!isBahanBaku);
        txtJumlah.setEnabled(true);
        txtHarga.setEnabled(true);
    }

    private void clearTable() {
        model.setRowCount(0);
    }

    private void clearForm() {
        clearTable();
        txtNama.setText("");
        txtJumlah.setText("");
        txtHarga.setText("");
    }

    private void addBahanToTable() {
        String selectedItem = (String) boxBahanBaku.getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bahan baku terlebih dahulu.");
            return;
        }

        String[] parts = selectedItem.split(" - ");
        if (parts.length < 2) {
            JOptionPane.showMessageDialog(this, "Format item tidak valid.");
            return;
        }

        String kode = parts[0];
        String nama = parts[1];

        String jumlahText = txtJumlah.getText().trim();
        String hargaText = txtHarga.getText().trim();

        if (jumlahText.isEmpty() || hargaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah dan harga.");
            return;
        }

        try {
            double jumlah = Double.parseDouble(jumlahText);
            double harga = Double.parseDouble(hargaText);

            if (jumlah <= 0 || harga <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah dan harga harus lebih dari 0.");
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 1).toString().equals(kode)) {
                    JOptionPane.showMessageDialog(this, "Bahan baku sudah ditambahkan.");
                    return;
                }
            }

            int no = model.getRowCount() + 1;
            model.addRow(new Object[]{no, kode, nama, jumlah, harga});
            txtJumlah.setText("");
            txtHarga.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka.");
        }
    }

    private void saveData() {
        try {
            boolean isBahanBaku = radioBahanBaku.isSelected();
            int idUser = 1;
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            String kodePengeluaran = generateKodePengeluaran();
            double totalPengeluaran = 0;

            if (isBahanBaku && model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Minimal satu bahan baku harus ditambahkan.");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO pengeluaran (kode_pengeluaran, id_user, tgl_pengeluaran, total_pengeluaran) VALUES (?, ?, ?, ?)"
            );
            ps1.setString(1, kodePengeluaran);
            ps1.setInt(2, idUser);
            ps1.setDate(3, today);
            ps1.setDouble(4, 0);
            ps1.executeUpdate();

            if (isBahanBaku) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String kode = model.getValueAt(i, 1).toString();
                    String nama = model.getValueAt(i, 2).toString();
                    double jumlah = Double.parseDouble(model.getValueAt(i, 3).toString());
                    double harga = Double.parseDouble(model.getValueAt(i, 4).toString());
                    double total = jumlah * harga;
                    totalPengeluaran += total;

                    PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO detail_pengeluaran (kode_pengeluaran, kode_bahanbaku, nama_pengeluaran, jumlah, harga_satuan, total) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    ps2.setString(1, kodePengeluaran);
                    ps2.setString(2, kode);
                    ps2.setString(3, nama);
                    ps2.setDouble(4, jumlah);
                    ps2.setDouble(5, harga);
                    ps2.setDouble(6, total);
                    ps2.executeUpdate();

                    PreparedStatement updateStok = con.prepareStatement(
                        "UPDATE bahanbaku SET stok_bahanbaku = stok_bahanbaku + ? WHERE kode_bahanbaku = ?"
                    );
                    updateStok.setDouble(1, jumlah);
                    updateStok.setString(2, kode);
                    updateStok.executeUpdate();
                }
            } else {
                String nama = txtNama.getText().trim();
                String jumlahText = txtJumlah.getText().trim();
                String hargaText = txtHarga.getText().trim();

                if (nama.isEmpty() || jumlahText.isEmpty() || hargaText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lengkapi semua data pengeluaran lain-lain.");
                    return;
                }

                double jumlah = Double.parseDouble(jumlahText);
                double harga = Double.parseDouble(hargaText);
                if (jumlah <= 0 || harga <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah dan harga harus lebih dari 0.");
                    return;
                }

                double total = jumlah * harga;
                totalPengeluaran = total;

                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO detail_pengeluaran (kode_pengeluaran, nama_pengeluaran, jumlah, harga_satuan, total) VALUES (?, ?, ?, ?, ?)"
                );
                ps2.setString(1, kodePengeluaran);
                ps2.setString(2, nama);
                ps2.setDouble(3, jumlah);
                ps2.setDouble(4, harga);
                ps2.setDouble(5, total);
                ps2.executeUpdate();
            }

            PreparedStatement psUpdate = con.prepareStatement(
                "UPDATE pengeluaran SET total_pengeluaran = ? WHERE kode_pengeluaran = ?"
            );
            psUpdate.setDouble(1, totalPengeluaran);
            psUpdate.setString(2, kodePengeluaran);
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pengeluaran berhasil disimpan.");
            GlassPanePopup.closePopupLast();
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private String generateKodePengeluaran() {
        String kode = "PGL00001";
        try {
            String sql = "SELECT MAX(kode_pengeluaran) FROM pengeluaran";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String lastKode = rs.getString(1);
                int num = Integer.parseInt(lastKode.substring(3));
                num++;
                kode = String.format("PGL%05d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kode;
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
        jLabel1 = new javax.swing.JLabel();
        txtHarga = new com.raven.util.TextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtJumlah = new com.raven.util.TextField();
        radioLain = new javax.swing.JRadioButton();
        radioBahanBaku = new javax.swing.JRadioButton();
        button1 = new com.raven.util.Button();
        btnSubmit = new com.raven.util.Button();
        jLabel6 = new javax.swing.JLabel();
        txtNama = new com.raven.util.TextField();
        boxBahanBaku = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBahan = new com.raven.swing.TableColumn();
        btnTambah = new com.raven.util.Button();
        btnBatal = new com.raven.util.Button();

        setOpaque(false);

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TAMBAH PENGELUARAN");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtHarga.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Bahan Baku");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Harga");

        txtJumlah.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        radioLain.setForeground(new java.awt.Color(255, 255, 255));
        radioLain.setText("Lain - Lain");
        radioLain.setBorder(null);
        radioLain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioLainActionPerformed(evt);
            }
        });

        radioBahanBaku.setForeground(new java.awt.Color(255, 255, 255));
        radioBahanBaku.setSelected(true);
        radioBahanBaku.setText("Bahan Baku");
        radioBahanBaku.setBorder(null);
        radioBahanBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBahanBakuActionPerformed(evt);
            }
        });

        button1.setBackground(new java.awt.Color(97, 131, 175));
        button1.setForeground(new java.awt.Color(255, 255, 255));
        button1.setText("Batal");
        button1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
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

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Jumlah");

        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaActionPerformed(evt);
            }
        });

        boxBahanBaku.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
        jScrollPane1.setViewportView(tblBahan);

        btnTambah.setBackground(new java.awt.Color(97, 131, 175));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(97, 131, 175));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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
                .addGap(20, 20, 20)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addComponent(radioBahanBaku)
                                .addGap(18, 18, 18)
                                .addComponent(radioLain))
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(211, 211, 211)
                                .addComponent(jLabel4)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRound1Layout.createSequentialGroup()
                                .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelRound1Layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel6))
                                    .addGroup(panelRound1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelRound1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelRound1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(panelRound1Layout.createSequentialGroup()
                                        .addComponent(boxBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtJumlah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20))))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(23, 23, 23)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioLain)
                    .addComponent(radioBahanBaku))
                .addGap(12, 12, 12)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boxBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void radioLainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioLainActionPerformed
        toggleInputMode();
    }//GEN-LAST:event_radioLainActionPerformed

    private void radioBahanBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBahanBakuActionPerformed
        toggleInputMode();
    }//GEN-LAST:event_radioBahanBakuActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_button1ActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        saveData();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void txtNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addBahanToTable();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        clearTable();
    }//GEN-LAST:event_btnBatalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> boxBahanBaku;
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnSubmit;
    private com.raven.util.Button btnTambah;
    private com.raven.util.Button button1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.PanelRound panelRound1;
    private javax.swing.JRadioButton radioBahanBaku;
    private javax.swing.JRadioButton radioLain;
    private com.raven.swing.TableColumn tblBahan;
    private com.raven.util.TextField txtHarga;
    private com.raven.util.TextField txtJumlah;
    private com.raven.util.TextField txtNama;
    // End of variables declaration//GEN-END:variables
}
