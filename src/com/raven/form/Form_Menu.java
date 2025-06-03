/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import com.raven.popup.MenuPopup;
import com.raven.popup.BahanBakuPopup;
import java.sql.*;
import config.DatabaseConfig;
import javax.swing.table.DefaultTableModel;
import raven.glasspanepopup.GlassPanePopup;
import com.raven.event.DataChangeListener;
import com.raven.popup.HapusDataPopup;
import com.raven.popup.KategoriPopup;
import com.raven.popup.SatuanPopup;
import com.raven.swing.ModernScrollBarUI;
import java.awt.Color;
import javax.swing.JOptionPane;

import static config.Utilz.*;

/**
 *
 * @author Fazaa
 */
public class Form_Menu extends javax.swing.JPanel {

    Connection con = null;
    DefaultTableModel tableModel;
    private DataChangeListener dataChangeListener;
    MenuPopup menuPopup = new MenuPopup();
    BahanBakuPopup bahanPopup = new BahanBakuPopup();
    KategoriPopup kategPopup = new KategoriPopup();
    SatuanPopup satuanPopup = new SatuanPopup();
    HapusDataPopup hapusPopup = new HapusDataPopup();
    int indexTable = 0; // 0 = Menu, 1 = Bahan Baku

    public Form_Menu() {
        getCon();
        initComponents();
        setModel();
        menuPopup.setMenuListener(new DataChangeListener() {
            @Override
            public void onDataChanged() {
                loadData();
            }
        });
        bahanPopup.setBahanBakuListener(new DataChangeListener() {
            @Override
            public void onDataChanged() {
                loadData();
            }
        });
        kategPopup.setKategoriListener(new DataChangeListener() {
            @Override
            public void onDataChanged() {
                loadData();
            }
        });
        satuanPopup.setSatuanListener(new DataChangeListener() {
            @Override
            public void onDataChanged() {
                loadData();
            }
        });
        setOpaque(false);

        jScrollPane2.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setModel() {
        String[][] judul = {{"Kode Menu", "Kategori", "Nama menu", "harga jual"}, {"Kode Bahan", "Nama", "Stok"}, {"", "Nama Satuan"}, {"Kode Kategori", "Nama Kategori"}};
        tableModel = new DefaultTableModel(judul[indexTable], 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl_menu.setModel(tableModel);
        loadData();
    }

    private void loadData() {
        if (con != null) {
            try {
                String query = "";

                if (indexTable == 0) {
                    query = "SELECT menu.kode_menu, kategori.nama_kategori AS kategori, menu.nama_menu, menu.harga FROM menu\n"
                            + "JOIN kategori ON menu.kode_kategori = kategori.kode_kategori";
                } else if (indexTable == 1) {
                    query = "SELECT b.kode_bahanbaku, b.nama_bahanbaku, b.stok_bahanbaku, s.satuan "
                            + "FROM bahanbaku b JOIN satuan s ON b.kode_satuan = s.kode_satuan";
                } else if (indexTable == 2) {
                    query = "SELECT kode_satuan, satuan FROM satuan";
                } else if (indexTable == 3) {
                    query = "SELECT * FROM kategori";
                }

                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                tableModel.setRowCount(0);

                while (rs.next()) {
                    String[] data;

                    if (indexTable == 0) {
                        data = new String[]{
                            rs.getString("kode_menu"),
                            rs.getString("kategori"),
                            rs.getString("nama_menu"),
                            convertRupiah(rs.getDouble("harga"))
                        };
                    } else if (indexTable == 1) {
                        double stok = rs.getDouble("stok_bahanbaku");
                        String satuan = rs.getString("satuan");
                        String stokFormatted;

                        if ((satuan.equalsIgnoreCase("Gram") || satuan.equalsIgnoreCase("Ml")) && stok >= 1000) {
                            stokFormatted = (stok / 1000) + (satuan.equalsIgnoreCase("Gram") ? " Kg" : " L");
                        } else {
                            stokFormatted = stok + " " + satuan;
                        }

                        data = new String[]{
                            rs.getString("kode_bahanbaku"),
                            rs.getString("nama_bahanbaku"),
                            stokFormatted
                        };
                    } else if (indexTable == 2) {
                        data = new String[]{rs.getString("kode_satuan"), rs.getString("satuan")};
                    } else if (indexTable == 3) {
                        data = new String[]{
                            rs.getString("kode_kategori"),
                            rs.getString("nama_kategori")
                        };
                    } else {
                        data = new String[]{};
                    }

                    tableModel.addRow(data);
                }

                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        button1 = new com.raven.util.Button();
        button2 = new com.raven.util.Button();
        editBtn = new com.raven.util.Button();
        hapusBtn = new com.raven.util.Button();
        btnAdd = new com.raven.util.Button();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_menu = new com.raven.swing.TableColumn();
        button3 = new com.raven.util.Button();
        button4 = new com.raven.util.Button();
        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(865, 583));

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));
        panelRound1.setPreferredSize(new java.awt.Dimension(865, 583));
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        button1.setText("DAFTAR MENU");
        button1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        panelRound1.add(button1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 160, -1));

        button2.setBackground(new java.awt.Color(144, 154, 170));
        button2.setText("BAHAN BAKU");
        button2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        panelRound1.add(button2, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 20, 160, -1));

