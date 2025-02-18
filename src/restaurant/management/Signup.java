package restaurant.management;

import structure.connectiontodatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Signup extends javax.swing.JFrame {

    public Signup() {
        initComponents();
        jLabel7.requestFocus();
        setLocationRelativeTo(null);

        //*****************************FOR @ AND 09 KEY LISTENER**********************************//
        username.setText("@");
        username.setForeground(Color.GRAY);

        // FocusAdapter is abstract class
        username.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (username.getText().equals("@")) {
                    username.setText("@");
                    username.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (username.getText().isEmpty()) {
                    username.setText("@");
                    username.setForeground(Color.GRAY);
                }
            }
        });
        //****************************************************************************************//
        mobilenumber.setText("09");
        mobilenumber.setForeground(Color.GRAY);
        mobilenumber.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (mobilenumber.getText().equals("09")) {
                    mobilenumber.setText("09");
                    mobilenumber.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (mobilenumber.getText().isEmpty()) {
                    mobilenumber.setText("09");
                    mobilenumber.setForeground(Color.GRAY);
                }
            }
        });

        mobilenumber.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                // Limit the input to 11 characters
                if (mobilenumber.getText().length() >= 11) {
                    evt.consume();
                }
            }
        });
        username.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (username.getText().equals(" already exists!")) {
                    username.setText("");  // Clear the error message
                    username.setForeground(Color.BLACK);  // Reset color to black
                }
            }
        });
        //****************************************************************************************//

    }
    //****************************************************************************************//

    public static boolean isUsernameExist(String username) {
        Connection con = connectiontodatabase.getConnection();
        if (con != null) {
            String query = "SELECT username FROM signup WHERE username = ?";
            try ( PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, username);
                try ( ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return true;  // Username exists
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;  // Username does not exist
    }
    
    public static boolean isEmailExist(String email) {
    Connection con = connectiontodatabase.getConnection();
    if (con != null) {
        String query = "SELECT email FROM signup WHERE email = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;  // Email exists
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return false;  // Email does not exist
}


    public static boolean registerUser(String name, String email, String mobilenumber, String address, String username, String password) {
        if (isUsernameExist(username)) {
            return false;  // Return false if the username exists
        }

        // Continue with the registration process
        Connection con = connectiontodatabase.getConnection();
        if (con != null) {
            String query = "INSERT INTO signup (name, email, mobile_number, address, username, password) VALUES (?, ?, ?, ?, ?, ?)";
            try ( PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, mobilenumber);
                pstmt.setString(4, address);
                pstmt.setString(5, username);
                pstmt.setString(6, password);
                pstmt.executeUpdate();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        mobilenumber = new javax.swing.JTextField();
        username = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        address = new javax.swing.JTextArea();
        create = new javax.swing.JButton();
        title_signup = new javax.swing.JLabel();
        haveanaccountl_abel = new javax.swing.JLabel();
        login_menu = new javax.swing.JLabel();
        fn = new javax.swing.JLabel();
        num = new javax.swing.JLabel();
        userr = new javax.swing.JLabel();
        passs = new javax.swing.JLabel();
        eemail = new javax.swing.JLabel();
        home = new javax.swing.JLabel();
        show1 = new javax.swing.JToggleButton();
        password = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 675));
        setResizable(false);
        setSize(new java.awt.Dimension(1200, 675));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Name");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, -1, -1));

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel5.setText("Email");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 210, 60, -1));

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setText("Mobile Number");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 250, -1, -1));

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel3.setText("Delivery Address & Landmark");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 310, 250, -1));

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel4.setText("Password");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 410, -1, -1));

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel6.setText("Username");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 320, -1, -1));

        name.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameActionPerformed(evt);
            }
        });
        name.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nameKeyReleased(evt);
            }
        });
        getContentPane().add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 210, 260, 30));

        email.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        email.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                emailKeyReleased(evt);
            }
        });
        getContentPane().add(email, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 240, 260, 30));

        mobilenumber.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        mobilenumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobilenumberKeyReleased(evt);
            }
        });
        getContentPane().add(mobilenumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 280, 260, 30));

        username.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                usernameKeyReleased(evt);
            }
        });
        getContentPane().add(username, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, 260, 30));

        address.setColumns(20);
        address.setRows(5);
        address.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                addressKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(address);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 340, 260, 106));

        create.setBackground(new java.awt.Color(231, 230, 221));
        create.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        create.setText("Create Account");
        create.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createActionPerformed(evt);
            }
        });
        getContentPane().add(create, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 540, 170, 40));

        title_signup.setFont(new java.awt.Font("Kannada MN", 1, 50)); // NOI18N
        title_signup.setText("Sign Up");
        getContentPane().add(title_signup, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, -1, -1));

        haveanaccountl_abel.setText("have an account?");
        getContentPane().add(haveanaccountl_abel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 610, -1, -1));

        login_menu.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        login_menu.setText("login here");
        login_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                login_menuMouseClicked(evt);
            }
        });
        getContentPane().add(login_menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 610, -1, -1));

        fn.setBackground(new java.awt.Color(255, 255, 255));
        fn.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        fn.setForeground(new java.awt.Color(255, 51, 51));
        fn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fnKeyReleased(evt);
            }
        });
        getContentPane().add(fn, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 210, 108, 23));

        num.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        num.setForeground(new java.awt.Color(255, 51, 51));
        num.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                numKeyReleased(evt);
            }
        });
        getContentPane().add(num, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 280, 108, 23));

        userr.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        userr.setForeground(new java.awt.Color(255, 51, 51));
        userr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userrKeyReleased(evt);
            }
        });
        getContentPane().add(userr, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 380, 260, 23));

        passs.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        passs.setForeground(new java.awt.Color(255, 51, 51));
        passs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passsKeyReleased(evt);
            }
        });
        getContentPane().add(passs, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 470, 350, 23));

        eemail.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        eemail.setForeground(new java.awt.Color(255, 51, 51));
        eemail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                eemailKeyReleased(evt);
            }
        });
        getContentPane().add(eemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 270, 260, 23));

        home.setFont(new java.awt.Font("Helvetica Neue", 1, 12)); // NOI18N
        home.setForeground(new java.awt.Color(255, 51, 51));
        home.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                homeKeyReleased(evt);
            }
        });
        getContentPane().add(home, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 450, 260, 23));

        show1.setText("show");
        show1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show1ActionPerformed(evt);
            }
        });
        getContentPane().add(show1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 440, 70, 30));

        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordKeyReleased(evt);
            }
        });
        getContentPane().add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 440, 260, 30));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/signup (1200 x 675 px) copy.png"))); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameActionPerformed

    }//GEN-LAST:event_nameActionPerformed

    private void createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createActionPerformed
        String namee = name.getText();
    String mail = email.getText();
    String num = mobilenumber.getText();
    String add = address.getText();
    String usernamee = username.getText();
    String passs = password.getText();

    // Check if all fields are filled out
    if (usernamee.isEmpty() || passs.isEmpty() || namee.isEmpty() || num.isEmpty() || add.isEmpty() || mail.isEmpty()) {
        JOptionPane.showMessageDialog(this, "PLEASE FILL OUT ALL FIELDS", "ERROR", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validate input formats
    if (!namee.matches("^[a-zA-Z\\s'-]{1,255}$")  // Name: only letters, spaces, and some punctuation
        || !num.matches("^09[0-9]{9}$")  // Mobile number: should start with "09" and be followed by 9 digits
        || !usernamee.matches("^@[a-zA-Z0-9._]{1,255}$")  // Username: must start with "@" followed by letters, numbers, or underscores
        || !mail.matches("^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{1,255}$")  // Email: standard email format validation
        || !add.matches("^[a-zA-Z0-9\\s,.'-()]{1,255}$")  // Address: allows letters, digits, and basic punctuation
        || passs.length() < 6  // Password length: minimum 6 characters
        || !passs.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@,._])[A-Za-z\\d@,._]{6,255}$")) {  // Strong password: upper, lower, number, special char
    JOptionPane.showMessageDialog(this, "Please input the correct format in the wrong field.", "ERROR", JOptionPane.WARNING_MESSAGE);
    return;
}
    
    // Check if email already exists
    boolean isEmailExist = Signup.isEmailExist(mail);

