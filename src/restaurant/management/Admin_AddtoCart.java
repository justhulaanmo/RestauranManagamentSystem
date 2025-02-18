package restaurant.management;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import structure.connectiontodatabase;

import java.awt.*;
import java.awt.print.*;

import structure.CartItem; 
import java.util.ArrayList;

public class Admin_AddtoCart extends javax.swing.JFrame {   

    Statement st;
    Connection con;
    private int adminId;
   
    private ArrayList<CartItem> cartItems = new ArrayList<>();
    
public Admin_AddtoCart() {
        this.adminId = Login.adminId;
        
        connectToDatabase();
        initComponents();
        setLocationRelativeTo(null);
        loadCartData();
        updatePriceFields();

        cash.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calculateChange();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calculateChange();
            }
        });
    }

    private void connectToDatabase() {
        try {
            connectiontodatabase dbConnection = new connectiontodatabase();
            con = dbConnection.getConnection();
            st = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCartData() {
        cartItems.clear();
        try {
            String query = "SELECT p_name, p_qty, p_price, subtotal FROM admin_cart WHERE admin_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, adminId); 
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); 
            
            while (rs.next()) {
                String productName = rs.getString("p_name");
                double price = rs.getDouble("p_price");
                int quantity = rs.getInt("p_qty");
                double total = rs.getDouble("subtotal");

                cartItems.add(new CartItem(productName, price, quantity));
                model.addRow(new Object[]{productName, price, quantity, total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading cart data: " + e.getMessage());
        }
    }

    private double calculateCartTotal() {
        double total = 0.0;
        try {
            String query = "SELECT subtotal FROM admin_cart WHERE admin_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble("subtotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error calculating total: " + e.getMessage());
        }
        return total;
    }

    private void updatePriceFields() {
        try {
            double subtotall = calculateCartTotal();
            double taxx = subtotall * 0.12;
            double totalAmountt = subtotall + taxx;

            subtotal.setText(String.format("%.2f", subtotall));
            tax.setText(String.format("%.2f", taxx));
            total.setText(String.format("%.2f", totalAmountt));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating price fields: " + e.getMessage());
        }
    }

    private void calculateChange() {
        try {
            double subtotal = calculateCartTotal();
            double tax = subtotal * 0.12;
            double totalAmount = subtotal + tax;
            double cashReceived = Double.parseDouble(cash.getText());

            if (cashReceived >= totalAmount) {
                double changeAmount = cashReceived - totalAmount;
                change.setText(String.format("%.2f", changeAmount));
            } else {
                change.setText("0.00");
            }
        } catch (NumberFormatException e) {
            change.setText("0.00");
        }
    }

    
    private void insertOrderDetails(int orderId) {
    try {
        // Ensure we're inserting order details into admin_orderdetails with the correct order_id
        String query = "INSERT INTO admin_orderdetails (order_id, admin_id, product_name, product_quantity, product_price) "
                       + "SELECT ?, admin_id, p_name, p_qty, p_price FROM admin_cart WHERE admin_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, orderId);  // Use the newly generated order_id
        ps.setInt(2, adminId);  // Set the admin_id to associate with the order
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
       // JOptionPane.showMessageDialog(this, "Error inserting order details: " + e.getMessage());
    }
}



    private void insertOrderIntoAdminOrders(int orderId, double totalAmount) {
    try {
        String query = "INSERT INTO admin_orders (order_id, admin_id, total_amount, order_date) VALUES (?, ?, ?, NOW())";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, orderId); // Use the orderId generated earlier
        ps.setInt(2, adminId);  // Set the admin_id for the current session
        ps.setDouble(3, totalAmount);  // Use the total calculated earlier
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error inserting order into admin_orders: " + e.getMessage());
    }
}

    
    
    private int createNewOrder(int adminId, double totalAmount) {
        try {
            String query = "INSERT INTO admin_orders (admin_id, total_amount, order_date) VALUES (?, ?, NOW())";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, adminId);
            ps.setDouble(2, totalAmount);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating order: " + e.getMessage());
        }
        return -1;
    }


  private void updateProductQuantities() {
    try {
        con.setAutoCommit(false); // Start transaction

        // Query to fetch all cart items for the current admin
        String query = "SELECT product_id, p_qty FROM admin_cart WHERE admin_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, adminId);
        ResultSet rs = ps.executeQuery();

        // Loop through the cart items and update the quantities in the products table
        while (rs.next()) {
            int productId = rs.getInt("product_id");
            int quantityToAdd = rs.getInt("p_qty");  // Quantity to add to the stock

            // Fetch the current stock for the product
            String checkStockQuery = "SELECT quantity FROM products WHERE product_id = ?";
            PreparedStatement stockPs = con.prepareStatement(checkStockQuery);
            stockPs.setInt(1, productId);
            ResultSet stockRs = stockPs.executeQuery();

            if (stockRs.next()) {
                int currentStock = stockRs.getInt("quantity");

                // Increase the stock by the quantity purchased
                String updateQuery = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
                PreparedStatement updatePs = con.prepareStatement(updateQuery);
                updatePs.setInt(1, quantityToAdd);  // Add quantity to the existing stock
                updatePs.setInt(2, productId);
                updatePs.executeUpdate();
            } else {
                // If product is not found, show an error
                JOptionPane.showMessageDialog(this, "Product not found: " + productId);
                con.rollback();
                return;
            }
        }

        // Commit the transaction after all updates
        con.commit();
    } catch (SQLException e) {
        try {
            con.rollback();  // Rollback if any error occurs
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating product quantities: " + e.getMessage());
    } finally {
        try {
            con.setAutoCommit(true); // Reset to default auto-commit mode
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


   private void finalizePurchase() {
    try {
        double cashReceived = Double.parseDouble(cash.getText());
        double subtotal = calculateCartTotal();
        double tax = subtotal * 0.12;
        double totalAmount = subtotal + tax;

        if (cashReceived >= totalAmount) {
            // 1. Create a new order
            int orderId = createNewOrder(adminId, totalAmount);

            if (orderId != -1) {
              
                updateProductQuantities();
                insertOrderDetails(orderId);
                insertOrderIntoAdminOrders(orderId, totalAmount);

                generateReceipt(orderId);
                
                clearAdminCart();
                
                JOptionPane.showMessageDialog(this, "Order completed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create order details.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Insufficient cash.");
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid cash amount.");
    }
}




    private void clearAdminCart() {
    try {
        String query = "DELETE FROM admin_cart WHERE admin_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, adminId);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error clearing cart.");
    }
}


    private void generateReceipt(int orderId) {
    StringBuilder receipt = new StringBuilder();
    double subtotal = calculateCartTotal();
    double tax = subtotal * 0.12;
    double totalAmount = subtotal + tax;

    receipt.append("=== Receipt ===\n");
    receipt.append("Order ID: ").append(orderId).append("\n");
    receipt.append("Admin ID: ").append(adminId).append("\n");
    receipt.append("Items:\n");

    // Ensure the cartItems list is updated
    loadCartData(); // This will refresh the cartItems list

    for (CartItem item : cartItems) {
        receipt.append("- ").append(item.getProductName())
               .append(" x").append(item.getQuantity())
               .append(" @ ").append(String.format("%.2f", item.getPrice()))
               .append(" = ").append(String.format("%.2f", item.getTotal()))
               .append("\n");
    }

    receipt.append("\nSubtotal: ").append(String.format("%.2f", subtotal)).append("\n");
    receipt.append("Tax (12%): ").append(String.format("%.2f", tax)).append("\n");
    receipt.append("Total: ").append(String.format("%.2f", totalAmount)).append("\n");

    // Get cash input
    double cashReceived;
    try {
        cashReceived = Double.parseDouble(cash.getText());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid cash amount.");
        return;
    }

    double change = cashReceived - totalAmount;
    receipt.append("Cash: ").append(String.format("%.2f", cashReceived)).append("\n");
    receipt.append("Change: ").append(String.format("%.2f", change)).append("\n");

    // Display receipt in text area
    resibo.setText(receipt.toString());
}

    private void printReceipt() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE; // No more pages
                }

                // Get the graphics object for the page
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Set font and rendering properties for better output
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Print the content from JTextArea (resibo)
                resibo.printAll(g2d);

                return PAGE_EXISTS; // Page is ready for printing
            }
        });

        // Show print dialog
        boolean printAccepted = printerJob.printDialog();
        if (printAccepted) {
            try {
                printerJob.print(); // Send to printer
            } catch (PrinterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to print the receipt.");
            }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        purchase = new javax.swing.JButton();
        reciept = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resibo = new javax.swing.JTextArea();
        tax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cash = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        total = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        change = new javax.swing.JTextField();
        back = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        subtotal = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1200, 675));
        setMinimumSize(new java.awt.Dimension(1200, 675));
        setResizable(false);
        setSize(new java.awt.Dimension(1200, 675));

        jPanel1.setSize(new java.awt.Dimension(1200, 675));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Price", "Quantity", "Total"
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

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, 662, 498));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        purchase.setText("purchase");
        purchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseActionPerformed(evt);
            }
        });

        reciept.setText("reciept");
        reciept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recieptActionPerformed(evt);
            }
        });

        remove.setText("remove");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        resibo.setEditable(false);
        resibo.setColumns(20);
        resibo.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        resibo.setRows(5);
        jScrollPane2.setViewportView(resibo);

        tax.setEditable(false);
        tax.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        jLabel2.setText("VAT Tax:");

        jLabel6.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        jLabel6.setText("Cash $: ");

        cash.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        cash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel4.setText("Total:");

        total.setEditable(false);
        total.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        jLabel7.setText("Change $: ");

        change.setEditable(false);
        change.setFont(new java.awt.Font("Trebuchet MS", 0, 18)); // NOI18N
        change.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(122, 122, 122)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cash, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tax, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addGap(78, 78, 78)
                            .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addGap(31, 31, 31)
                        .addComponent(change, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(change, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(purchase)
                .addGap(18, 18, 18)
                .addComponent(reciept)
                .addGap(18, 18, 18)
                .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reciept)
                    .addComponent(remove)
                    .addComponent(purchase))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 330, 540));

        back.setBackground(new java.awt.Color(231, 230, 221));
        back.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        back.setText("back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel3.setText("Sub Total:");

        subtotal.setEditable(false);
        subtotal.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(364, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 70, 660, 40));

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

    private void purchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseActionPerformed
             finalizePurchase();
   
    }//GEN-LAST:event_purchaseActionPerformed

    private void recieptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recieptActionPerformed
        
       printReceipt();
        
    }//GEN-LAST:event_recieptActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
                                                   
    try {
        int selectedRow = jTable1.getSelectedRow(); // Get the selected row
        if (selectedRow >= 0) {
            String productName = jTable1.getValueAt(selectedRow, 0).toString(); // Column 0: Product Name

            // Remove item from the cart in the database
            String query = "DELETE FROM admin_cart WHERE admin_id = ? AND p_name = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, adminId);
            ps.setString(2, productName);
            ps.executeUpdate();

            // Refresh table data
            loadCartData();
            updatePriceFields();
            JOptionPane.showMessageDialog(this, "Item removed from cart.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }

    }//GEN-LAST:event_removeActionPerformed

    private void cashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashActionPerformed

    private void changeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_changeActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        
            this.dispose();  
            OrderProduct lg = new OrderProduct();
            lg.setVisible(true);  
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
            java.util.logging.Logger.getLogger(Admin_AddtoCart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin_AddtoCart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin_AddtoCart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin_AddtoCart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin_AddtoCart().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton back;
    private javax.swing.JTextField cash;
    private javax.swing.JTextField change;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton purchase;
    private javax.swing.JButton reciept;
    private javax.swing.JButton remove;
    private javax.swing.JTextArea resibo;
    private javax.swing.JTextField subtotal;
    private javax.swing.JTextField tax;
    private javax.swing.JTextField total;
    // End of variables declaration//GEN-END:variables

}