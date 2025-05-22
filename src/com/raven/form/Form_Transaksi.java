package com.raven.form;

import com.raven.swing.ModernScrollBarUI;
import config.DatabaseConfig;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Form_Transaksi extends javax.swing.JPanel {

    Connection con = null;
    DefaultTableModel tableModel;
    DefaultTableModel tableModelMenu;
    private static double totalBayar = 0;
    private boolean isMember = false;
    private String RFIDId = "";
    private long lastTime = 0;
    private final long RFID_THRESHOLD = 100;

    public Form_Transaksi() {
        initComponents();
        getCon();
        setModel();
        setOpaque(false);
        loadData();
        if (!isMember) {
            indikatorMember.setVisible(false);
            poin.setVisible(false);
        }
        
        jScrollPane3.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    }

    private void getCon() {
        try {
            con = DatabaseConfig.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setModel() {
        String[] judul = {"Kode Menu", "Nama Menu", "Jumlah", "Harga", "Harga Total", "Aksi"};
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

    private void searchData() {
        String kodeMenu = inputKode.getText();
        if (con != null) {
            try {
                String qCari = "SELECT * FROM v_porsi_menu WHERE kode_menu = ?";
                PreparedStatement ps = con.prepareStatement(qCari);
                ps.setString(1, kodeMenu);
                ResultSet rs = ps.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                        inputQty.setText("1");
                        String namaMenu = rs.getString(2);
                        int stok = rs.getInt(3);
                        int jumlah = Integer.parseInt(inputQty.getText());
                        double harga = Double.parseDouble(rs.getString(4));
                        double totalHarga = jumlah * harga;
                        inputMenu.setText(rs.getString("nama_menu"));
                        boolean cekKode = false;
                        boolean cekStok = false; //cek stok saat melakukan tambah pesanan
                        if (stok >= 0) {
                            //cek apakah ada kode menu yang sama
                            int row = tblPesanan.getRowCount();
                            for (int i = 0; i < row; i++) {
                                //jika ada akan menambahkan jumlah menu tersebut
                                int stokTabel = (int) tblPesanan.getValueAt(i, 2);
                                if (tblPesanan.getValueAt(i, 0).equals(kodeMenu) && stokTabel < stok) {
                                    int jumlahBaru = Integer.parseInt(tblPesanan.getValueAt(i, 2).toString());
                                    tblPesanan.setValueAt(jumlahBaru + jumlah, i, 2);
                                    double total = (jumlahBaru + jumlah) * harga;
                                    tblPesanan.setValueAt(total, i, 4);
                                    cekKode = true;
                                    totalBayar += totalHarga;
                                    break;
                                } else {
                                    cekStok = true;
                                    JOptionPane.showMessageDialog(null, "Stok Tidak Mencukupi!");
                                }
                            }
                            //jika tidak ada menu yang sama, maka akan menambahkan baris baru
                            if (!cekKode) {
                                tableModel.addRow(new Object[]{kodeMenu, namaMenu, jumlah, harga, totalHarga});
                                totalBayar += totalHarga;
                            }
                            //set total

                        } else {
                            JOptionPane.showMessageDialog(null, "Stok Habis!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Kode Menu Tidak Terdaftar!");
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
        totalHarga = new javax.swing.JLabel();
        inputKode = new com.raven.util.TextField();
        inputMenu = new com.raven.util.TextField();
        inputSub = new com.raven.util.TextField();
        inputQty = new com.raven.util.TextField();
        jLabel14 = new javax.swing.JLabel();
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
        indikatorMember = new javax.swing.JLabel();
        member = new com.raven.util.TextField();
        poin = new javax.swing.JPanel();
        poinField = new com.raven.util.TextField();
        jLabel6 = new javax.swing.JLabel();
        pakePoin = new javax.swing.JCheckBox();
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

        panelRound2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("TOTAL:");
        panelRound2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, 35));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel10.setText("Rp. 0");
        panelRound2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(593, 6, -1, 85));

        totalHarga.setFont(new java.awt.Font("Bahnschrift", 1, 50)); // NOI18N
        totalHarga.setText("0");
        panelRound2.add(totalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 400, -1));

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 15, 490, 100));

        inputKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKodeActionPerformed(evt);
            }
        });
        inputKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputKodeKeyPressed(evt);
            }
        });
        panelRound1.add(inputKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 31, 244, -1));
        panelRound1.add(inputMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 88, 198, -1));
        panelRound1.add(inputSub, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 200, -1));
        panelRound1.add(inputQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 88, 44, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("QTY");
        panelRound1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 72, -1, -1));
        panelRound1.add(inputBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 500, 200, -1));
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

        indikatorMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/person.png"))); // NOI18N
        indikatorMember.setToolTipText("Member");
        panelRound1.add(indikatorMember, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 157, -1, -1));

        member.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberActionPerformed(evt);
            }
        });
        member.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                memberKeyTyped(evt);
            }
        });
        panelRound1.add(member, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 150, 240, -1));

        poin.setBackground(new java.awt.Color(33, 53, 85));
        poin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        poinField.setEditable(false);
        poinField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                poinFieldActionPerformed(evt);
            }
        });
        poinField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                poinFieldKeyTyped(evt);
            }
        });
        poin.add(poinField, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 260, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("POIN");
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 0));
        poin.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pakePoin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pakePoin.setForeground(new java.awt.Color(255, 255, 255));
        pakePoin.setText("Gunakan Poin");
        pakePoin.setBorder(null);
        poin.add(pakePoin, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 100, -1));

        panelRound1.add(poin, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

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

    private void inputKodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKodeKeyPressed
        if (evt.getKeyChar() == '\n') {
            searchData();
        }
    }//GEN-LAST:event_inputKodeKeyPressed

    private void memberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memberActionPerformed

    private void cariMember(boolean isRFID) {
        try {
            String query = "";
            if (isRFID) {
                query = "SELECT * FROM member WHERE uid = ? LIMIT 1";
            } else {
                query = "SELECT * FROM member WHERE no_telp_member = ? LIMIT 1";
            }
            String rfid = RFIDId;
            try (PreparedStatement ps = con.prepareStatement(query)) {
                if (isRFID) {
                    ps.setString(1, rfid);
                } else {
                    ps.setString(1, member.getText());
                }

                ResultSet hasil = ps.executeQuery();
                if (hasil.next()) {
                    member.setText(hasil.getString("nama_member"));
                    poinField.setText(hasil.getDouble("point")+"");
                    isMember = true;
                    indikatorMember.setVisible(true);
                    poin.setVisible(true);
                } else {
                    if (isRFID) {
                        JOptionPane.showMessageDialog(this, "RFID Tidak terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Nomor Tidak terdaftar!", "Error", JOptionPane.PLAIN_MESSAGE);
                    }
                    member.setText("");
                    poinField.setText("");
                    pakePoin.setSelected(false);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void memberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_memberKeyTyped
        long currentTime = System.currentTimeMillis();
        char c = evt.getKeyChar();

        // Reset RFIDId kalau inputnya lambat
        if (lastTime != 0 && (currentTime - lastTime) > RFID_THRESHOLD) {
            RFIDId = "";
        }

        RFIDId += c;
        lastTime = currentTime;

        if (c == '\n' || c == '\r') {
            RFIDId = RFIDId.replace("\n", "");
            RFIDId = RFIDId.replace("\r", "");
            if (RFIDId.length() >= 9) {
                System.out.println("Scan RFID Terdeteksi: " + RFIDId);
                cariMember(true);
            } else {
                //                System.out.println("RFID tidak valid, panjang kurang dari 9 karakter.");
                if (isNumeric(member)) {
                    cariMember(false);
                } else {
                    pakePoin.setSelected(false);
                    poinField.setText("");
                    indikatorMember.setVisible(false);
                    poin.setVisible(false);
                }
            }
            // Kosongkan RFIDId setelah pemrosesan
            RFIDId = "";
            //            userInput.setText("");
        }
    }//GEN-LAST:event_memberKeyTyped

    private void poinFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_poinFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_poinFieldActionPerformed

    private void poinFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_poinFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_poinFieldKeyTyped

    private void inputKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputKodeActionPerformed

    public static boolean isNumeric(JTextField textField) {
        String text = textField.getText();
        if (text.isEmpty()) {
            return false; // or handle empty case as needed
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel indikatorMember;
    private com.raven.util.TextField inputBayar;
    private com.raven.util.TextField inputKembalian;
    private com.raven.util.TextField inputKode;
    private com.raven.util.TextField inputMenu;
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
    private com.raven.util.TextField member;
    private javax.swing.JCheckBox pakePoin;
    private com.raven.swing.PanelRound panelRound1;
    private com.raven.swing.PanelRound panelRound2;
    private javax.swing.JPanel poin;
    private com.raven.util.TextField poinField;
    private com.raven.swing.TableColumn tblMenu;
    private com.raven.swing.TableColumn tblPesanan;
    private javax.swing.JLabel totalHarga;
    // End of variables declaration//GEN-END:variables
}
