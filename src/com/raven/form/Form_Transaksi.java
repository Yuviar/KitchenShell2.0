package com.raven.form;

import config.DatabaseConfig;
import java.awt.event.KeyAdapter;
import java.sql.*;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Form_Transaksi extends javax.swing.JPanel {

    Connection con = null;
    DefaultTableModel tableModel;
    DefaultTableModel tableModelMenu;

    public Form_Transaksi() {
        initComponents();
        getCon();
        setModel();
        setOpaque(false);
        SwingUtilities.invokeLater(() -> {

            String[] items = {"Apple", "Banana", "Cherry", "Date", "Grape", "Lemon", "Orange", "Peach", "Strawberry", "Watermelon"};
            comboBox.setEditable(true);

            JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();

            editor.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    String input = editor.getText();
                    comboBox.hidePopup();
                    comboBox.removeAllItems();

                    for (String item : items) {
                        if (item.toLowerCase().contains(input.toLowerCase())) {
                            comboBox.addItem(item);
                        }
                    }

                    editor.setText(input); // keep the text
                    comboBox.showPopup();
                }
            });
        });
        loadData();
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setModel() {
        String[] judul = {"Kode Menu", "Nama Menu", "Harga", "Jumlah", "Harga Total", "Aksi"};
        String[] judulMenu = {"Nama Menu", "Stok"};
        tableModel = new DefaultTableModel(judul, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModelMenu = new DefaultTableModel(judulMenu, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPesanan.setModel(tableModel);
        tblMenu.setModel(tableModelMenu);
    }

    private void loadData() {
        if (con != null) {
            try {
                String q = "SELECT nama_menu, jumlah_porsi FROM v_porsi_menu";
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String[] data = {rs.getString(1), rs.getString(2)};
                    tableModelMenu.addRow(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new com.raven.swing.PanelRound();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelRound2 = new com.raven.swing.PanelRound();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        inputKode = new com.raven.util.TextField();
        inputMenu = new com.raven.util.TextField();
        inputNama = new com.raven.util.TextField();
        inputSub = new com.raven.util.TextField();
        inputQty = new com.raven.util.TextField();
        jLabel14 = new javax.swing.JLabel();
        checkPoint = new javax.swing.JCheckBox();
        inputBayar = new com.raven.util.TextField();
        inputKembalian = new com.raven.util.TextField();
        labelSelesai = new com.raven.util.Button();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblMenu = new com.raven.swing.TableColumn();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPesanan = new com.raven.swing.TableColumn();
        comboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        panelRound1.setBackground(new java.awt.Color(33, 53, 85));
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("KODE MENU");
        panelRound1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("NAMA PELANGGAN");
        panelRound1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 129, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("TOTAL:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel10.setText("Rp. 0");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(524, 524, 524)
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(panelRound2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 15, 490, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("MEMBER");
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 0));
        panelRound1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

        inputKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKodeActionPerformed(evt);
            }
        });
        panelRound1.add(inputKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 31, 244, -1));

        inputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputMenuActionPerformed(evt);
            }
        });
        panelRound1.add(inputMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 88, 198, -1));

        inputNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputNamaActionPerformed(evt);
            }
        });
        panelRound1.add(inputNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 151, 244, -1));

        inputSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputSubActionPerformed(evt);
            }
        });
        panelRound1.add(inputSub, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 200, -1));

        inputQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputQtyActionPerformed(evt);
            }
        });
        panelRound1.add(inputQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 88, 44, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("QTY");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 72, -1, -1));

        checkPoint.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        checkPoint.setForeground(new java.awt.Color(255, 255, 255));
        checkPoint.setText("Gunakan Poin");
        checkPoint.setBorder(null);
        checkPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkPointActionPerformed(evt);
            }
        });
        panelRound1.add(checkPoint, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 130, 100, -1));

        inputBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputBayarActionPerformed(evt);
            }
        });
        panelRound1.add(inputBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 500, 200, -1));

        inputKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKembalianActionPerformed(evt);
            }
        });
        panelRound1.add(inputKembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 500, 200, -1));

        labelSelesai.setBackground(new java.awt.Color(97, 131, 175));
        labelSelesai.setForeground(new java.awt.Color(255, 255, 255));
        labelSelesai.setText("SELESAI");
        labelSelesai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        panelRound1.add(labelSelesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 500, 130, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("NAMA MENU");
        panelRound1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 72, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("SUBTOTAL");
        panelRound1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("BAYAR");
        panelRound1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 480, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("KEMBALIAN");
        panelRound1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 480, -1, -1));

        jScrollPane3.setBorder(null);

        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NAMA MENU", "STOK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblMenu);

        panelRound1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 150, 210, 320));

        jScrollPane2.setBorder(null);

        tblPesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "KODE", "NAMA MENU", "HARGA", "QTY", "SUBTOTAL", "AKSI"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPesanan.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblPesanan);
        if (tblPesanan.getColumnModel().getColumnCount() > 0) {
            tblPesanan.getColumnModel().getColumn(1).setPreferredWidth(135);
            tblPesanan.getColumnModel().getColumn(3).setPreferredWidth(40);
        }

        panelRound1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 198, 530, 270));

        comboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBox.setBorder(null);
        comboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelRound1.add(comboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 152, 260, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(33, 53, 85));
        jLabel1.setText("TRANSAKSI");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        jLabel1.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 780, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkPointActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkPointActionPerformed

    private void inputQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputQtyActionPerformed

    private void inputNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputNamaActionPerformed

    private void inputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputMenuActionPerformed

    private void inputKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKodeActionPerformed

    private void inputSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputSubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputSubActionPerformed

    private void inputBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputBayarActionPerformed

    private void inputKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKembalianActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkPoint;
    private javax.swing.JComboBox<String> comboBox;
    private com.raven.util.TextField inputBayar;
    private com.raven.util.TextField inputKembalian;
    private com.raven.util.TextField inputKode;
    private com.raven.util.TextField inputMenu;
    private com.raven.util.TextField inputNama;
    private com.raven.util.TextField inputQty;
    private com.raven.util.TextField inputSub;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.raven.util.Button labelSelesai;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.PanelRound panelRound2;
    private com.raven.swing.TableColumn tblMenu;
    private com.raven.swing.TableColumn tblPesanan;
    // End of variables declaration//GEN-END:variables
}
