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
import java.sql.*;
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
import javax.swing.table.DefaultTableModel;
import raven.glasspanepopup.GlassPanePopup;

/**
 *
 * @author Fazaa
 */
public class PorsiPopup extends javax.swing.JPanel {

    /**
     * Creates new form PorsiPopup
     */
    Connection con = null;
    List<String> items;
    DefaultTableModel tableModel;
    private DataChangeListener dataChangeListener;

    public PorsiPopup() {
        initComponents();
        getCon();
        setModel();
        loadData();
        styling(menuCombo, items);
        setOpaque(false);

        btnBatalPorsi.setVisible(false);
        tbl_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbl_menu.getSelectedRow() != -1) {
                    btnBatalPorsi.setVisible(true);
                }
            }
        });

        tbl_menu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnBatalPorsi.setVisible(tbl_menu.getSelectedRow() != -1);
            }
        });
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMenuListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    private void setModel() {
        String[] judul = {"Kode Menu", "Nama Menu", "Jumlah"};
        tableModel = new DefaultTableModel(judul, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl_menu.setModel(tableModel);
    }

    private void loadData() {
        items = new ArrayList<String>();
        if (con != null) {
            try {
                String query = "SELECT * FROM menu";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String kode_menu = rs.getString("kode_menu");
                    String nama_menu = rs.getString("nama_menu");
                    menuCombo.addItem(kode_menu + " - " + nama_menu);
                    items.add(kode_menu + " - " + nama_menu);
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
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
        menuCombo = new javax.swing.JComboBox<>();
        btnBatalPorsi = new com.raven.util.Button();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_menu = new com.raven.swing.TableColumn();
        btnTambah = new com.raven.util.Button();

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TAMBAH PORSI");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama Menu");

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

        menuCombo.setBorder(null);

        btnBatalPorsi.setBackground(new java.awt.Color(97, 131, 175));
        btnBatalPorsi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBatalPorsi.setForeground(new java.awt.Color(255, 255, 255));
        btnBatalPorsi.setText("Batal");
        btnBatalPorsi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatalPorsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalPorsiActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(null);

        tbl_menu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "KODE", "NAMA", "JUMLAH"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_menu.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tbl_menu);

        btnTambah.setBackground(new java.awt.Color(97, 131, 175));
        btnTambah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(menuCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addComponent(btnBatalPorsi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(310, 310, 310)
                        .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel2))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(menuCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(btnBatalPorsi, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        GlassPanePopup.closePopupAll();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada item dalam pesanan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            con.setAutoCommit(false); // Mulai transaction

            // 3. Insert ke tabel detail_transaksi dan update stok
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String kodeMenu = (String) tableModel.getValueAt(i, 0);
                int jumlahPorsi = (Integer) tableModel.getValueAt(i, 2);

                String ket = "Porsi menu harian";
                String cekPorsi = "SELECT * FROM v_porsi_harian WHERE kode_menu = ?";
                PreparedStatement ps = con.prepareStatement(cekPorsi);
                ps.setString(1, kodeMenu);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ket = "Tambah porsi menu";
                }
                // Insert detail transaksi
                String queryPorsi = "INSERT INTO porsi_harian (kode_menu, jumlah, tanggal, keterangan) VALUES (?,  ?, NOW(), ?)";
                PreparedStatement psDetail = con.prepareStatement(queryPorsi);
                psDetail.setString(1, kodeMenu);
                psDetail.setInt(2, jumlahPorsi);
                psDetail.setString(3, ket);
                psDetail.executeUpdate();

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
    }//GEN-LAST:event_btnSelesaiActionPerformed

    private void btnBatalPorsiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalPorsiActionPerformed
        int selectedRow = tbl_menu.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin membatalkan menu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DefaultTableModel model = (DefaultTableModel) tbl_menu.getModel();
                model.removeRow(selectedRow);

                // Jika tidak ada data lagi, sembunyikan tombol
                if (tbl_menu.getRowCount() == 0) {
                    btnBatalPorsi.setVisible(false);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris bahan terlebih dahulu.");
        }
    }//GEN-LAST:event_btnBatalPorsiActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        String[] kodeMenu = menuCombo.getSelectedItem().toString().split(" - ");
        String jumlahStr = JOptionPane.showInputDialog(this, "Masukkan jumlah Porsi:");

        if (jumlahStr == null || jumlahStr.isEmpty() || Integer.parseInt(jumlahStr) <=0) {
            return;
        }
        if (con != null) {
            try {
                String qCari = "SELECT * FROM menu WHERE kode_menu = ? LIMIT 1";
                PreparedStatement ps = con.prepareStatement(qCari);
                ps.setString(1, kodeMenu[0].trim());
                ResultSet rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        String namaMenu = rs.getString(3);
                        boolean cekKode = false;

                        //cek apakah ada kode menu yang sama
                        int row = tbl_menu.getRowCount();
                        for (int i = 0; i < row; i++) {
                            //jika ada akan menambahkan jumlah menu tersebut
                            int stokTabel = (int) tbl_menu.getValueAt(i, 2);
                            if (tbl_menu.getValueAt(i, 0).equals(kodeMenu[0].trim())) {
                                int jumlahBaru = Integer.parseInt(tbl_menu.getValueAt(i, 2).toString());
                                tbl_menu.setValueAt(jumlahBaru + Integer.parseInt(jumlahStr), i, 2);
                                cekKode = true;
                                break;
                            }
                        }
                        if (!cekKode) {
                            tableModel.addRow(new Object[]{kodeMenu[0].trim(), namaMenu, Integer.parseInt(jumlahStr)});
                        }

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Menu Tidak Terdaftar!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnTambahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBatal;
    private com.raven.util.Button btnBatalPorsi;
    private com.raven.util.Button btnSelesai;
    private com.raven.util.Button btnTambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<String> menuCombo;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.TableColumn tbl_menu;
    // End of variables declaration//GEN-END:variables
}
