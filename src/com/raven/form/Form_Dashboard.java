package com.raven.form;

import com.raven.chart.ModelChart;
import com.raven.swing.ModernScrollBarUI;
import config.DatabaseConfig;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.JOptionPane;

import static config.Utilz.*;

public class Form_Dashboard extends javax.swing.JPanel {

    private Connection con;
    DefaultTableModel tableModel;

    public Form_Dashboard() {
        initComponents();
        setOpaque(false);
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
        }
        init();
        jScrollPane1.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    }

    private void init() {
        initChart();
        initCard();
        initTable();
    }

    private void initTable() {
        String[] judul = {"Bahan Baku", "Sisa Bahan"};
        tableModel = new DefaultTableModel(judul, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(tableModel);

        String query = "SELECT `nama_bahanbaku`,`stok_bahanbaku`,`satuan` FROM `bahanbaku` JOIN `satuan` ON `satuan`.`kode_satuan` = `bahanbaku`.`kode_satuan`";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet hasil = ps.executeQuery();
            while (hasil.next()) {
                if (hasil.getInt(2) <= 1000 && !"Pcs".equals(hasil.getString(3))) {
                    String[] data = {hasil.getString(1), hasil.getString(2) + " " + hasil.getString(3)};
                    tableModel.addRow(data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//  1/3 done
    private void initCard() {
        String query = "";
        try {
            query = "SELECT COUNT(*) FROM `menu`";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ResultSet hasil = ps.executeQuery();
                if (hasil.next()) {
                    jmlMenu.setText(hasil.getInt(1) + "");
                } else {
                    jmlMenu.setText("0");
                }
            }

            query = "SELECT SUM(total_penjualan) as total_penjualan FROM v_laporan WHERE DATE(tanggal) = CURDATE() GROUP BY DATE(tanggal);";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ResultSet hasil = ps.executeQuery();
                if (hasil.next()) {
                    pendapatanHari.setText(convertRupiah((double) hasil.getInt(1)));
                } else {
                    pendapatanHari.setText("Rp. 0");

                }
            }

            query = "SELECT SUM(total_penjualan) as total_penjualan FROM v_laporan WHERE MONTH(tanggal) = MONTH(CURDATE()) GROUP BY DATE_FORMAT(tanggal,'%m');";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ResultSet hasil = ps.executeQuery();
                if (hasil.next()) {
                    pendapatanBulan.setText(convertRupiah((double) hasil.getInt(1)));
                } else {
                    pendapatanBulan.setText("Rp. 0");

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initChart() {
        chart.addLegend("Pendapatan", new Color(245, 189, 135));
        chart.addLegend("Pengeluaran", new Color(135, 189, 245));
        try {
            String q = "SELECT \n"
                    + "    bulan_data.bulan_nama AS bulan,\n"
                    + "    IFNULL(SUM(t.total_penjualan), 0) AS total_penjualan,\n"
                    + "    IFNULL(SUM(t.total_pengeluaran), 0) AS total_pengeluaran\n"
                    + "FROM (\n"
                    + "    SELECT 1 AS bulan_num, 'Jan' AS bulan_nama UNION ALL\n"
                    + "    SELECT 2, 'Feb' UNION ALL\n"
                    + "    SELECT 3, 'Mar' UNION ALL\n"
                    + "    SELECT 4, 'Apr' UNION ALL\n"
                    + "    SELECT 5, 'May' UNION ALL\n"
                    + "    SELECT 6, 'Jun' UNION ALL\n"
                    + "    SELECT 7, 'Jul' UNION ALL\n"
                    + "    SELECT 8, 'Aug' UNION ALL\n"
                    + "    SELECT 9, 'Sep' UNION ALL\n"
                    + "    SELECT 10, 'Oct' UNION ALL\n"
                    + "    SELECT 11, 'Nov' UNION ALL\n"
                    + "    SELECT 12, 'Dec'\n"
                    + ") AS bulan_data\n"
                    + "LEFT JOIN (\n"
                    + "    SELECT \n"
                    + "        MONTH(tanggal) AS bulan,\n"
                    + "        SUM(total_penjualan) AS total_penjualan,\n"
                    + "        SUM(total_pengeluaran) AS total_pengeluaran\n"
                    + "    FROM v_laporan\n"
                    + "    WHERE YEAR(tanggal) = YEAR(CURDATE())\n"
                    + "    GROUP BY MONTH(tanggal)\n"
                    + ") t\n"
                    + "ON bulan_data.bulan_num = t.bulan\n"
                    + "GROUP BY bulan_data.bulan_num, bulan_data.bulan_nama\n"
                    + "ORDER BY bulan_data.bulan_num;";
            PreparedStatement ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chart.addData(new ModelChart(rs.getString(1), new double[]{rs.getDouble(2), rs.getDouble(3)}));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        chart.addData(new ModelChart("Jan", new double[]{100, 150}));
//        chart.addData(new ModelChart("Feb", new double[]{600, 750}));
//        chart.addData(new ModelChart("Mar", new double[]{200, 350}));
//        chart.addData(new ModelChart("Apr", new double[]{480, 150}));
//        chart.addData(new ModelChart("Mei", new double[]{350, 540}));
//        chart.addData(new ModelChart("Juni", new double[]{190, 500}));
//        chart.addData(new ModelChart("Juli", new double[]{100, 150}));
//        chart.addData(new ModelChart("Ags", new double[]{600, 750}));
//        chart.addData(new ModelChart("Sept", new double[]{200, 350}));
//        chart.addData(new ModelChart("Okt", new double[]{480, 150}));
//        chart.addData(new ModelChart("Nov", new double[]{350, 540}));
//        chart.addData(new ModelChart("Des", new double[]{190, 500}));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"Tahu", "900g"});
        model.addRow(new Object[]{"Minyak", "500ml"});
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(jLabel1.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        stats = new com.raven.swing.PanelRound();
        chart = new com.raven.chart.Chart();
        jLabel10 = new javax.swing.JLabel();
        card1 = new com.raven.swing.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jmlMenu = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        card3 = new com.raven.swing.PanelRound();
        jLabel7 = new javax.swing.JLabel();
        pendapatanBulan = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        card2 = new com.raven.swing.PanelRound();
        jLabel5 = new javax.swing.JLabel();
        pendapatanHari = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tabel = new com.raven.swing.PanelRound();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.TableColumn();
        jLabel9 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(25, 32767));
        setMinimumSize(new java.awt.Dimension(16, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(33, 53, 85));
        jLabel1.setText("DASHBOARD");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);

        stats.setBackground(new java.awt.Color(33, 53, 85));

        chart.setPreferredSize(new java.awt.Dimension(320, 382));

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Grafik Tahunan");

        javax.swing.GroupLayout statsLayout = new javax.swing.GroupLayout(stats);
        stats.setLayout(statsLayout);
        statsLayout.setHorizontalGroup(
            statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(statsLayout.createSequentialGroup()
                .addGap(249, 249, 249)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        statsLayout.setVerticalGroup(
            statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        card1.setBackground(new java.awt.Color(33, 53, 85));
        card1.setForeground(new java.awt.Color(255, 255, 255));
        card1.setMaximumSize(new java.awt.Dimension(300, 32767));
        card1.setMinimumSize(new java.awt.Dimension(200, 0));
        card1.setPreferredSize(new java.awt.Dimension(285, 100));
        card1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Jumlah Menu");
        card1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 190, -1));

        jmlMenu.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jmlMenu.setForeground(new java.awt.Color(255, 255, 255));
        jmlMenu.setText("20");
        card1.add(jmlMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 190, 52));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/icons8-fast-food-64.png"))); // NOI18N
        card1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 15, -1, -1));

        card3.setBackground(new java.awt.Color(33, 53, 85));
        card3.setForeground(new java.awt.Color(255, 255, 255));
        card3.setMaximumSize(new java.awt.Dimension(300, 32767));
        card3.setMinimumSize(new java.awt.Dimension(200, 0));
        card3.setPreferredSize(new java.awt.Dimension(285, 100));
        card3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Pendapatan Bulan ini");
        card3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 190, -1));

        pendapatanBulan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        pendapatanBulan.setForeground(new java.awt.Color(255, 255, 255));
        pendapatanBulan.setText("Rp 500.000");
        card3.add(pendapatanBulan, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 190, 52));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/icons8-cash-64.png"))); // NOI18N
        card3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 17, -1, 60));

        card2.setBackground(new java.awt.Color(33, 53, 85));
        card2.setForeground(new java.awt.Color(255, 255, 255));
        card2.setMaximumSize(new java.awt.Dimension(300, 32767));
        card2.setMinimumSize(new java.awt.Dimension(200, 0));
        card2.setPreferredSize(new java.awt.Dimension(285, 100));
        card2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Pendapatan Hari ini");
        card2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 190, -1));

        pendapatanHari.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        pendapatanHari.setForeground(new java.awt.Color(255, 255, 255));
        pendapatanHari.setText("Rp 50.000");
        card2.add(pendapatanHari, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 190, 52));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/icons8-dollar-coin-64.png"))); // NOI18N
        card2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 17, -1, -1));

        tabel.setBackground(new java.awt.Color(33, 53, 85));
        tabel.setMaximumSize(new java.awt.Dimension(300, 32767));
        tabel.setMinimumSize(new java.awt.Dimension(280, 0));
        tabel.setPreferredSize(new java.awt.Dimension(285, 44));

        jScrollPane1.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bahan baku", "Sisa Bahan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table);

        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Bahan baku hampir habis");

        javax.swing.GroupLayout tabelLayout = new javax.swing.GroupLayout(tabel);
        tabel.setLayout(tabelLayout);
        tabelLayout.setHorizontalGroup(
            tabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(tabelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tabelLayout.setVerticalGroup(
            tabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(stats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel1))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tabel, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.swing.PanelRound card1;
    private com.raven.swing.PanelRound card2;
    private com.raven.swing.PanelRound card3;
    private com.raven.chart.Chart chart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jmlMenu;
    private javax.swing.JLabel pendapatanBulan;
    private javax.swing.JLabel pendapatanHari;
    private com.raven.swing.PanelRound stats;
    private com.raven.swing.PanelRound tabel;
    private com.raven.swing.TableColumn table;
    // End of variables declaration//GEN-END:variables
}
