package restaurant.management;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import structure.connectiontodatabase;

public class Customer_UserDetails extends javax.swing.JFrame {
    
        private Connection con;
        private PreparedStatement st;
        private String username;
        public static String currentUser;
        private int customerID;

    public Customer_UserDetails() {
        this.customerID = Login.customerID;
        //fetchUserDetails();
        
        if (acc_username == null) {
    System.out.println("acc_username is null. Check initialization.");
} else {
    acc_username.setText("Test"); // This should not cause an exception if initialized
}
        
        
        con = connectiontodatabase.getConnection();

        if (con == null) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        initComponents();
        //fetchUserData(userId);
        jPanel1.requestFocus();
        setLocationRelativeTo(null);
        fetchUserDetails();
        
        
        
        acc_mobilenumber.addFocusListener(new java.awt.event.FocusAdapter() {    
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (acc_mobilenumber.getText().equals("09")) {
                acc_mobilenumber.setText("");
                acc_mobilenumber.setForeground(Color.BLACK);
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (acc_mobilenumber.getText().isEmpty()) {
                acc_mobilenumber.setText("09");
                acc_mobilenumber.setForeground(Color.GRAY);  
            } } });
        
        acc_mobilenumber.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
        // Limit the input to 11 characters
        if (acc_mobilenumber.getText().length() >= 11) {
            evt.consume();
        }
    }
});
        //****************************************************************************************//
        
       
        //****************************************************************************************//
        
        edit.setEnabled(true);
        save.setEnabled(false);
        edit.addActionListener(e -> enableEditing());
        save.addActionListener(e -> saveUserData());
        
        
    }
        //****************************************************************************************//
    
        private void fetchUserDetails() {
            
        try {
            Connection conn = connectiontodatabase.getConnection();
                String query = "SELECT * FROM `signup` WHERE customer_id = '"+customerID+"'";
            PreparedStatement pstmt = conn.prepareStatement(query);
         
            //pstmt.setInt(1, customerID); 
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Populate user details in UI
                acc_username.setText(rs.getString("username"));
                acc_name.setText(rs.getString("name"));
                acc_email.setText(rs.getString("email"));
                acc_password.setText(rs.getString("password"));
                acc_mobilenumber.setText(rs.getString("mobile_number"));
                acc_address.setText(rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



        //****************************************************************************************//
        
        private void enableEditing() {
        acc_name.setEditable(true);
        acc_email.setEditable(true);
        acc_mobilenumber.setEditable(true);
        acc_password.setEditable(true);
        acc_address.setEditable(true);
        acc_username.setEnabled(false);
        
        edit.setEnabled(false); // Disable the Edit button
        save.setEnabled(true);  // Enable the Save button
    }
        
        //****************************************************************************************//
        
        private void saveUserData() {
        try {
        String updatedName = acc_name.getText().trim();
        String updatedEmail = acc_email.getText().trim();
        String updatedPassword = acc_password.getText().trim();
        String updatedAddress = acc_address.getText().trim();
        String updatedMobile = acc_mobilenumber.getText().trim();
        String updatedUsername = acc_username.getText().trim();
        
            if (updatedUsername.isEmpty() || updatedName.isEmpty() || updatedEmail.isEmpty() || 
            updatedPassword.isEmpty() || updatedAddress.isEmpty() || updatedMobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
            
            if (!updatedName.matches("^[a-zA-Z\\s'-]{1,255}$") 
        || !updatedMobile.matches("^09[0-9]{9}$")  
        || !updatedUsername.matches("^@[a-zA-Z0-9._]{1,255}$")  // Removed @ symbol for username
        || !updatedEmail.matches("^[a-zA-Z0-9._%+-]+@[a-z]+\\.[a-z]{1,255}$")
        || !updatedAddress.matches("^[a-zA-Z0-9\\s,.'-()]{1,255}$")
        || updatedPassword.length() < 6 || !updatedPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@,._])[A-Za-z\\d@,._]{6,255}$")) {
        JOptionPane.showMessageDialog(this, "Please input the correct format in the wrong field.", "ERROR", JOptionPane.WARNING_MESSAGE);
        return;
    }

       
        String updateQuery = "UPDATE signup SET name = ?, email = ?, mobile_number = ?, address = ?, username = ?, password = ? WHERE customer_id = ?";
        PreparedStatement pst = con.prepareStatement(updateQuery);
        pst.setString(1, updatedName);
        pst.setString(2, updatedEmail);
        pst.setString(3, updatedMobile);
        pst.setString(4, updatedAddress);
        pst.setString(5, updatedUsername);
        pst.setString(6, updatedPassword);
        pst.setInt(7, customerID); // Use the correct ID

        int rowsUpdated = pst.executeUpdate();
        
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "User information updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. User not found.");
        }

            // Disable editing after saving
            acc_username.setEditable(false);
            acc_name.setEditable(false);
            acc_email.setEditable(false);
            acc_password.setEditable(false);
            acc_mobilenumber.setEditable(false);
            acc_address.setEditable(false);
            edit.setEnabled(true); // Enable the Edit button again
            save.setEnabled(false); // Disable the Save button
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating data", "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        acc_name = new javax.swing.JTextField();
        acc_email = new javax.swing.JTextField();
        acc_mobilenumber = new javax.swing.JTextField();
        acc_username = new javax.swing.JTextField();
        acc_password = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        acc_address = new javax.swing.JTextArea();
        fn = new javax.swing.JLabel();
        num = new javax.swing.JLabel();
        userr = new javax.swing.JLabel();
        passs = new javax.swing.JLabel();
        eemail = new javax.swing.JLabel();
        home = new javax.swing.JLabel();
        title_signup = new javax.swing.JLabel();
        edit = new javax.swing.JButton();
        save = new javax.swing.JButton();
        back = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(1200, 675));
        setSize(new java.awt.Dimension(1200, 675));

        jPanel1.setMaximumSize(new java.awt.Dimension(1200, 675));
        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 675));
        jPanel1.setSize(new java.awt.Dimension(1200, 675));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setText("Name");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 148, -1, -1));

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel5.setText("Email");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 430, 60, -1));

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel2.setText("Mobile Number");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 218, -1, -1));

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel3.setText("Delivery Address & Landmark");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 495, 250, -1));

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel4.setText("Password");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 358, -1, -1));

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel6.setText("Username");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 288, -1, -1));

        acc_name.setEditable(false);
        acc_name.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        acc_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acc_nameActionPerformed(evt);
            }
        });
        acc_name.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_nameKeyReleased(evt);
            }
        });
        jPanel1.add(acc_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 178, 260, 28));

        acc_email.setEditable(false);
        acc_email.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        acc_email.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_emailKeyReleased(evt);
            }
        });
        jPanel1.add(acc_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 455, 260, 31));

        acc_mobilenumber.setEditable(false);
        acc_mobilenumber.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        acc_mobilenumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_mobilenumberKeyReleased(evt);
            }
        });
        jPanel1.add(acc_mobilenumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 248, 260, 28));

        acc_username.setEditable(false);
        acc_username.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        acc_username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_usernameKeyReleased(evt);
            }
        });
        jPanel1.add(acc_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 318, 260, 28));

        acc_password.setEditable(false);
        acc_password.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        acc_password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_passwordKeyReleased(evt);
            }
        });
        jPanel1.add(acc_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 385, 260, 31));

        acc_address.setEditable(false);
        acc_address.setColumns(20);
        acc_address.setRows(5);
        acc_address.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                acc_addressKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(acc_address);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(332, 528, 260, 106));

        fn.setBackground(new java.awt.Color(255, 255, 255));
        fn.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        fn.setForeground(new java.awt.Color(255, 51, 51));
        fn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fnKeyReleased(evt);
            }
        });
        jPanel1.add(fn, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 178, 108, 28));

        num.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        num.setForeground(new java.awt.Color(255, 51, 51));
        num.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        num.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                numKeyReleased(evt);
            }
        });
        jPanel1.add(num, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 248, 108, 28));

        userr.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        userr.setForeground(new java.awt.Color(255, 51, 51));
        userr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userrKeyReleased(evt);
            }
        });
        jPanel1.add(userr, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 318, 108, 28));

        passs.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        passs.setForeground(new java.awt.Color(255, 51, 51));
        passs.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        passs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passsKeyReleased(evt);
            }
        });
        jPanel1.add(passs, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 385, 400, 31));

        eemail.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        eemail.setForeground(new java.awt.Color(255, 51, 51));
        eemail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        eemail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                eemailKeyReleased(evt);
            }
        });
        jPanel1.add(eemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 455, 108, 31));

        home.setFont(new java.awt.Font("Helvetica Neue", 3, 12)); // NOI18N
        home.setForeground(new java.awt.Color(255, 51, 51));
        home.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        home.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                homeKeyReleased(evt);
            }
        });
        jPanel1.add(home, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 528, 108, 31));

        title_signup.setFont(new java.awt.Font("Kannada MN", 1, 50)); // NOI18N
        title_signup.setText("ACCOUNT INFORMATION");
        jPanel1.add(title_signup, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 50, -1, -1));

        edit.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        edit.setText("edit");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });
        jPanel1.add(edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 600, -1, 32));

        save.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        save.setText("save");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        jPanel1.add(save, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 600, -1, 32));

        back.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        back.setText("back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 600, -1, 32));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/normalbackground.png"))); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void acc_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acc_nameActionPerformed
         String PATTERN = "^[a-zA-Z\\s'-]{1,255}$";  // Allow letters and spaces
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(acc_name.getText());
    if (!match.matches()) {
        fn.setText("Invalid Input");
    } else {
        fn.setText(null);  // Clear the error message
    }
    
    }//GEN-LAST:event_acc_nameActionPerformed

    private void acc_nameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_nameKeyReleased

       String PATTERN = "^[a-zA-Z\\s'-]{1,255}$";  // Allow letters and spaces
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(acc_name.getText());
    if (!match.matches()) {
        fn.setText("Invalid Input");
    } else {
        fn.setText(null);  // Clear the error message
    }
    
    }//GEN-LAST:event_acc_nameKeyReleased

    private void acc_emailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_emailKeyReleased

        String PATTERN = "^[a-zA-Z0-9._%+-]+@[a-z]+\\.[a-z]{1,255}$"; // Fixed dot escaping
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(acc_email.getText());
    if(!match.matches()){
        eemail.setText("Invalid Input");
    }
    else{
        eemail.setText(null); 
    }
    }//GEN-LAST:event_acc_emailKeyReleased

    private void acc_mobilenumberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_mobilenumberKeyReleased

        String PATTERN = "^09[0-9]{9}$";  // Ensure phone number is 11 digits starting with 09
    Pattern pattern = Pattern.compile(PATTERN);
    Matcher match = pattern.matcher(acc_mobilenumber.getText());
    if(!match.matches()){
        num.setText("Invalid Input");
    }
    else{
        num.setText(null); 
    }
    }//GEN-LAST:event_acc_mobilenumberKeyReleased

    private void acc_usernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_usernameKeyReleased

        String PATTERN = "^@[a-zA-Z0-9._]{5,100}$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(acc_username.getText());
        if(!match.matches()){
            userr.setText("Invalid Input");
        }
        else{
            userr.setText(null);
        }
    }//GEN-LAST:event_acc_usernameKeyReleased

    private void acc_passwordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_passwordKeyReleased

         String PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@,._])[A-Za-z\\d@,._]{6,255}$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(acc_password.getText());
        if(!match.matches()){
            passs.setText("Password must include: upper, lower, number & special char");
        }
        else{
            passs.setText(null); 
        } 
    }//GEN-LAST:event_acc_passwordKeyReleased

    private void acc_addressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_acc_addressKeyReleased

        String PATTERN = "^[a-zA-Z0-9\\s,.'-()]{1,255}$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher match = pattern.matcher(acc_address.getText());
        if(!match.matches()){
            home.setText("Invalid Input");
        }
        else{
            home.setText(null); 
        } 
    }//GEN-LAST:event_acc_addressKeyReleased

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

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        
        dashboard_menu dash = new dashboard_menu();
        dash.setVisible(true);  
        this.dispose();
    }//GEN-LAST:event_backActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        try {
        // Ensure the database connection is open
        
        // Get the updated values from the text fields
        String updatedUsername = acc_username.getText().trim();
        String updatedName = acc_name.getText().trim();
        String updatedEmail = acc_email.getText().trim();
        String updatedPassword = acc_password.getText().trim();
        String updatedAddress = acc_address.getText().trim();
        String updatedMobile = acc_mobilenumber.getText().trim();
        
        // Validate required fields
        if (updatedUsername.isEmpty() || updatedEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Prepare the update query using a PreparedStatement to avoid SQL injection
        String updateQuery = "UPDATE signup SET name = ?, email = ?, mobile_number = ?, address = ?, username = ?, password = ? WHERE customer_id = ?";
        PreparedStatement pst = con.prepareStatement(updateQuery);
        pst.setString(1, updatedName);
        pst.setString(2, updatedEmail);
        pst.setString(3, updatedMobile);
        pst.setString(4, updatedAddress);
        pst.setString(5, updatedUsername);
        pst.setString(6, updatedPassword);
        pst.setInt(7, customerID); // Replace '1' with the actual user ID you are updating
        
        // Execute the update query
        int rowsAffected = pst.executeUpdate();
        
        // Check if the update was successful
        if (rowsAffected > 0) {
            //JOptionPane.showMessageDialog(this, "User information updated successfully!");

            // Disable editing after saving
            acc_username.setEditable(false);
            acc_name.setEditable(false);
            acc_email.setEditable(false);
            acc_password.setEditable(false);
            acc_mobilenumber.setEditable(false);
            acc_address.setEditable(false);

            // Enable the Edit button and disable the Save button
            edit.setEnabled(true);
            save.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "No changes were made.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        // Close the PreparedStatement
        pst.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "An error occurred while saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_saveActionPerformed

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
            java.util.logging.Logger.getLogger(Customer_UserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Customer_UserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Customer_UserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Customer_UserDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                //int customerID = 1;
                new Customer_UserDetails().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextArea acc_address;
    public javax.swing.JTextField acc_email;
    public javax.swing.JTextField acc_mobilenumber;
    public javax.swing.JTextField acc_name;
    public javax.swing.JTextField acc_password;
    public javax.swing.JTextField acc_username;
    private javax.swing.JButton back;
    private javax.swing.JButton edit;
    public javax.swing.JLabel eemail;
    private javax.swing.JLabel fn;
    public javax.swing.JLabel home;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel num;
    public javax.swing.JLabel passs;
    private javax.swing.JButton save;
    private javax.swing.JLabel title_signup;
    public javax.swing.JLabel userr;
    // End of variables declaration//GEN-END:variables
}
