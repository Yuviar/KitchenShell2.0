/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.popup;

import config.DatabaseConfig;
import java.sql.*;
import javax.swing.JOptionPane;
import raven.glasspanepopup.GlassPanePopup;
import com.raven.event.DataChangeListener;
import com.raven.swing.ModernScrollBarUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class BahanBakuPopup extends javax.swing.JPanel {

    private Connection con = null;
    private DataChangeListener dataChangeListener;
    List<String> items;
    private boolean isEditMode = false;
    private String editKodeBahan = null;

    public BahanBakuPopup() {
        initComponents();
        getCon();
        loadData();
        styling(satuanCombo);
    }

    public void setBahanBakuListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        items = new ArrayList<String>();
        if (con != null) {
            try {
                String query = "SELECT * FROM satuan";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String kode_satuan = rs.getString("kode_satuan");
                    String nama_satuan = rs.getString("satuan");
                    satuanCombo.addItem(kode_satuan + " - " + nama_satuan);
                    items.add(kode_satuan + " - " + nama_satuan);
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void styling(JComboBox combo) {
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

    }

    private String generateKodeMenu() {
        try {
            String sql = "SELECT RIGHT(kode_bahanbaku, 4) AS nomor FROM bahanbaku ORDER BY kode_bahanbaku DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int nextNumber = 1;
            if (rs.next() && rs.getString("nomor") != null) {
                nextNumber = Integer.parseInt(rs.getString("nomor")) + 1;
            }

            return String.format("BB%04d", nextNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return "BB0001";
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnBatal = new com.raven.util.Button();
        btnSelesai = new com.raven.util.Button();
        txtNama = new com.raven.util.TextField();
        jLabel4 = new javax.swing.JLabel();
        satuanCombo = new javax.swing.JComboBox<>();

        setOpaque(false);

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TAMBAH BAHAN");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama");

        btnBatal.setBackground(new java.awt.Color(97, 131, 175));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnSelesai.setBackground(new java.awt.Color(97, 131, 175));
        btnSelesai.setForeground(new java.awt.Color(255, 255, 255));
        btnSelesai.setText("Selesai");
        btnSelesai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelesaiActionPerformed(evt);
            }
        });

        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Satuan");

        satuanCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Satuan" }));
        satuanCombo.setBorder(null);

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(satuanCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelRound1Layout.createSequentialGroup()
                            .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4))
                            .addGap(86, 86, 86))
                        .addGroup(panelRound1Layout.createSequentialGroup()
                            .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                            .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addGap(0, 0, 0)
                .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(satuanCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        try {
            String nama = txtNama.getText().trim();

            if (!nama.isEmpty() || satuanCombo.getSelectedIndex() > 0) {
                if (isEditMode) {
                    // UPDATE 
                    String query = "UPDATE bahanbaku SET nama_bahanbaku = ?, kode_satuan = ? WHERE kode_bahanbaku = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, nama);
                    ps.setString(2, satuanCombo.getSelectedItem().toString().split(" - ")[0]);
                    ps.setString(3, editKodeBahan);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Data berhasil diupdate!");
                } else {
                    // INSERT
                    String query = "INSERT INTO bahanbaku VALUES (?, ?, 0, ?)";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, generateKodeMenu());
                    ps.setString(2, nama);
                    ps.setString(3, satuanCombo.getSelectedItem().toString().split(" - ")[0]);
                    ps.execute();

                    JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
                }

                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged();
                }

                GlassPanePopup.closePopupLast();
            } else {
                throw new Exception("Semua data harus diisi!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
        }

    }//GEN-LAST:event_btnSelesaiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnSelesai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private com.raven.swing.PanelRound panelRound1;
    private javax.swing.JComboBox<String> satuanCombo;
    private com.raven.util.TextField txtNama;
    // End of variables declaration//GEN-END:variables
}
