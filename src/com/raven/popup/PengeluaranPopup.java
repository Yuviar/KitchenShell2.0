/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.popup;

import com.raven.event.DataChangeListener;
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
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import raven.glasspanepopup.GlassPanePopup;

public class PengeluaranPopup extends javax.swing.JPanel {

    private Connection con;
    private DefaultTableModel model;

    private DataChangeListener dataChangeListener;
    List<String> items;

    public PengeluaranPopup() {
        initComponents();
        getCon();
        initTable();
        loadBahanBaku();
        styling(boxBahanBaku, items);

        jScrollPane1.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOutListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
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

        SwingUtilities.invokeLater(() -> editor.requestFocusInWindow());

        editor.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                String input = editor.getText();
                combo.hidePopup();
                combo.removeAllItems();

                for (String item : items) {
                    if (item.toLowerCase().contains(input.toLowerCase())) {
                        combo.addItem(item);
                    }
                }
                editor.setText(input); // keep the text
                combo.showPopup();

                if (e.getKeyCode() == KeyEvent.VK_ENTER && combo.getItemCount() > 0) {
                    combo.setSelectedIndex(0); // Pilih item pertama
                    editor.setText(combo.getSelectedItem().toString()); // keep the text
                    combo.hidePopup();
                }
            }

        });

    }

    private void initTable() {
        model = new DefaultTableModel(new Object[]{"No.", "Nama Pengeluaran", "Jumlah", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBahan.setModel(model);

        // Atur lebar kolom
        TableColumnModel columnModel = tblBahan.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);   // No.
        columnModel.getColumn(1).setPreferredWidth(200);  // Nama Pengeluaran
        columnModel.getColumn(2).setPreferredWidth(70);   // Jumlah
        columnModel.getColumn(3).setPreferredWidth(100);  // Harga
    }

    private void loadBahanBaku() {
        try {
            items = new ArrayList<String>();
            String sql = "SELECT kode_bahanbaku, nama_bahanbaku FROM bahanbaku ORDER BY kode_bahanbaku ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boxBahanBaku.removeAllItems();
            while (rs.next()) {
                String kode = rs.getString("kode_bahanbaku");
                String nama = rs.getString("nama_bahanbaku");
                boxBahanBaku.addItem(kode + " - " + nama);
                items.add(kode + " - " + nama);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTable() {
        model.setRowCount(0);
    }

    private void clearForm() {
        clearTable();
        txtJumlah.setText("");
        txtHarga.setText("");
    }

    private void addToTable() {
        String selectedItem = (String) boxBahanBaku.getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bahan baku terlebih dahulu.");
            return;
        }

        String jumlahText = txtJumlah.getText().trim();
        String hargaText = txtHarga.getText().trim();

        if (jumlahText.isEmpty() || hargaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah dan harga.");
            return;
        }
        boolean cekKode = false;

        try {
            int jumlah = Integer.parseInt(jumlahText);
            double harga = Double.parseDouble(hargaText);

            if (jumlah <= 0 || harga <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah dan harga harus lebih dari 0.");
                return;
            }

            // cek bahan
            if (selectedItem.contains("BB")) {
                boolean adaBahan = false;
                String[] kode = selectedItem.split(" - ");
                String query = "SELECT * FROM `bahanbaku` WHERE kode_bahanbaku = ? LIMIT 1";
                PreparedStatement st = con.prepareStatement(query);
                st.setString(1, kode[0]);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    adaBahan = true;
                }
                if (!adaBahan) {
                    JOptionPane.showMessageDialog(this, "Bahan tidak terdaftar!");
                    return;
                }

            }

            int row = tblBahan.getRowCount();
            for (int i = 0; i < row; i++) {
                //jika ada akan menambahkan jumlah menu tersebut
                if (tblBahan.getValueAt(i, 1).toString().equals(selectedItem)) {
                    int jumlahTabel = Integer.parseInt(tblBahan.getValueAt(i, 2).toString());
                    tblBahan.setValueAt(jumlahTabel + jumlah, i, 2);

                    cekKode = true;
                    break;
                }
            }

            int no = model.getRowCount() + 1;
            if (!cekKode) {
                model.addRow(new Object[]{no, selectedItem, jumlah, harga});
            }
            txtJumlah.setText("");
            txtHarga.setText("");
            boxBahanBaku.setSelectedItem("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka.");
        } catch (SQLException ex) {
            Logger.getLogger(PengeluaranPopup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveData() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada item pada pengeluaran!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            con.setAutoCommit(false); // Mulai transaction

            int idUser = Integer.parseInt(Session.getId());
            String kodePengeluaran = generateKodePengeluaran();
            double totalPengeluaran = 0;

            PreparedStatement ps1 = con.prepareStatement(
                    "INSERT INTO pengeluaran (kode_pengeluaran, id_user, tgl_pengeluaran, total_pengeluaran) VALUES (?, ?, NOW(), ?)"
            );
            ps1.setString(1, kodePengeluaran);
            ps1.setInt(2, idUser);
            ps1.setDouble(3, 0);
            ps1.executeUpdate();

            for (int i = 0; i < model.getRowCount(); i++) {
                String kodeMenu = null;
                String pengeluaran = (String) model.getValueAt(i, 1);
                int jumlah = (Integer) model.getValueAt(i, 2);
                double harga = Double.parseDouble(model.getValueAt(i, 3).toString());
                double total = jumlah * harga;

                if (pengeluaran.contains("BB")) {
                    String[] kode = pengeluaran.split(" - ");
                    kodeMenu = kode[0];
                    pengeluaran = kode[1];
                }

                totalPengeluaran += total;

                PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO detail_pengeluaran (kode_pengeluaran, kode_bahanbaku, nama_pengeluaran, jumlah, harga_satuan, total) VALUES (?, ?, ?, ?, ?, ?)"
                );
                ps2.setString(1, kodePengeluaran);
                ps2.setString(2, kodeMenu);
                ps2.setString(3, pengeluaran);
                ps2.setDouble(4, jumlah);
                ps2.setDouble(5, harga);
                ps2.setDouble(6, total);
                ps2.executeUpdate();

                if (kodeMenu != null) {
                    PreparedStatement updateStok = con.prepareStatement(
                            "UPDATE bahanbaku SET stok_bahanbaku = stok_bahanbaku + ? WHERE kode_bahanbaku = ?"
                    );
                    updateStok.setDouble(1, jumlah);
                    updateStok.setString(2, kodeMenu);
                    updateStok.executeUpdate();

                }
            }

            con.commit(); // Commit transaction

            // Tampilkan pesan sukses dan reset form
            JOptionPane.showMessageDialog(this,
                    "berhasil Menambahkan Porsi!\n",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            GlassPanePopup.closePopupLast();
            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged();
            }

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
        button1 = new com.raven.util.Button();
        btnSubmit = new com.raven.util.Button();
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
        jLabel2.setText("Jumlah");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nama Pengeluaran");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Harga");

        txtJumlah.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        button1.setBackground(new java.awt.Color(208, 90, 90));
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBahan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblBahan);
        if (tblBahan.getColumnModel().getColumnCount() > 0) {
            tblBahan.getColumnModel().getColumn(0).setResizable(false);
            tblBahan.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblBahan.getColumnModel().getColumn(2).setResizable(false);
            tblBahan.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblBahan.getColumnModel().getColumn(3).setResizable(false);
        }

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
        btnBatal.setText("Clear");
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
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(boxBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel2)
                        .addGap(204, 204, 204)
                        .addComponent(jLabel4))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(330, 330, 330)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(25, 25, 25)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel3))
                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(boxBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
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
                .addGap(5, 5, 5)
                .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_button1ActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        saveData();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addToTable();
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
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.TableColumn tblBahan;
    private com.raven.util.TextField txtHarga;
    private com.raven.util.TextField txtJumlah;
    // End of variables declaration//GEN-END:variables
}
