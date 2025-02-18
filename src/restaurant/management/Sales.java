package restaurant.management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import structure.connectiontodatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.Calendar;
import javax.swing.JButton;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Sales extends javax.swing.JFrame {
    
    Connection con;
    private java.sql.Date startDate;
    private java.sql.Date endDate;

    public Sales() {
        initComponents();
        
        con = connectiontodatabase.getConnection(); 
    if (con == null) {
        JOptionPane.showMessageDialog(this, "Database connection failed. Please check your connection.");
        return;
    }
    
    populateDateComboBox();
    
            applyfilter = new JButton("Apply Filter");
    applyfilter.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedFilter = (String) jComboBox1.getSelectedItem();
            System.out.println("Apply Filter Button Clicked");
            System.out.println("Selected Filter: " + selectedFilter);
            applyDateFilter(selectedFilter);  // Apply filter when the button is clicked
            loadBestsellingProducts(startDate, endDate);
        }
    });

    calculateTotalProductsSold();
    calculateTotalIncome();
    calculateOverallSummary();
    
    
    // Add mouse listener to jTable1
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jTable1MouseClicked(evt); // Call the jTable1MouseClicked method
        }
    });
}
    
    private void applyDateFilter(String selectedFilter) {
    java.sql.Date startDate;
    java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());  // Set current date as end date

    // Get the current date (without time)
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());

    if ("All Day".equals(selectedFilter)) {
        System.out.println("Applying All Day Filter");
        // Set startDate to today's date, at the start of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startDate = new java.sql.Date(calendar.getTimeInMillis());
    } else {
        System.out.println("Applying Date Filter for: " + selectedFilter);
        // Parse selected date (assuming it's in the format yyyy-mm-dd)
        startDate = java.sql.Date.valueOf(selectedFilter);  // Convert string to Date
    }

    // Debugging: Check start and end dates
    System.out.println("Start Date: " + startDate);
    System.out.println("End Date: " + endDate);

    // Call method to load data based on the selected date range
    loadBestsellingProducts(startDate, endDate);
    loadSalesData(startDate, endDate);
}



    private void populateDateComboBox() {
    try {
        // Query to get distinct order dates from the customer_orders table
        String query = "SELECT DISTINCT order_date FROM customer_orders";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        // Clear existing items in the combo box
        jComboBox1.removeAllItems();

        // Add "All Day" option for convenience
        jComboBox1.addItem("All Day");

        // Add dates from the database
        while (rs.next()) {
            java.sql.Date orderDate = rs.getDate("order_date");
            // Add the date to combo box (formatted as YYYY-MM-DD)
            jComboBox1.addItem(orderDate.toString());
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading available dates: " + e.getMessage());
    }
}

 private void loadSalesData(java.sql.Date startDate, java.sql.Date endDate) {
    try {
        // SQL query with date filter and correct column selection
        String query = "SELECT od.product_name, p.product_price, p.selling_price, " +
                       "(od.product_quantity * p.selling_price) AS subtotal, " +
                       "SUM(od.product_quantity) AS quantity_sold, " +
                       "SUM(od.product_quantity * p.selling_price) AS gross_income " +
                       "FROM customer_orderdetails od " +
                       "JOIN customer_orders co ON od.order_id = co.order_id " +
                       "JOIN products p ON od.product_id = p.product_id " +
                       "WHERE DATE(co.order_date) BETWEEN ? AND ? " + // Date range filter
                       "GROUP BY od.product_name, p.product_price, p.selling_price"; // Group by product details

        // Prepare the statement with the query
        PreparedStatement ps = con.prepareStatement(query);
        ps.setDate(1, startDate);  // Set start date
        ps.setDate(2, endDate);    // Set end date

        ResultSet rs = ps.executeQuery();

        // Reset the table model to refresh data
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows
        
        double totalQuantitySold = 0;
        double totalIncome = 0;
        double totalCostOfGoodsSold = 0;
        double totalNetIncome = 0;

        while (rs.next()) {
            String productName = rs.getString("product_name");
            double productPrice = rs.getDouble("product_price");  // Cost Price
            double sellingPrice = rs.getDouble("selling_price");  // Selling Price
            double subtotal = rs.getDouble("subtotal");  // Subtotal (selling price * quantity sold)
            double grossIncome = rs.getDouble("gross_income");  // Total sales revenue
            int quantitySold = rs.getInt("quantity_sold");

            // Calculate the net income (gross income - cost of goods sold)
            double netIncomeForProduct = grossIncome - (quantitySold * productPrice);

            // Add row to the table
            model.addRow(new Object[]{productName, productPrice, sellingPrice, subtotal, grossIncome, netIncomeForProduct});
            
            totalQuantitySold += quantitySold;
            totalIncome += grossIncome;
            totalCostOfGoodsSold += (quantitySold * productPrice);
            totalNetIncome += netIncomeForProduct;
        }
        
        // Update the UI elements with calculated values
        totalquantitysold.setText(String.valueOf((int) totalQuantitySold)); // Cast to int to avoid decimals
        totalincome.setText(String.format("%.2f", totalIncome));  // Format total income to two decimals
        netincome.setText(String.format("%.2f", totalNetIncome));  // Format net income to two decimals
        grossincome.setText(String.format("%.2f", totalIncome));  // Format gross income to two decimals

        // Use TableRowSorter to apply date-based filtering on rows (if needed)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage());
    }
}





    // Show the total quantity sold for the selected product
    private void displayTotalProductsSold() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            String productName = (String) jTable1.getValueAt(selectedRow, 0);

            String query = "SELECT quantity_sold FROM sales WHERE product_name = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, productName);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int totalSold = rs.getInt("quantity_sold");
                    totalquantitysold.setText(String.valueOf(totalSold)); // Display in total_productsold label
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Calculate the total quantity of products sold
    private void calculateTotalProductsSold() {
        try {
            String query = "SELECT SUM(quantity_sold) AS total_products FROM sales";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int totalQuantity = rs.getInt("total_products");
                totalquantitysold.setText(String.valueOf((int) totalQuantity)); // Update label
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculate the total income
    private void calculateTotalIncome() {
        try {
            String query = "SELECT SUM(gross_income) AS total_income FROM sales";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double totalIncome = rs.getDouble("total_income");
                grossincome.setText(String.format("%.2f", totalIncome)); // Format the income
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void calculateNetIncome(double grossIncome, int totalQuantitySold) {
    try {
        // Retrieve product cost from the database (assuming productPrice is the cost of the product)
        String query = "SELECT product_price FROM products WHERE product_name = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, "product_name"); // Pass the selected product's name
        ResultSet rs = ps.executeQuery();

        double productPrice = 0;
        if (rs.next()) {
            productPrice = rs.getDouble("product_price");
        }

        // Calculate the net income (gross income - cost of goods sold)
        double costOfGoodsSold = totalQuantitySold * productPrice;  // COGS based on quantity and product cost
        double netIncome = grossIncome - costOfGoodsSold;  // Calculate net income

        // Update the net income label with the result
        netincome.setText(String.format("%.2f", netIncome));  // Display the calculated net income

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error calculating net income: " + e.getMessage());
    }
}


    // Calculate the overall summary, i.e., net income
    private void calculateOverallSummary() {
    try {
        String totalIncomeText = totalincome.getText();
        String totalProductsText = totalquantitysold.getText();

        if (totalIncomeText != null && !totalIncomeText.isEmpty() && totalProductsText != null && !totalProductsText.isEmpty()) {
            double totalIncome = Double.parseDouble(totalIncomeText);
            int totalProducts = Integer.parseInt(totalProductsText);

            // Assuming the cost of goods sold is calculated based on some business logic (use actual cost logic here)
            double costOfGoodsSold = totalProducts * 10; // Placeholder for actual cost of goods
            double netIncome = totalIncome - costOfGoodsSold;

            netincome.setText(String.format("%.2f", netIncome)); // Display net income
        } else {
            netincome.setText("0.00"); // Handle case where data is missing
        }
    } catch (NumberFormatException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Invalid number format for total income or total products sold.");
    }
}

    // Display the most sold products in the best-selling text area
    private void loadBestsellingProducts(java.sql.Date startDate, java.sql.Date endDate) {
    String query = "SELECT od.product_id, SUM(od.product_quantity) AS total_quantity_sold " +
                   "FROM customer_orderdetails od " +
                   "JOIN customer_orders co ON od.order_id = co.order_id " +
                   "WHERE co.order_date BETWEEN ? AND ? " +
                   "GROUP BY od.product_id " +
                   "ORDER BY total_quantity_sold DESC";

    try (PreparedStatement ps = con.prepareStatement(query)) {
        ps.setDate(1, startDate);
        ps.setDate(2, endDate);
        
        ResultSet rs = ps.executeQuery();
        StringBuilder bestsellingText = new StringBuilder("Bestselling Products:\n");

        // Check if there are results
        int rank = 1;
        while (rs.next()) {
            int productId = rs.getInt("product_id");
            int totalQuantitySold = rs.getInt("total_quantity_sold");

            // Optionally: Retrieve product name or other details
            String productName = getProductNameById(productId); // Implement this method to get the product name

            bestsellingText.append("Rank " + rank + ": " + productName + " - " + totalQuantitySold + " sold\n");
            rank++;
        }

        // Display the bestselling products in JTextArea
        bestselling.setText(bestsellingText.toString());
        
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching bestselling products: " + e.getMessage());
    }
}

    private String getProductNameById(int productId) {
    String productName = "Unknown Product";  // Default name if not found
    String query = "SELECT product_name FROM products WHERE product_id = ?";  // Assuming you have a `products` table
    
    try (PreparedStatement ps = con.prepareStatement(query)) {
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            productName = rs.getString("product_name");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return productName;
}




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        totalquantitysold = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        totalincome = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        bestselling = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        netincome = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        back = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        applyfilter = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        daterange = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        grossincome = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1200, 675));
        setMinimumSize(new java.awt.Dimension(1200, 675));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Product Price", "Selling Price", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, 840, 400));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel3.setText("Total Products Sold:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 70, -1, -1));

        jPanel3.setBackground(new java.awt.Color(51, 51, 255));

        totalquantitysold.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        totalquantitysold.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalquantitysold, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalquantitysold, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 110, 90));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel2.setText("Total income:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 70, -1, -1));

        jPanel4.setBackground(new java.awt.Color(255, 0, 51));

        totalincome.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        totalincome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalincome, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalincome, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 40, 110, 90));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel4.setText("Best Selling Products:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

        bestselling.setEditable(false);
        bestselling.setColumns(20);
        bestselling.setFont(new java.awt.Font("Kannada Sangam MN", 0, 14)); // NOI18N
        bestselling.setRows(5);
        jScrollPane2.setViewportView(bestselling);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, -1, 270));

        jPanel5.setBackground(new java.awt.Color(0, 204, 204));

        netincome.setFont(new java.awt.Font("Helvetica Neue", 1, 30)); // NOI18N
        netincome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(netincome, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(netincome, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 120, 110));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel5.setText("Gross Income:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 450, -1, -1));

        back.setBackground(new java.awt.Color(231, 230, 221));
        back.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        back.setText("back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        getContentPane().add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Day" }));

        jLabel8.setText("Filter Date by:");

        applyfilter.setText("Apply Filter");
        applyfilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyfilterActionPerformed(evt);
            }
        });

        reset.setText("Reset");
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(applyfilter)
                        .addGap(18, 18, 18)
                        .addComponent(reset))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyfilter)
                    .addComponent(reset))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 230, 100));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel1.setText("Date Range:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, -1, -1));

        daterange.setEditable(false);
        getContentPane().add(daterange, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 180, 230, -1));

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel7.setText("Net Income:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jPanel2.setBackground(new java.awt.Color(153, 255, 102));

        grossincome.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        grossincome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(grossincome, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(grossincome, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 480, 120, 110));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/normalbackground.png"))); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed

        this.dispose();
        AdminHome lg = new AdminHome();
        lg.setVisible(true);
    }//GEN-LAST:event_backActionPerformed

    private void resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetActionPerformed
     jComboBox1.setSelectedItem("All Day");  // Reset combo box to "All Day"
    totalquantitysold.setText("");
    totalincome.setText("");
    netincome.setText("");
    grossincome.setText("");

    // Apply the "All Day" filter based on the combo box selection
    applyDateFilter((String) jComboBox1.getSelectedItem());
    
    }//GEN-LAST:event_resetActionPerformed

    private void applyfilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyfilterActionPerformed
   String selectedFilter = (String) jComboBox1.getSelectedItem();
    
    if (selectedFilter != null) {
        // Apply the filter when the button is clicked
        applyDateFilter(selectedFilter);

        // Update the daterange label with the selected date
        daterange.setText("Selected Date: " + selectedFilter);  // Display the selected date
    }
  
    }//GEN-LAST:event_applyfilterActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
    
    if (selectedRow != -1) {
        // Get the product details for the selected row
        String productName = (String) jTable1.getValueAt(selectedRow, 0); // Product Name
        double productPrice = (Double) jTable1.getValueAt(selectedRow, 1); // Product Cost Price
        double sellingPrice = (Double) jTable1.getValueAt(selectedRow, 2); // Selling Price
        double subtotal = (Double) jTable1.getValueAt(selectedRow, 3); // Subtotal (Selling Price * Quantity Sold)

        // Check if quantity sold is correctly calculated
        // Assuming the subtotal is calculated as (sellingPrice * quantitySold)
        int quantitySold = (int) (subtotal / sellingPrice); // Calculate quantity sold based on the subtotal

        // Calculate the Net Income (Gross Income - Quantity Sold * Product Price)
        double grossIncome = sellingPrice * quantitySold; // Gross income is selling price * quantity sold
        double netIncome = grossIncome - (quantitySold * productPrice); // Net income is gross income minus cost of goods sold

        // Update the product-specific fields
        totalquantitysold.setText(String.valueOf(quantitySold)); // Set quantity sold
        totalincome.setText(String.format("%.2f", grossIncome)); // Set total income (Gross Income)

        // Set the calculated Net Income in the label
        netincome.setText(String.format("%.2f", netIncome)); // Display net income
        
        String selectedFilter = (String) jComboBox1.getSelectedItem();
        if (selectedFilter != null) {
            daterange.setText("Selected Date: " + selectedFilter); // Show selected date in daterange label
        }
    }
    }//GEN-LAST:event_jTable1MouseClicked

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
            java.util.logging.Logger.getLogger(Sales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Sales().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyfilter;
    private javax.swing.JButton back;
    private javax.swing.JTextArea bestselling;
    private javax.swing.JTextField daterange;
    private javax.swing.JLabel grossincome;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel netincome;
    private javax.swing.JButton reset;
    private javax.swing.JLabel totalincome;
    private javax.swing.JLabel totalquantitysold;
    // End of variables declaration//GEN-END:variables
}
