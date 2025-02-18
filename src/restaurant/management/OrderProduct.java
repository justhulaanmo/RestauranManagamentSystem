package restaurant.management;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.swing.table.DefaultTableModel;
import structure.connectiontodatabase;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class OrderProduct extends javax.swing.JFrame {

    private Connection con;
    private Statement st;
    private int adminId;
    
    public OrderProduct() {
         this.adminId = Login.adminId; 
        
        initComponents();
        
        connectToDatabase();
        loadProductTableData();
        
        jLabel1.requestFocus();
        setLocationRelativeTo(null);
        
        setupTableClickListener();
        setupQuantityListener();
        
        //addToCart();
        
        
        adminid.setText(String.valueOf(adminId));
        
        System.out.println("Logged-in Admin ID: " + this.adminId);
        
        //*******************************************************************************************************************//
        
        try {
        int selectedRow = jTable1.getSelectedRow(); // Get selected product row
        if (selectedRow >= 0) {
            // Retrieve selected product details
            String productName = jTable1.getValueAt(selectedRow, 1).toString();
            double productPrice = Double.parseDouble(jTable1.getValueAt(selectedRow, 2).toString());
            int quantityValue = Integer.parseInt(quantity.getText());
            double subtotalValue = productPrice * quantityValue;

            // SQL query to insert cart data
            String query = "INSERT INTO admin_cart (admin_id, p_name, p_qty, p_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, adminId);  // Use the logged-in admin ID
            ps.setString(2, productName);
            ps.setDouble(3, productPrice);
            ps.setInt(4, quantityValue);
            ps.setDouble(5, subtotalValue);

            ps.executeUpdate();  // Execute the insert operation

        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error adding to cart: " + e.getMessage());
    }

    }
    
        //*******************************************************************************************************************//
    
        private void connectToDatabase() {
        try {
            connectiontodatabase dbConnection = new connectiontodatabase(); // Assuming this is your DB handler class
            con = dbConnection.getConnection(); // Replace with your method to get the connection
            st = con.createStatement(); // Create a Statement object
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        //*******************************************************************************************************************//
    
        private void loadProductTableData() {
        try {
        // SQL query to fetch required columns from the products table
        String query = "SELECT product_id, product_name, product_price, category FROM products";
        ResultSet rs = st.executeQuery(query); // Assuming `st` is a Statement object

        // Get the table model and clear existing data
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel(); 
        model.setRowCount(0);

        // Loop through the result set and add rows to the table model
        while (rs.next()) {
            Object[] row = {
                rs.getInt("product_id"),         // Column 1: Product ID
                rs.getString("product_name"),   // Column 2: Product Name
                rs.getDouble("product_price"),  // Column 3: Product Price
                rs.getString("category") // Column 4: Product Category
            };
            model.addRow(row);
        }
    }   catch (Exception e) {
        e.printStackTrace();
    }
        }
        
        private void loadCartData() {
    try {
        // Get the cart items for the current admin
        String query = "SELECT p_name, p_qty, p_price, subtotal FROM admin_cart WHERE admin_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();

        // Get the table model and clear existing data
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows

        // Loop through the result set and add rows to the table model
        while (rs.next()) {
            Object[] row = {
                rs.getString("p_name"),
                rs.getInt("p_qty"),
                rs.getDouble("p_price"),
                rs.getDouble("subtotal")
            };
            model.addRow(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading cart data: " + e.getMessage());
    }
}

    //*******************************************************************************************************************//
    
    private void setupTableClickListener() {
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = jTable1.getSelectedRow();
            
            if (selectedRow >= 0) {
                // Retrieve selected product details
                String productName = jTable1.getValueAt(selectedRow, 1).toString();
                double productPrice = Double.parseDouble(jTable1.getValueAt(selectedRow, 2).toString());
                
                name.setText(productName);
                // Set the price in the 'price' field
                price.setText(String.format("%.2f", productPrice));  // Set price in the field
                
                // Set the quantity field to a default value (e.g., 1)
                if (quantity.getText().isEmpty()) {
                    quantity.setText("1");
                }
                
                // Update the subtotal if quantity is already filled
                calculateSubtotal();  // Will trigger with current values
            }
        }
    });
}


    
    //*******************************************************************************************************************//
    
        private void calculateSubtotal() {
    // Get the product price and quantity
    String priceText = price.getText().trim();
    String quantityText = quantity.getText().trim();

    // Ensure that both fields are not empty
    if (priceText.isEmpty() || quantityText.isEmpty()) {
        subtotal.setText("0.00");
        return;  // Exit if either price or quantity is empty
    }

    try {
        // Parse the product price
        double productPrice = Double.parseDouble(priceText);

        // Parse the quantity
        int quantityValue = Integer.parseInt(quantityText);

        // Check if quantity is greater than 0
        if (quantityValue <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
            subtotal.setText("0.00");
            return;  // Exit if quantity is less than or equal to 0
        }

        // Calculate the subtotal
        double subtotalValue = productPrice * quantityValue;
        subtotal.setText(String.format("%.2f", subtotalValue));

    } catch (Exception e) {
        // Handle any exceptions by resetting the subtotal to 0.00
        subtotal.setText("0.00");
    }
}

    //*******************************************************************************************************************//

    private void addToCart(int selectedRow, String productName, int quantity, double price) {
    try {
        // Check if the quantity is valid
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
            return;  // Exit the method if invalid quantity
        }

        int productId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

        // Check if the product is already in the cart
        String checkQuery = "SELECT p_qty FROM admin_cart WHERE admin_id = ? AND product_id = ?";
        PreparedStatement checkPs = con.prepareStatement(checkQuery);
        checkPs.setInt(1, adminId);
        checkPs.setInt(2, productId);
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            // Product already in cart, update quantity
            int existingQuantity = rs.getInt("p_qty");
            int newQuantity = existingQuantity + quantity;

            String updateQuery = "UPDATE admin_cart SET p_qty = ? WHERE admin_id = ? AND product_id = ?";
            PreparedStatement updatePs = con.prepareStatement(updateQuery);
            updatePs.setInt(1, newQuantity);
            updatePs.setInt(2, adminId);
            updatePs.setInt(3, productId);
            updatePs.executeUpdate();
        } else {
            // Product not in cart, insert new row
            String insertQuery = "INSERT INTO admin_cart (admin_id, product_id, p_name, p_qty, p_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertPs = con.prepareStatement(insertQuery);
            insertPs.setInt(1, adminId);
            insertPs.setInt(2, productId);
            insertPs.setString(3, productName);
            insertPs.setInt(4, quantity);
            insertPs.setDouble(5, price);
            insertPs.executeUpdate();
        }

        // Optionally, refresh the cart or show a confirmation message
        JOptionPane.showMessageDialog(this, "Item added to cart successfully!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error adding to cart: " + e.getMessage());
    }
}


    //*******************************************************************************************************************//
        
       private void setupQuantityListener() {
 
    quantity.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            calculateSubtotal();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            calculateSubtotal();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            calculateSubtotal();
        }
    });
}
       //*******************************************************************************************************************//

     private int getOrderId() {
    try {
        // Query to fetch the latest order_id from admin_cart for the logged-in admin
        String query = "SELECT order_id FROM admin_cart WHERE admin_id = ? ORDER BY order_id DESC LIMIT 1";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, adminId); // Use the correct admin ID
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            // Return the most recent order_id if an active order exists
            return rs.getInt("order_id");
        } else {
            // If no active order exists, create a new one
            return createNewOrder();
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching order ID: " + e.getMessage());
    }
    return -1;  // Return -1 if there is an error fetching order ID
}


