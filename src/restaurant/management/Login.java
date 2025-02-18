package restaurant.management;

import structure.connectiontodatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.awt.Color;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import structure.customer_info;


public class Login extends javax.swing.JFrame {

    private Connection con; // Declare connection at class level
    public static int adminId;
    public static int customerID;
    public static String currentUser; // Declare currentUser globally

    public static String getCurrentUser() {
        return currentUser;
    }


    public Login() {

        initComponents();
        background.requestFocus();
        setLocationRelativeTo(null);
        con = connectiontodatabase.getConnection();
        
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        //*****************************************FOR @ AND 09********************************************//
        lg_username.setText("@");
        lg_username.setForeground(Color.GRAY);
        
        // FocusAdapter is abstract class
        lg_username.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (lg_username.getText().equals("@")) {
                lg_username.setText("@");
                lg_username.setForeground(Color.BLACK);

            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (lg_username.getText().isEmpty()) {
                lg_username.setText("@");
                lg_username.setForeground(Color.GRAY);   } 
        }
    });
        
   
//***********************************************************************************************************************************************//
        
        //interface that listens to an event example button is clicked
        
        loginbtn.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Get username and password from input fields
        String username = lg_username.getText().trim();
        String password = new String(lg_password.getPassword());

         // Authenticate user and get user type which has a method
        String userType/*(combobox)*/ = authenticateUser(username, password);

        if (userType.equals("Admin")) {
            AdminHome adminHome = new AdminHome();
            adminHome.setVisible(true);
            dispose(); 
            
        } else if (userType.equals("Customer")) {
            dashboard_menu customerHome = new dashboard_menu();
            //System.out.println(""+customerID);
            customerHome.setVisible(true);
            dispose(); 
        } 
    }
});
        }
    
//***********************************************************************************************************************************************//
        
        //method for authenthication
        private String authenticateUser(String username, String password) {
        Connection conn = connectiontodatabase.getConnection();
    
        if (conn != null) {
        String selectedUserType = usertypecombobox.getSelectedItem().toString();
        
        try{
        
        if (selectedUserType.equals("Admin")) {
            // Query for admin login
            String query = "SELECT admin_id FROM admins WHERE BINARY username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    adminId = rs.getInt("admin_id");  
                    return "Admin"; 
                }
            } 
            
        } else if (selectedUserType.equals("Customer")) {
            // Query for customer login
            String query = "SELECT customer_id FROM signup WHERE BINARY username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    customerID = rs.getInt("customer_id");  
                    return "Customer"; 
            }
            }
        }
        
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
        
            return "none";
}

//*******************************************************CUSTOMER INFOTMATION (DASHBOARD)***************************************************************************************//
        
        public static customer_info getUserInfo(String username) {
        Connection conn = connectiontodatabase.getConnection();
        if (conn != null) {
            String query = "SELECT * FROM signup WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return new customer_info(rs.getString("username"), rs.getString("name"), rs.getString("address"), rs.getString("email"), rs.getString("mobile_number"), rs.getString("password"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
        }
        
//***********************************************************************************************************************************************//

        
    
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lg_username = new javax.swing.JTextField();
        jlabel = new javax.swing.JLabel();
        loginbtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        createaccount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lg_password = new javax.swing.JPasswordField();
        usertypecombobox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 675));
        setResizable(false);
        setSize(new java.awt.Dimension(1200, 675));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("username");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 330, -1, -1));

        lg_username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lg_usernameKeyReleased(evt);
            }
        });
        getContentPane().add(lg_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 360, 323, -1));

        jlabel.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jlabel.setText("password");
        getContentPane().add(jlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 390, -1, -1));

        loginbtn.setBackground(new java.awt.Color(231, 230, 221));
        loginbtn.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        loginbtn.setText("Login");
        loginbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtnActionPerformed(evt);
            }
        });
        getContentPane().add(loginbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 560, -1, -1));

        jLabel2.setText("no account, yet?");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 640, -1, -1));

        createaccount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        createaccount.setText("create account");
        createaccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                createaccountMouseClicked(evt);
            }
        });
        getContentPane().add(createaccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 640, -1, -1));

        jLabel4.setFont(new java.awt.Font("Kannada MN", 1, 50)); // NOI18N
        jLabel4.setText("LOG IN");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 230, -1, -1));
        getContentPane().add(lg_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 420, 320, -1));

        usertypecombobox.setBackground(new java.awt.Color(231, 230, 221));
        usertypecombobox.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
        usertypecombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Customer", "Admin" }));
        usertypecombobox.setToolTipText("");
        getContentPane().add(usertypecombobox, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 490, 100, -1));

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel3.setText("Type of User:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 490, -1, -1));

        jLabel5.setText("forgot password?");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 450, -1, -1));

        jToggleButton1.setText("show");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 420, 70, -1));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/FIVE STAR BITES (1200 x 675 px).png"))); // NOI18N
        getContentPane().add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtnActionPerformed
                                                                                                                          
            String username = lg_username.getText().trim();
            String password = new String(lg_password.getPassword());
            
            lg_username.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
            lg_password.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));

            if (username.isEmpty() || username.equals("@") || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields correctly!", "WARNING", JOptionPane.WARNING_MESSAGE);

                // Highlight empty fields
                lg_username.setBorder(username.equals("@") ? javax.swing.BorderFactory.createLineBorder(Color.red) : javax.swing.BorderFactory.createLineBorder(Color.black));
                lg_password.setBorder(password.isEmpty() ? javax.swing.BorderFactory.createLineBorder(Color.red) : javax.swing.BorderFactory.createLineBorder(Color.black));
                return;
            }
            
//**********************************************************************************************************************************************//
           
            String usertype = authenticateUser(username, password);

            switch (usertype) {
                case "Admin":
           
                JOptionPane.showMessageDialog(this, "Welcome Admin!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                AdminHome manage = new AdminHome();
                manage.setVisible(true);
                this.dispose(); 
                break;

                case "Customer":
                
                JOptionPane.showMessageDialog(this, "Welcome " + username + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                dashboard_menu menu = new dashboard_menu();
                menu.setVisible(true);
                this.dispose(); 
                break;

                case "none":
                
                JOptionPane.showMessageDialog(this, "Invalid credentials username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                break;

                case "error":
                
                JOptionPane.showMessageDialog(this, "An error occurred while trying to log in. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
    }
    }//GEN-LAST:event_loginbtnActionPerformed

    private void createaccountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_createaccountMouseClicked
        
        Signup sign = new Signup();
        sign.setVisible(true);
        sign.pack();
        sign.setLocationRelativeTo(null);
        this.dispose();
        
    }//GEN-LAST:event_createaccountMouseClicked

    private void lg_usernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lg_usernameKeyReleased

    }//GEN-LAST:event_lg_usernameKeyReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        
     if (jToggleButton1.isSelected()) {
        // Show password
        lg_password.setEchoChar((char) 0);
        jToggleButton1.setText("Hide");
    } else {
        // Hide password
        lg_password.setEchoChar('*'); 
        jToggleButton1.setText("Show");
    }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
     
        this.dispose();  
        ForgotPass add = new ForgotPass();
        add.setVisible(true); 
        
    }//GEN-LAST:event_jLabel5MouseClicked

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel createaccount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel jlabel;
    private javax.swing.JPasswordField lg_password;
    private javax.swing.JTextField lg_username;
    private javax.swing.JButton loginbtn;
    private javax.swing.JComboBox<String> usertypecombobox;
    // End of variables declaration//GEN-END:variables
}
