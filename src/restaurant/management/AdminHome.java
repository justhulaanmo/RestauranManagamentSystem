package restaurant.management;

public class AdminHome extends javax.swing.JFrame {
    
    private int adminId;
    
    public AdminHome() {
        
            this.adminId = Login.adminId; 
            initComponents();
            jLabel1.requestFocus();
            setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        manageproduct = new javax.swing.JButton();
        orderproduct = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1200, 675));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(237, 226, 219));
        jButton1.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        jButton1.setText("Sales");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 510, 150, 40));

        manageproduct.setBackground(new java.awt.Color(237, 226, 219));
        manageproduct.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        manageproduct.setText("Manage Products");
        manageproduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageproductActionPerformed(evt);
            }
        });
        jPanel1.add(manageproduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 330, 150, 40));

        orderproduct.setBackground(new java.awt.Color(237, 226, 219));
        orderproduct.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        orderproduct.setText("Order Product");
        orderproduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderproductActionPerformed(evt);
            }
        });
        jPanel1.add(orderproduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 390, 150, 40));

        jButton5.setBackground(new java.awt.Color(237, 226, 219));
        jButton5.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        jButton5.setText("Profiles");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 330, 150, 40));

        jButton6.setBackground(new java.awt.Color(237, 226, 219));
        jButton6.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        jButton6.setText("Stock Ordered");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 450, 150, 40));

        jButton7.setBackground(new java.awt.Color(237, 226, 219));
        jButton7.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        jButton7.setText("Customer's Orders");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 390, 150, 40));

        jLabel2.setFont(new java.awt.Font("Kannada MN", 1, 50)); // NOI18N
        jLabel2.setText("W E L C O M E");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 230, -1, -1));

        jLabel3.setFont(new java.awt.Font("Kannada MN", 0, 24)); // NOI18N
        jLabel3.setText("Five Star Bites");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 340, -1, -1));

        jLabel4.setFont(new java.awt.Font("Kannada MN", 0, 24)); // NOI18N
        jLabel4.setText("Management System");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 380, -1, -1));

        jLabel5.setFont(new java.awt.Font("Kannada MN", 0, 24)); // NOI18N
        jLabel5.setText("to");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 300, -1, -1));

        logout.setFont(new java.awt.Font("Telugu MN", 1, 18)); // NOI18N
        logout.setText("Logout");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        jPanel1.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 610, -1, -1));

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("CUSTOMER");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 280, 150, -1));

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 24)); // NOI18N
        jLabel8.setText("ADMIN");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 280, -1, -1));

        jButton2.setBackground(new java.awt.Color(237, 226, 219));
        jButton2.setFont(new java.awt.Font("Lao MN", 1, 13)); // NOI18N
        jButton2.setText("Cart");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 570, 150, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/FIVE STAR BITES (1200 x 675 px).png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void manageproductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageproductActionPerformed
                                        
            int adminId = Login.adminId; 
            this.dispose();  
            ManageProduct manage = new ManageProduct(); 
            manage.setVisible(true); 
     
    }//GEN-LAST:event_manageproductActionPerformed

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked

            this.dispose();  
            Login lg = new Login();
            lg.setVisible(true);  
            
    }//GEN-LAST:event_logoutMouseClicked

    private void orderproductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderproductActionPerformed
        
            int adminId = Login.adminId; 
            this.dispose();  
            OrderProduct manage = new OrderProduct();
            manage.setVisible(true); 
        
    }//GEN-LAST:event_orderproductActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
     
            int adminId = Login.adminId; 
            this.dispose();  
            ViewCart manage = new ViewCart();
            manage.setVisible(true);
            
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
            int adminId = Login.adminId; 
            this.dispose();  
            CustomerProfile manage = new CustomerProfile();
            manage.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        
            this.dispose();  
            CustomersOrdered manage = new CustomersOrdered();
            manage.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            this.dispose();  
            Sales manage = new Sales();
            manage.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
            this.dispose();  
            Admin_AddtoCart manage = new Admin_AddtoCart();
            manage.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminHome.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminHome().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel logout;
    private javax.swing.JButton manageproduct;
    private javax.swing.JButton orderproduct;
    // End of variables declaration//GEN-END:variables
}