private int createNewOrder() {
    try {
        // Insert a new order into admin_orders after finalizing the cart
        String query = "INSERT INTO admin_orders (admin_id, total_amount, order_date) VALUES (?, 0, NOW())";
        PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, adminId);
        ps.executeUpdate();

        // Retrieve the generated order_id from the inserted row
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);  // Return the newly generated order_id
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error creating a new order: " + e.getMessage());
    }
    return -1;  // Return -1 if there is an error creating a new order
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        adminid = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        quantity = new javax.swing.JTextField();
        addtocartbtn = new javax.swing.JButton();
        cartbtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        subtotal = new javax.swing.JTextField();
        back = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        price = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1200, 675));

        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 675));
        jPanel1.setSize(new java.awt.Dimension(1200, 675));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Name", "Price", "Category"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(442, 127, 662, 498));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel2.setText("Order by Admin:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, -1, -1));

        adminid.setEditable(false);
        jPanel1.add(adminid, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 310, 36));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel3.setText("Product Name:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 200, -1, -1));

        name.setEditable(false);
        jPanel1.add(name, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, 310, 36));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel4.setText("Quantity:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, -1, -1));
        jPanel1.add(quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 380, 310, 36));

        addtocartbtn.setFont(new java.awt.Font("Optima", 1, 24)); // NOI18N
        addtocartbtn.setText("Add to Cart");
        addtocartbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addtocartbtnActionPerformed(evt);
            }
        });
        jPanel1.add(addtocartbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 525, -1, -1));

        cartbtn.setFont(new java.awt.Font("Optima", 1, 24)); // NOI18N
        cartbtn.setText("Cart");
        cartbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cartbtnActionPerformed(evt);
            }
        });
        jPanel1.add(cartbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 570, -1, -1));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel5.setText("Sub Total");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 430, -1, -1));

        subtotal.setEditable(false);
        jPanel1.add(subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 460, 310, 36));

        back.setBackground(new java.awt.Color(231, 230, 221));
        back.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        back.setText("back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, -1, -1));

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel6.setText("Price:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        price.setEditable(false);
        jPanel1.add(price, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, 310, 36));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/normalbackground.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

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

    private void addtocartbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addtocartbtnActionPerformed
    int selectedRow = jTable1.getSelectedRow();
    String productName = jTable1.getValueAt(selectedRow, 1).toString();
    double productPrice = Double.parseDouble(jTable1.getValueAt(selectedRow, 2).toString());
    int quantityValue = Integer.parseInt(quantity.getText());

    // Call the addToCart method with the retrieved values
    addToCart(selectedRow, productName, quantityValue, productPrice); // Use actual parameters

    }//GEN-LAST:event_addtocartbtnActionPerformed

    private void cartbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartbtnActionPerformed
            this.dispose();
            Admin_AddtoCart add = new Admin_AddtoCart();
            add.setVisible(true);
            
    }//GEN-LAST:event_cartbtnActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        this.dispose();
        AdminHome home = new AdminHome();
        home.setVisible(true);
    }//GEN-LAST:event_backActionPerformed

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
            java.util.logging.Logger.getLogger(OrderProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrderProduct().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addtocartbtn;
    private javax.swing.JTextField adminid;
    private javax.swing.JButton back;
    private javax.swing.JButton cartbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField name;
    private javax.swing.JTextField price;
    private javax.swing.JTextField quantity;
    private javax.swing.JTextField subtotal;
    // End of variables declaration//GEN-END:variables
}
