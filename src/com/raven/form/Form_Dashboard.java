package com.raven.form;

import com.raven.chart.ModelChart;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Form_Dashboard extends javax.swing.JPanel {

    public Form_Dashboard() {
        initComponents();
        setOpaque(false);
        init();
    }

    private void init() {
        chart.addLegend("Income", new Color(245, 189, 135));
        chart.addLegend("Expense", new Color(135, 189, 245));
        chart.addLegend("Profit", new Color(189, 135, 245));
        chart.addLegend("Cost", new Color(139, 229, 222));
        chart.addData(new ModelChart("January", new double[]{100, 150, 200, 500}));
        chart.addData(new ModelChart("February", new double[]{600, 750, 300, 150}));
        chart.addData(new ModelChart("March", new double[]{200, 350, 1000, 900}));
        chart.addData(new ModelChart("April", new double[]{480, 150, 750, 700}));
        chart.addData(new ModelChart("May", new double[]{350, 540, 300, 150}));
        chart.addData(new ModelChart("June", new double[]{190, 500, 700, 1000}));
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{"Jonh china", "Male"});
        model.addRow(new Object[]{"Jonh china", "Male"});
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(jLabel1.CENTER);
    
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        stats = new com.raven.swing.PanelRound();
        chart = new com.raven.chart.Chart();
        scrollBar1 = new com.raven.swing.ScrollBar();
        card1 = new com.raven.swing.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        card3 = new com.raven.swing.PanelRound();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        card2 = new com.raven.swing.PanelRound();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tabel = new com.raven.swing.PanelRound();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.TableColumn();

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

        javax.swing.GroupLayout statsLayout = new javax.swing.GroupLayout(stats);
        stats.setLayout(statsLayout);
        statsLayout.setHorizontalGroup(
            statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        statsLayout.setVerticalGroup(
            statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statsLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        scrollBar1.setBackground(new java.awt.Color(245, 245, 245));

        card1.setBackground(new java.awt.Color(33, 53, 85));
        card1.setForeground(new java.awt.Color(255, 255, 255));
        card1.setMaximumSize(new java.awt.Dimension(300, 32767));
        card1.setMinimumSize(new java.awt.Dimension(200, 0));
        card1.setPreferredSize(new java.awt.Dimension(285, 100));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Jumlah Menu");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("20");

        javax.swing.GroupLayout card1Layout = new javax.swing.GroupLayout(card1);
        card1.setLayout(card1Layout);
        card1Layout.setHorizontalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap())
        );
        card1Layout.setVerticalGroup(
            card1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card1Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        card3.setBackground(new java.awt.Color(33, 53, 85));
        card3.setForeground(new java.awt.Color(255, 255, 255));
        card3.setMaximumSize(new java.awt.Dimension(300, 32767));
        card3.setMinimumSize(new java.awt.Dimension(200, 0));
        card3.setPreferredSize(new java.awt.Dimension(285, 100));

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Jumlah Menu");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("20");

        javax.swing.GroupLayout card3Layout = new javax.swing.GroupLayout(card3);
        card3.setLayout(card3Layout);
        card3Layout.setHorizontalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, card3Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        card3Layout.setVerticalGroup(
            card3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel7)
                .addGap(0, 0, 0)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        card2.setBackground(new java.awt.Color(33, 53, 85));
        card2.setForeground(new java.awt.Color(255, 255, 255));
        card2.setMaximumSize(new java.awt.Dimension(300, 32767));
        card2.setMinimumSize(new java.awt.Dimension(200, 0));
        card2.setPreferredSize(new java.awt.Dimension(285, 100));

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Jumlah Menu");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("20");

        javax.swing.GroupLayout card2Layout = new javax.swing.GroupLayout(card2);
        card2.setLayout(card2Layout);
        card2Layout.setHorizontalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, card2Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        card2Layout.setVerticalGroup(
            card2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabel.setBackground(new java.awt.Color(33, 53, 85));
        tabel.setMaximumSize(new java.awt.Dimension(300, 32767));
        tabel.setMinimumSize(new java.awt.Dimension(280, 0));
        tabel.setPreferredSize(new java.awt.Dimension(285, 44));

        jScrollPane1.setBorder(null);
        jScrollPane1.setVerticalScrollBar(scrollBar1);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bahan baku", "Sisa Stok"
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

        javax.swing.GroupLayout tabelLayout = new javax.swing.GroupLayout(tabel);
        tabel.setLayout(tabelLayout);
        tabelLayout.setHorizontalGroup(
            tabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabelLayout.setVerticalGroup(
            tabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(stats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tabel, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(scrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel1))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .addComponent(stats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.swing.PanelRound card1;
    private com.raven.swing.PanelRound card2;
    private com.raven.swing.PanelRound card3;
    private com.raven.chart.Chart chart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.ScrollBar scrollBar1;
    private com.raven.swing.PanelRound stats;
    private com.raven.swing.PanelRound tabel;
    private com.raven.swing.TableColumn table;
    // End of variables declaration//GEN-END:variables
}