        editBtn.setBackground(new java.awt.Color(255, 157, 35));
        editBtn.setForeground(new java.awt.Color(255, 255, 255));
        editBtn.setText("UPDATE");
        editBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        editBtn.setShadowColor(new java.awt.Color(102, 102, 102));
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });
        panelRound1.add(editBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 80, 45));

        hapusBtn.setBackground(new java.awt.Color(208, 90, 90));
        hapusBtn.setForeground(new java.awt.Color(255, 255, 255));
        hapusBtn.setText("DELETE");
        hapusBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        hapusBtn.setShadowColor(new java.awt.Color(102, 102, 102));
        hapusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusBtnActionPerformed(evt);
            }
        });
        panelRound1.add(hapusBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 75, 80, 45));

        btnAdd.setBackground(new java.awt.Color(97, 131, 175));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("ADD");
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdd.setShadowColor(new java.awt.Color(102, 102, 102));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        panelRound1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 75, 80, 45));

        jScrollPane2.setBorder(null);

        tbl_menu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "KODE", "NAMA", "NO TELP", "LEVEL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_menu.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tbl_menu);

        panelRound1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 800, 350));

        button3.setBackground(new java.awt.Color(144, 154, 170));
        button3.setText("SATUAN");
        button3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });
        panelRound1.add(button3, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 160, -1));

        button4.setBackground(new java.awt.Color(144, 154, 170));
        button4.setText("KATEGORI");
        button4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });
        panelRound1.add(button4, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 20, 160, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(33, 53, 85));
        jLabel1.setText("MENU");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:
        indexTable = 0;
        button1.setBackground(new Color(255, 255, 255));
        button2.setBackground(new Color(144, 154, 170));
        button3.setBackground(new Color(144, 154, 170));
        button4.setBackground(new Color(144, 154, 170));
        setModel();
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:
        indexTable = 1;
        button2.setBackground(new Color(255, 255, 255));
        button1.setBackground(new Color(144, 154, 170));
        button3.setBackground(new Color(144, 154, 170));
        button4.setBackground(new Color(144, 154, 170));
        setModel();
    }//GEN-LAST:event_button2ActionPerformed

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        int selectedRow = tbl_menu.getSelectedRow();
        if (selectedRow != -1) {
            if (indexTable == 0) {
                String kode = tbl_menu.getValueAt(selectedRow, 0).toString();
                menuPopup.setEditMode(kode);
                GlassPanePopup.showPopup(menuPopup);
            } else if (indexTable == 1) {
                String kode = tbl_menu.getValueAt(selectedRow, 0).toString();
                String nama = tbl_menu.getValueAt(selectedRow, 1).toString();
                bahanPopup.setEditMode(kode, nama);
                GlassPanePopup.showPopup(bahanPopup);
            } else if (indexTable == 2) {
                String kode = tbl_menu.getValueAt(selectedRow, 0).toString();
                String nama = tbl_menu.getValueAt(selectedRow, 1).toString();
                satuanPopup.setEditMode(kode, nama);
                GlassPanePopup.showPopup(satuanPopup);
            } else if (indexTable == 3) {
                String kode = tbl_menu.getValueAt(selectedRow, 0).toString();
                String nama = tbl_menu.getValueAt(selectedRow, 1).toString();
                kategPopup.setEditMode(kode, nama);
                GlassPanePopup.showPopup(kategPopup);
            }
//            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin diedit!");
        }


    }//GEN-LAST:event_editBtnActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (indexTable == 0) {
            menuPopup.resetForm();
            menuPopup.prepareTambah();
            GlassPanePopup.showPopup(menuPopup);
        } else if (indexTable == 1) {
            bahanPopup.resetForm();
            GlassPanePopup.showPopup(bahanPopup);
        } else if (indexTable == 2) {
            satuanPopup.resetForm();
            GlassPanePopup.showPopup(satuanPopup);
        } else if (indexTable == 3) {
            kategPopup.resetForm();
            GlassPanePopup.showPopup(kategPopup);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void hapusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusBtnActionPerformed
        int selectedRow = tbl_menu.getSelectedRow();
        if (selectedRow != -1) {
            if (indexTable == 0) {
                String uid = tbl_menu.getValueAt(selectedRow, 0).toString();
                hapusPopup.setData("menu", "kode_menu", uid);
                hapusPopup.setDataChangeListener(() -> loadData());
                GlassPanePopup.showPopup(hapusPopup);
            } else if (indexTable == 1) {
                String uid = tbl_menu.getValueAt(selectedRow, 0).toString();
                hapusPopup.setData("bahanbaku", "kode_bahanbaku", uid);
                hapusPopup.setDataChangeListener(() -> loadData());
                GlassPanePopup.showPopup(hapusPopup);
            } else if (indexTable == 2) {
                String uid = tbl_menu.getValueAt(selectedRow, 0).toString();
                hapusPopup.setData("satuan", "kode_satuan", uid);
                hapusPopup.setDataChangeListener(() -> loadData());
                GlassPanePopup.showPopup(hapusPopup);
            } else if (indexTable == 3) {
                String uid = tbl_menu.getValueAt(selectedRow, 0).toString();
                hapusPopup.setData("kategori", "kode_kategori", uid);
                hapusPopup.setDataChangeListener(() -> loadData());
                GlassPanePopup.showPopup(hapusPopup);
            }
        }
    }//GEN-LAST:event_hapusBtnActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        indexTable = 2;
        button3.setBackground(new Color(255, 255, 255));
        button1.setBackground(new Color(144, 154, 170));
        button2.setBackground(new Color(144, 154, 170));
        button4.setBackground(new Color(144, 154, 170));
        setModel();
    }//GEN-LAST:event_button3ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        indexTable = 3;
        button4.setBackground(new Color(255, 255, 255));
        button1.setBackground(new Color(144, 154, 170));
        button2.setBackground(new Color(144, 154, 170));
        button3.setBackground(new Color(144, 154, 170));
        setModel();
    }//GEN-LAST:event_button4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnAdd;
    private com.raven.util.Button button1;
    private com.raven.util.Button button2;
    private com.raven.util.Button button3;
    private com.raven.util.Button button4;
    private com.raven.util.Button editBtn;
    private com.raven.util.Button hapusBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.TableColumn tbl_menu;
    // End of variables declaration//GEN-END:variables

}
