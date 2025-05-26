/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import config.DatabaseConfig;
import java.awt.Color;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class Form_Laporan extends javax.swing.JPanel {

    Connection con;
    DefaultTableModel tableModel;
    int indexLaporan = 0; // 0 = Profit, 1 = Pemasukan, 2 = Pengeluaran
    int indexFilter = 0;  // 0 = Hari, 1 = Bulan, 2 = Tahun

    public Form_Laporan() {
        initComponents();
        getCon();
        setTableModelDynamic();
        loadData();
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTableModelDynamic() {
        String[][][] kolom = {
            { // Profit
                {"Tanggal", "Penjualan", "Pengeluaran", "Profit", "Minus"},
                {"Bulan", "Penjualan", "Pengeluaran", "Profit", "Minus"},
                {"Tahun", "Penjualan", "Pengeluaran", "Profit", "Minus"}
            },
            { // Pemasukan
                {"Tanggal", "No Transaksi", "Nama Pelanggan", "Nama Menu", "Jumlah", "Total"},
                {"Bulan", "Total Transaksi", "Rata-rata per hari", "Menu Terlaris"},
                {"Tahun", "Total", "Rata-rata per bulan", "Menu Terlaris"}
            },
            { // Pengeluaran
                {"Tanggal", "Kategori", "Nama", "Jumlah", "Harga Satuan", "Total Harga"},
                {"Bulan", "Kategori", "Total Pengeluaran"},
                {"Tahun", "Total Pengeluaran", "Kategori"}
            }
        };

        tableModel = new DefaultTableModel(kolom[indexLaporan][indexFilter], 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblLaporan.setModel(tableModel);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (indexLaporan == 0) {
            loadLaporanProfit();
        } else if (indexLaporan == 1) {
            loadLaporanPemasukan();
        } else if (indexLaporan == 2) {
            loadLaporanPengeluaran();
        }
    }

    private void loadLaporanProfit() {
        try {
            String query = "SELECT tanggal, total_penjualan, total_pengeluaran FROM v_laporan WHERE 1=1";
            if (indexFilter == 0) {
                query += " AND tanggal = CURDATE()";
            } else if (indexFilter == 1) {
                query += " AND MONTH(tanggal) = MONTH(CURDATE()) AND YEAR(tanggal) = YEAR(CURDATE())";
            } else {
                query += " AND YEAR(tanggal) = YEAR(CURDATE())";
            }

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double penjualan = rs.getDouble("total_penjualan");
                double pengeluaran = rs.getDouble("total_pengeluaran");
                double profit = penjualan - pengeluaran;
                String minus = profit < 0 ? "YA" : "TIDAK";
                Object waktu = rs.getDate("tanggal");
                if (indexFilter == 1) {
                    waktu = new SimpleDateFormat("MMMM").format(rs.getDate("tanggal"));
                } else if (indexFilter == 2) {
                    waktu = new SimpleDateFormat("yyyy").format(rs.getDate("tanggal"));
                }
                tableModel.addRow(new Object[]{waktu, penjualan, pengeluaran, profit, minus});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLaporanPemasukan() {
        try {
            String query = "SELECT t.tgl_transaksi, t.kode_transaksi, t.nama_pelanggan, m.nama_menu, dt.jumlah, (dt.jumlah * m.harga) as total "
                    + "FROM transaksi t JOIN detail_transaksi dt ON t.kode_transaksi = dt.kode_transaksi "
                    + "JOIN menu m ON dt.kode_menu = m.kode_menu WHERE 1=1";
            if (indexFilter == 0) {
                query += " AND DATE(t.tgl_transaksi) = CURDATE()";
            } else if (indexFilter == 1) {
                query += " AND MONTH(t.tgl_transaksi) = MONTH(CURDATE()) AND YEAR(t.tgl_transaksi) = YEAR(CURDATE())";
            } else {
                query += " AND YEAR(t.tgl_transaksi) = YEAR(CURDATE())";
            }

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getDate("tgl_transaksi"),
                    rs.getString("kode_transaksi"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah"),
                    rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLaporanPengeluaran() {
        try {
            String query = "SELECT p.tgl_pengeluaran, dp.nama_pengeluaran, dp.jumlah, dp.harga_satuan, dp.total "
                    + "FROM pengeluaran p JOIN detail_pengeluaran dp ON p.kode_pengeluaran = dp.kode_pengeluaran WHERE 1=1";
            if (indexFilter == 0) {
                query += " AND p.tgl_pengeluaran = CURDATE()";
            } else if (indexFilter == 1) {
                query += " AND MONTH(p.tgl_pengeluaran) = MONTH(CURDATE()) AND YEAR(p.tgl_pengeluaran) = YEAR(CURDATE())";
            } else {
                query += " AND YEAR(p.tgl_pengeluaran) = YEAR(CURDATE())";
            }

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getDate("tgl_pengeluaran"),
                    "Operasional",
                    rs.getString("nama_pengeluaran"),
                    rs.getInt("jumlah"),
                    rs.getDouble("harga_satuan"),
                    rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        jLabel2 = new javax.swing.JLabel();
        panelRound2 = new com.raven.swing.PanelRound();
        btnProfit = new com.raven.util.Button();
        btnPemasukan = new com.raven.util.Button();
        btnPengeluaran = new com.raven.util.Button();
        btnHari = new com.raven.util.Button();
        btnBulan = new com.raven.util.Button();
        btnTahun = new com.raven.util.Button();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLaporan = new com.raven.swing.TableColumn();

        setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(33, 53, 85));
        jLabel2.setText("LAPORAN");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));

        panelRound2.setBackground(new java.awt.Color(33, 53, 85));

        btnProfit.setText("PROFIT");
        btnProfit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnProfit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfitActionPerformed(evt);
            }
        });

        btnPemasukan.setText("PEMASUKAN");
        btnPemasukan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPemasukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPemasukanActionPerformed(evt);
            }
        });

        btnPengeluaran.setText("PENGELUARAN");
        btnPengeluaran.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPengeluaranActionPerformed(evt);
            }
        });

        btnHari.setText("DAY");
        btnHari.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHariActionPerformed(evt);
            }
        });

        btnBulan.setText("MONTH");
        btnBulan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBulanActionPerformed(evt);
            }
        });

        btnTahun.setText("YEAR");
        btnTahun.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTahun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTahunActionPerformed(evt);
            }
        });

        tblLaporan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TANGGAL", "PEMASUKAN", "PENGELUARAN", "PROFIT", "MINUS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblLaporan);

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(btnProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(btnPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(btnHari, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHari, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnProfitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfitActionPerformed
        indexLaporan = 0;
        btnProfit.setBackground(Color.WHITE);
        btnPemasukan.setBackground(new Color(144, 154, 170));
        btnPengeluaran.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnProfitActionPerformed

    private void btnBulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBulanActionPerformed
        indexFilter = 1;
        btnBulan.setBackground(Color.WHITE);
        btnHari.setBackground(new Color(144, 154, 170));
        btnTahun.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnBulanActionPerformed

    private void btnPemasukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPemasukanActionPerformed
        indexLaporan = 1;
        btnPemasukan.setBackground(Color.WHITE);
        btnProfit.setBackground(new Color(144, 154, 170));
        btnPengeluaran.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnPemasukanActionPerformed

    private void btnPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengeluaranActionPerformed
        indexLaporan = 2;
        btnPengeluaran.setBackground(Color.WHITE);
        btnProfit.setBackground(new Color(144, 154, 170));
        btnPemasukan.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnPengeluaranActionPerformed

    private void btnHariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHariActionPerformed
        indexFilter = 0;
        btnHari.setBackground(Color.WHITE);
        btnBulan.setBackground(new Color(144, 154, 170));
        btnTahun.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnHariActionPerformed

    private void btnTahunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTahunActionPerformed
        indexFilter = 2;
        btnTahun.setBackground(Color.WHITE);
        btnHari.setBackground(new Color(144, 154, 170));
        btnBulan.setBackground(new Color(144, 154, 170));
        setTableModelDynamic();
        loadData();
    }//GEN-LAST:event_btnTahunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.util.Button btnBulan;
    private com.raven.util.Button btnHari;
    private com.raven.util.Button btnPemasukan;
    private com.raven.util.Button btnPengeluaran;
    private com.raven.util.Button btnProfit;
    private com.raven.util.Button btnTahun;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private com.raven.swing.PanelRound panelRound2;
    private com.raven.swing.TableColumn tblLaporan;
    // End of variables declaration//GEN-END:variables
}