// Check if username already exists
    boolean isUsernameExist = Signup.isUsernameExist(usernamee);


    if (isEmailExist && isUsernameExist) {
    eemail.setText("Email is already registered!");
    eemail.setForeground(Color.RED);
    userr.setText("Username already exists!");
    userr.setForeground(Color.RED);
    return;  
    }
    else if (isEmailExist) {
        eemail.setText("Email is already registered!");
        eemail.setForeground(Color.RED);
        return;  // Stop the process if email exists
    }

    // Check if username already exists
    else if (isUsernameExist) {
        userr.setText("Username already exists!");
        userr.setForeground(Color.RED);
        return;  // Stop the process if username exists
    }

    // Proceed with the registration if username and email are unique
    if (Signup.registerUser(namee, mail, num, add, usernamee, passs)) {
        JOptionPane.showMessageDialog(null, "Signup successful!");
        new Login().setVisible(true);
        dispose(); // Close Signup Frame
    } else {
        JOptionPane.showMessageDialog(null, "Signup failed!");
    }

    }//GEN-LAST:event_createActionPerformed

    private void login_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_login_menuMouseClicked

        Login lg = new Login();
        lg.setVisible(true);
        lg.pack();
        lg.setLocationRelativeTo(null);
        this.dispose();

    }//GEN-LAST:event_login_menuMouseClicked

    private void fnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fnKeyReleased

    }//GEN-LAST:event_fnKeyReleased

    private void numKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numKeyReleased

    }//GEN-LAST:event_numKeyReleased

    private void userrKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userrKeyReleased

    }//GEN-LAST:event_userrKeyReleased

    private void passsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passsKeyReleased

    }//GEN-LAST:event_passsKeyReleased

    private void eemailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_eemailKeyReleased

    }//GEN-LAST:event_eemailKeyReleased

    private void homeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_homeKeyReleased

    }//GEN-LAST:event_homeKeyReleased

    private void nameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameKeyReleased

        String PATTERN = "^[a-zA-Z\\s'-]{1,255}$";  // Allow letters and spaces
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(name.getText());
        if (!match.matches()) {
            fn.setText("Invalid Input");
        } else {
            fn.setText(null);  // Clear the error message
        }

    }//GEN-LAST:event_nameKeyReleased

    private void mobilenumberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobilenumberKeyReleased

        String PATTERN = "^09[0-9]{9}$";  // Ensure phone number is 11 digits starting with 09
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(mobilenumber.getText());
        if (!match.matches()) {
            num.setText("Invalid Input");
        } else {
            num.setText(null);
        }
    }//GEN-LAST:event_mobilenumberKeyReleased

    private void usernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernameKeyReleased

     String PATTERN = "^@[a-zA-Z0-9._]{1,255}$"; // Adjusted regex
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(username.getText());
    if(!match.matches()){
        userr.setText("Invalid Input");
    }
    else{
        userr.setText(null); 
    }
    }//GEN-LAST:event_usernameKeyReleased

    private void emailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailKeyReleased

        String PATTERN = "^[a-zA-Z0-9._%+-]+@[a-z.-]+\\.[a-z]{1,255}$"; // Fixed dot escaping
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(email.getText());
        if (!match.matches()) {
            eemail.setText("Invalid Input");
        } else {
            eemail.setText(null);
        }
    }//GEN-LAST:event_emailKeyReleased

    private void addressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addressKeyReleased

        String PATTERN = "^[a-zA-Z0-9\\s,.'-()]{1,255}$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(address.getText());
        if (!match.matches()) {
            home.setText("Invalid Input");
        } 
        else {
            home.setText(null);
        }
    }//GEN-LAST:event_addressKeyReleased

    private void show1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show1ActionPerformed
        if (show1.isSelected()) {
            // Show password
            password.setEchoChar((char) 0); // Makes the password visible
            show1.setText("Hide");
        } else {
            // Hide password
            password.setEchoChar('•'); // Restores the hidden password (you can use '*' or any other character)
            show1.setText("Show");
        }
    }//GEN-LAST:event_show1ActionPerformed

    private void passwordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordKeyReleased
       String PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@,._])[A-Za-z\\d@,._]{6,255}$";
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(password.getText());
    if (!match.matches()) {
        passs.setText("Password must include: upper, lower, number & special char");
    } else {
        passs.setText(null);  // Clear the error message if the password is valid
    }
    }//GEN-LAST:event_passwordKeyReleased

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
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Signup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Signup().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextArea address;
    private javax.swing.JButton create;
    public javax.swing.JLabel eemail;
    public javax.swing.JTextField email;
    private javax.swing.JLabel fn;
    private javax.swing.JLabel haveanaccountl_abel;
    public javax.swing.JLabel home;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel login_menu;
    public javax.swing.JTextField mobilenumber;
    public javax.swing.JTextField name;
    public javax.swing.JLabel num;
    public javax.swing.JLabel passs;
    private javax.swing.JPasswordField password;
    private javax.swing.JToggleButton show1;
    private javax.swing.JLabel title_signup;
    public javax.swing.JTextField username;
    public javax.swing.JLabel userr;
    // End of variables declaration//GEN-END:variables
}
