package restaurant.management;

import structure.connectiontodatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import structure.OrderItem_Customer;



public class dashboard_menu extends javax.swing.JFrame {
    
    private javax.swing.JButton[] addToCartButtons;
    public static Connection con;
    private PreparedStatement pst;
    
    private JLabel[] lblNames;
    private JLabel[] lblPrices;
    private int customerID;
    private static String username;
    private javax.swing.JSpinner[] qtySpinners;
    
    DefaultTableModel model;
    List<OrderItem_Customer> cartItems;
    
//*********************************************************************************************************************************************//
   
public dashboard_menu() {
        initComponents();
        this.customerID = Login.customerID;
        this.username = Login.getCurrentUser();
        jLabel2.requestFocus();
        setLocationRelativeTo(null);
        hideProductIdColumn();
         loadCustomerCart();
        jLabel1.setText("Welcome, " + (username));
        
            lblNames = new JLabel[]{comboA, comboB, comboC, comboD,comboE,comboF,
                                    Dessert8,Dessert9,Dessert10,Dessert11,Dessert12,Dessert13}; // Your actual JLabel names in your design

            lblPrices = new JLabel[]{priceA, priceB, priceC, priceD,priceE,priceF,
                                    price8,price9,price10,price11,price12,price13}; // Your price JLabel names

            addToCartButtons = new javax.swing.JButton[]{b1, b2, b3, b4, b5, b6,b7, b8, b9, b10, b11, b12};
            qtySpinners = new javax.swing.JSpinner[]{s1, s2, s3, s4, s5, s6,s7, s8, s9, s10, s11,s12};
            
            int[] productId = {101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112};
            
           

            addActionListeners(productId);
            fetchAndDisplayProducts();
            clearCart();
    }


private void hideProductIdColumn() {
        cart_table.getColumnModel().getColumn(4).setMaxWidth(0); // Hide the productId column (index 4)
        cart_table.getColumnModel().getColumn(4).setMinWidth(0); 
        cart_table.getColumnModel().getColumn(4).setPreferredWidth(0);
        
        // Optionally hide the column header as well
        cart_table.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(0);
        cart_table.getTableHeader().getColumnModel().getColumn(4).setMinWidth(0);
    }

//*********************************************************************************************************************************************//

        private void fetchAndDisplayProducts() {
    String query = "SELECT product_id, product_name, selling_price, quantity FROM products LIMIT 12";
    try (
        Connection con = connectiontodatabase.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()
    ) {
        int productCount = 0;  // Counter for the product labels
        while (rs.next() && productCount < lblNames.length) {
            String name = rs.getString("product_name");
            double price = rs.getDouble("selling_price");  // Ensure double type for price
            int quantity = rs.getInt("quantity");  // Available stock for the product
            int productId = rs.getInt("product_id");

            if (lblNames[productCount] != null) {
                lblNames[productCount].setText(name);
            }
            if (lblPrices[productCount] != null) {
                lblPrices[productCount].setText(String.format("%.2f", price));
            }
            if (qtySpinners[productCount] != null) {
                javax.swing.SpinnerNumberModel model = new javax.swing.SpinnerNumberModel(0, 0, quantity, 1);
                qtySpinners[productCount].setModel(model);  // Minimum 0, maximum = product quantity
            }
            if (quantity == 0 && qtySpinners[productCount] != null) {
                qtySpinners[productCount].setEnabled(false); // Disable spinner if out of stock
                addToCartButtons[productCount].setEnabled(false); // Disable Add-to-Cart button
            }

            productCount++; // Move to the next product
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

       
//*********************************************************************************************************************************************//
        
        private void addActionListeners(int[] productId) {
    for (int i = 0; i < addToCartButtons.length; i++) {
        int index = i;  // Index for lambda expression
        int productIds = productId[index];

        // Add the action listener to each button
        addToCartButtons[i].addActionListener(e -> {
            // Pass the product name, price, and spinner value when clicked
            addToCartButtonActionPerformed(lblNames[index].getText(), lblPrices[index], qtySpinners[index]);
        });
    }
}

//*********************************************************************************************************************************************//        
        
        private void clearCart() {
        DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
        model.setRowCount(0); // Clear all rows in the cart table
        updateSubtotal(); // Clear subtotal, tax, and total fields
}

//*********************************************************************************************************************************************//
        
        // Call this method when the Add to Cart button is clicked for any product
private void addToCartButtonActionPerformed(String productName, JLabel lblPrice, javax.swing.JSpinner qtySpinner) {
    double productPrice = Double.parseDouble(lblPrice.getText().replace("", ""));
    int selectedQuantity = (int) qtySpinner.getValue();

    if (selectedQuantity > 0) {
        try (Connection con = connectiontodatabase.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT quantity FROM products WHERE product_name = ?")) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int availableQuantity = rs.getInt("quantity");

                    // Calculate current quantity in the cart for this product
                    int currentCartQuantity = getCurrentCartQuantity(productName);

                    // Check if adding this quantity exceeds available stock
                    if (currentCartQuantity + selectedQuantity > availableQuantity) {
                        JOptionPane.showMessageDialog(this, "You have reached the maximum available quantity for this product.", "Error", JOptionPane.WARNING_MESSAGE);
                        qtySpinner.setValue(availableQuantity - currentCartQuantity); // Reset spinner to remaining available quantity
                        return; // Prevent adding to the cart
                    }

                    // Check if current cart quantity equals available quantity
                    if (currentCartQuantity == availableQuantity) {
                        JOptionPane.showMessageDialog(this, "You have already added the maximum quantity of this product to your cart.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return; // Prevent adding more to the cart
                    }

                    // If all checks pass, add to cart
                    addToCart(productName, productPrice, selectedQuantity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a valid quantity!", "Error", JOptionPane.WARNING_MESSAGE);
    }
}


private int getCurrentCartQuantity(String productName) {
    int quantity = 0;

    // Iterate through the cart table to find the current quantity of the product in the cart
    DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
    for (int i = 0; i < model.getRowCount(); i++) {
        String existingProductName = model.getValueAt(i, 0).toString();  // Item name in column 0
        if (existingProductName.equals(productName)) {
            quantity = (int) model.getValueAt(i, 2);  // Qty in column 2
            break;
        }
    }
    return quantity;
}



// Modified the insertIntoCustomerAddToCart method
private void insertIntoCustomerAddToCart(int customerID, int productId, String productName, int quantity, double productPrice) {
    String insertQuery = "INSERT INTO customer_cart (customer_id, product_id, product_name, product_quantity, product_price) VALUES (?, ?, ?, ?, ?)";

    try (Connection con = connectiontodatabase.getConnection()) {
        PreparedStatement pst = con.prepareStatement(insertQuery);
        pst.setInt(1, customerID);
        pst.setInt(2, productId);
        pst.setString(3, productName);
        pst.setInt(4, quantity);
        pst.setDouble(5, productPrice);
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Item added to cart successfully.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: Unable to add item to cart.");
    }
}

// The addToCart method stays the same
private void addToCart(String productName, double productPrice, int quantity) {
    DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
    boolean productExists = false;

    // Check if product already exists in the cart table, and update if necessary
    for (int i = 0; i < model.getRowCount(); i++) {
        String existingProductName = model.getValueAt(i, 0).toString();  // Item name in column 0
        if (existingProductName.equals(productName)) {
            // Update quantity and subtotal
            int existingQuantity = (int) model.getValueAt(i, 2);  // Qty in column 2
            int newQuantity = existingQuantity + quantity;
            double newSubtotal = newQuantity * productPrice;

            model.setValueAt(newQuantity, i, 2);  // Update quantity
            model.setValueAt(newSubtotal, i, 3); // Update subtotal
            productExists = true;
            break;
        }
    }

    // If the product doesn't exist, add a new row to the cart table
    if (!productExists) {
        double totalPrice = productPrice * quantity;
        model.addRow(new Object[]{productName, productPrice, quantity, totalPrice});  // Add new row without productId
    }

    // Update subtotal, tax, and total fields
    updateSubtotal();
}


// The updateCustomerAddToCart method stays the same
private void updateCustomerAddToCart(int customerID, int productId, int newQuantity) {
    String query = "UPDATE customer_cart SET product_quantity = ? WHERE customer_id = ? AND product_id = ?";
    try (Connection con = connectiontodatabase.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {
        pst.setInt(1, newQuantity);
        pst.setInt(2, customerID);
        pst.setInt(3, productId);
        pst.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


       private void loadCustomerCart() {
    String query = "SELECT product_name, product_quantity, product_price, (product_quantity * product_price) AS subtotal FROM customer_cart WHERE customer_id = ?";
    
    try (Connection con = connectiontodatabase.getConnection()) {
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, this.customerID);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
        model.setRowCount(0);  // Clear existing data

        while (rs.next()) {
            String productName = rs.getString("product_name");
            int quantity = rs.getInt("product_quantity");
            double price = rs.getDouble("product_price");
            double subtotal = rs.getDouble("subtotal");

            // Add data to the table
            model.addRow(new Object[]{productName, price, quantity, subtotal});
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


//*********************************************************************************************************************************************//

        private void updateSubtotal() {
    DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
    double subtotalAmount = 0.0;

    for (int i = 0; i < model.getRowCount(); i++) {
        subtotalAmount += (double) model.getValueAt(i, 3);
    }

    double taxAmount = subtotalAmount * 0.12;
    double totalAmount = subtotalAmount + taxAmount;

    subtotal.setText(String.format("%.2f", subtotalAmount));
    tax.setText(String.format("%.2f", taxAmount));
    total.setText(String.format("%.2f", totalAmount));
}

//*********************************************************************************************************************************************//
        
       private void insertCustomerOrderDetails(int customerID, int orderId, List<OrderItem_Customer> orderItems) {
    String queryOrderDetails = "INSERT INTO customer_orderdetails (order_id, customer_id, product_id, product_name, product_quantity, product_price, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String queryGetProductId = "SELECT product_id FROM products WHERE product_name = ?";
    String queryUpdateTotalAmount = "UPDATE customer_orders SET total_amount = ? WHERE order_id = ?";

    try (Connection con = connectiontodatabase.getConnection();
         PreparedStatement stmtOrderDetails = con.prepareStatement(queryOrderDetails);
         PreparedStatement stmtGetProductId = con.prepareStatement(queryGetProductId);
         PreparedStatement stmtUpdateTotalAmount = con.prepareStatement(queryUpdateTotalAmount)) {

        double totalAmount = 0.0;

        // Loop through each item in the orderItems list
        for (OrderItem_Customer item : orderItems) {
            // Get product_id from product_name
            stmtGetProductId.setString(1, item.getProductName()); // Set product name
            ResultSet rs = stmtGetProductId.executeQuery();

            int productId = 0;
            if (rs.next()) {
                productId = rs.getInt("product_id"); // Get product_id from the result
            } else {
                // Handle case where product_name doesn't exist in the products table
                System.err.println("Product not found: " + item.getProductName());
                continue; // Skip this item if product is not found
            }

            // Insert order details
            stmtOrderDetails.setInt(1, orderId);      // Set order ID
            stmtOrderDetails.setInt(2, customerID);   // Set customer ID
            stmtOrderDetails.setInt(3, productId);    // Set product ID
            stmtOrderDetails.setString(4, item.getProductName()); // Set product name
            stmtOrderDetails.setInt(5, item.getQuantity());  // Set quantity
            stmtOrderDetails.setDouble(6, item.getPrice()); // Set product price
            stmtOrderDetails.setDouble(7, item.getSubtotal());  // Set subtotal

            stmtOrderDetails.addBatch();  // Add to batch for batch processing

            // Update total amount
            totalAmount += item.getSubtotal(); // Accumulate subtotal for total amount
        }

        // Execute batch insert for order details
        stmtOrderDetails.executeBatch();

        // Update total_amount in customer_orders
        stmtUpdateTotalAmount.setDouble(1, totalAmount);
        stmtUpdateTotalAmount.setInt(2, orderId);
        stmtUpdateTotalAmount.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}



//**********************************************************************************************************************????***********************//
        
        private int insertCustomerOrderSummary(int customerID, double totalAmount) {
    String query = "INSERT INTO customer_orders (customer_id, total_amount, order_date) VALUES (?, ?, NOW())";
    int orderId = -1;

    try (Connection con = connectiontodatabase.getConnection();
         PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, customerID);  // Use the logged-in customer ID
        stmt.setDouble(2, totalAmount); // Total order amount
        stmt.executeUpdate();

        // After inserting, get the last inserted order_id
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                orderId = rs.getInt(1); // Retrieve the last inserted ID
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return orderId; // Return the generated order ID (or -1 if something went wrong)
}

//*********************************************************************************************************************************************//

        private void completeOrder(int customerID, List<OrderItem_Customer> orderItems, double totalAmount) {
    // Step 1: Generate the order ID
    int orderId = insertCustomerOrderSummary(customerID, totalAmount);

    if (orderId != -1) {
        // Step 2: Insert each product in the order into the customer_orderdetails table
       updateProductQuantities(orderItems); 
       insertCustomerOrderDetails(customerID, orderId, orderItems);
        
        displayReceipt(orderId, customerID, orderItems);

        clearCustomerAddToCart(customerID);
        
        clearCart();  // Clear cart in UI
    } else {
        JOptionPane.showMessageDialog(this, "Failed to complete the order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
        
        
        private void clearCustomerAddToCart(int customerID) {
    String query = "DELETE FROM customer_cart WHERE customer_id = ?";

    try (Connection con = connectiontodatabase.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {

        pst.setInt(1, customerID);
        pst.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
        
        private void updateProductQuantities(List<OrderItem_Customer> orderItems) {
    String query = "UPDATE products SET quantity = quantity - ? WHERE product_name = ?";

    try (Connection con = connectiontodatabase.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {

        for (OrderItem_Customer item : orderItems) {
            pst.setInt(1, item.getQuantity());
            pst.setString(2, item.getProductName());
            pst.addBatch();
        }
        pst.executeBatch();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

//*********************************************************************************************************************************************//

        private void displayReceipt(int orderId, int customerID, List<OrderItem_Customer> orderItems) {
    // Initialize the subtotal amount to 0.0
    double subtotalAmount = 0.0;

    // Create the receipt string with the header
    String receipt = "----------- RECEIPT -----------\n";
    receipt += "Order ID: " + orderId + "\n";
    receipt += "Customer ID: " + customerID + "\n";
    receipt += "------------------------------\n";
    receipt += "Items:\n";

    // Loop through the order items to display item details and calculate the subtotal
    for (OrderItem_Customer item : orderItems) {
        double itemTotal = item.getPrice() * item.getQuantity(); // Calculate total for each item
        subtotalAmount += itemTotal;

        // Add item details to the receipt
        receipt += String.format("%-20s %5d x $%-6.2f = $%-6.2f\n", 
                                 item.getProductName(), 
                                 item.getQuantity(), 
                                 item.getPrice(), 
                                 itemTotal);
    }

    // Calculate tax and total
    double taxAmount = subtotalAmount * 0.12;  // Assuming 12% tax
    double totalAmount = subtotalAmount + taxAmount;

    // Add subtotal, tax, and total to the receipt
    receipt += "------------------------------\n";
    receipt += "Subtotal: " + String.format("%.2f", subtotalAmount) + "\n";
    receipt += "Tax (12%): " + String.format("%.2f", taxAmount) + "\n";
    receipt += "Total: " + String.format("%.2f", totalAmount) + "\n";
    receipt += "------------------------------";

   reciept.setText(receipt);
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
                reciept.printAll(g2d);

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

        
//*********************************************************************************************************************************************//
        
     private List<OrderItem_Customer> getOrderItems() {
    List<OrderItem_Customer> orderItems = new ArrayList<>();
    DefaultTableModel model = (DefaultTableModel) cart_table.getModel();

    // Loop through each row in the cart table to extract product details
    for (int i = 0; i < model.getRowCount(); i++) {
        
        // Get product name (assuming it's always a String) - Column 0 (Item)
        String productName = (String) model.getValueAt(i, 0);  // Get product name
        
        // Get quantity - Column 2 (Qty)
        String quantityStr = model.getValueAt(i, 2).toString();  // Get quantity as String (Qty)
        int quantity = Integer.parseInt(quantityStr);  // Convert to integer
        
        // Get product price - Column 1 (Price)
        Object productPriceObj = model.getValueAt(i, 1);  // Column 1 (Price)
        double productPrice = 0.0;

        // Check if productPriceObj is not null and is a valid number
        if (productPriceObj != null) {
            String priceString = productPriceObj.toString().trim(); // Trim any extra spaces
            try {
                // Print for debugging
                System.out.println("Price string to be parsed: '" + priceString + "'");
                // Remove non-numeric characters before parsing
                priceString = priceString.replaceAll("[^\\d.]", "");  // Remove non-numeric characters except the decimal
                productPrice = Double.parseDouble(priceString);  // Parse the numeric value
            } catch (NumberFormatException e) {
                // Error handling: in case the value cannot be parsed as double
                System.err.println("Error parsing product price: " + e.getMessage());
                productPrice = 0.0; // Default to 0 if there is a parsing error
            }
        } else {
            // If productPriceObj is null, set productPrice to 0.0
            System.err.println("Error: product price is null.");
            productPrice = 0.0;
        }

        // Debugging output
        System.out.println("Product Name: " + productName);
        System.out.println("Quantity: " + quantity);
        System.out.println("Product Price: " + productPrice);

        // Create an OrderItem_Customer and add to list
        OrderItem_Customer orderItem = new OrderItem_Customer(productName, quantity, productPrice);
        orderItems.add(orderItem);
    }
    return orderItems;
}


//*********************************************************************************************************************************************//

        private double getTotalAmount() {
            double totalAmount = 0;
            DefaultTableModel model = (DefaultTableModel) cart_table.getModel();

            // Loop through each row in the cart table to sum up the total
            for (int i = 0; i < model.getRowCount(); i++) {
                double subtotal = (double) model.getValueAt(i, 3);  // Get the total price for the item (price * quantity)
                totalAmount += subtotal;
            }
            return totalAmount;
        }

//*********************************************************************************************************************************************//

        private void removeItemFromCart(int selectedRow) {
    // Check if a row is selected
    if (selectedRow != -1) {
        DefaultTableModel model = (DefaultTableModel) cart_table.getModel();
        
        // Remove the selected row from the model
        model.removeRow(selectedRow);

        // Optionally, update the UI or give feedback
        JOptionPane.showMessageDialog(this, "Item removed from cart.");
    } else {
        JOptionPane.showMessageDialog(this, "Please select an item to remove.");
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
        reciept = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        cart_table = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        account = new javax.swing.JLabel();
        menutabbed = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        ComboA_image4 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        comboA = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        priceA = new javax.swing.JLabel();
        s1 = new javax.swing.JSpinner();
        jLabel37 = new javax.swing.JLabel();
        b1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        ComboB_image = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        comboB = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        priceB = new javax.swing.JLabel();
        s2 = new javax.swing.JSpinner();
        jLabel24 = new javax.swing.JLabel();
        b2 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        ComboC_image = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        comboC = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        priceC = new javax.swing.JLabel();
        s3 = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        b3 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        ComboD_image = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        comboD = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        priceD = new javax.swing.JLabel();
        s4 = new javax.swing.JSpinner();
        jLabel36 = new javax.swing.JLabel();
        b4 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        ComboE_image = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        comboE = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        priceE = new javax.swing.JLabel();
        s5 = new javax.swing.JSpinner();
        jLabel42 = new javax.swing.JLabel();
        b5 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        ComboF_image = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        comboF = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        priceF = new javax.swing.JLabel();
        s6 = new javax.swing.JSpinner();
        jLabel48 = new javax.swing.JLabel();
        b6 = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        ComboA_image5 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        Dessert8 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        price8 = new javax.swing.JLabel();
        s7 = new javax.swing.JSpinner();
        jLabel43 = new javax.swing.JLabel();
        b7 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        ComboB_image1 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        Dessert9 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        price9 = new javax.swing.JLabel();
        s8 = new javax.swing.JSpinner();
        jLabel49 = new javax.swing.JLabel();
        b8 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        ComboC_image1 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        Dessert10 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        price10 = new javax.swing.JLabel();
        s9 = new javax.swing.JSpinner();
        jLabel52 = new javax.swing.JLabel();
        b9 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        ComboD_image1 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        Dessert11 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        price11 = new javax.swing.JLabel();
        s10 = new javax.swing.JSpinner();
        jLabel55 = new javax.swing.JLabel();
        b10 = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        ComboE_image1 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        Dessert12 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        price12 = new javax.swing.JLabel();
        s11 = new javax.swing.JSpinner();
        jLabel58 = new javax.swing.JLabel();
        b11 = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        ComboF_image1 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        Dessert13 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        price13 = new javax.swing.JLabel();
        s12 = new javax.swing.JSpinner();
        jLabel61 = new javax.swing.JLabel();
        b12 = new javax.swing.JButton();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        total = new javax.swing.JTextField();
        subtotal = new javax.swing.JTextField();
        tax = new javax.swing.JTextField();
        remove = new javax.swing.JButton();
        purchase = new javax.swing.JButton();
        print = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1200, 675));
        setMinimumSize(new java.awt.Dimension(1200, 675));
        setPreferredSize(new java.awt.Dimension(1200, 675));
        setResizable(false);
        setSize(new java.awt.Dimension(1200, 675));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        reciept.setEditable(false);
        reciept.setColumns(20);
        reciept.setRows(5);
        reciept.setAutoscrolls(false);
        jScrollPane1.setViewportView(reciept);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(959, 57, 230, 523));

        cart_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price", "Qty", "Subtotal", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(cart_table);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 57, 260, 419));

        jPanel3.setBackground(new java.awt.Color(245, 223, 194));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setText("Welcome!");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, -1));

        logout.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        logout.setText("Logout");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 10, -1, 30));

        account.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        account.setText("Account");
        account.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accountMouseClicked(evt);
            }
        });
        jPanel3.add(account, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 10, -1, 30));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 1201, 50));

        menutabbed.setBackground(new java.awt.Color(245, 223, 194));

        jPanel4.setBackground(new java.awt.Color(245, 223, 194));
        jPanel4.setAutoscrolls(true);

        jPanel17.setBackground(new java.awt.Color(245, 223, 194));

        ComboA_image4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo A.png"))); // NOI18N
        ComboA_image4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboA_image4.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboA_image4.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboA_image4.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboA_image4.setSize(new java.awt.Dimension(147, 149));

        jLabel33.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel33.setText("Name:");

        jLabel35.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel35.setText("Price:");

        priceA.setText("0.00");

        jLabel37.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel37.setText("Quantity:");

        b1.setText("Add to Cart");
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboA_image4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(34, 34, 34)
                                .addComponent(comboA, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(14, 14, 14)
                                .addComponent(s1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(37, 37, 37)
                                .addComponent(priceA, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b1)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboA_image4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(comboA, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(priceA, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s1)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b1)
                .addGap(37, 37, 37))
        );

        jPanel8.setBackground(new java.awt.Color(245, 223, 194));

        ComboB_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo B.png"))); // NOI18N
        ComboB_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboB_image.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboB_image.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboB_image.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboB_image.setSize(new java.awt.Dimension(147, 149));

        jLabel20.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel20.setText("Name:");

        jLabel22.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel22.setText("Price:");

        priceB.setText("0.00");

        jLabel24.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel24.setText("Quantity:");

        b2.setText("Add to Cart");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboB_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(34, 34, 34)
                                .addComponent(comboB, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(14, 14, 14)
                                .addComponent(s2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(37, 37, 37)
                                .addComponent(priceB, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b2)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboB_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(priceB, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s2)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b2)
                .addGap(37, 37, 37))
        );

        jPanel9.setBackground(new java.awt.Color(245, 223, 194));

        ComboC_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo C.png"))); // NOI18N
        ComboC_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboC_image.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboC_image.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboC_image.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboC_image.setSize(new java.awt.Dimension(147, 149));

        jLabel26.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel26.setText("Name:");

        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel28.setText("Price:");

        priceC.setText("0.00");

        jLabel30.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel30.setText("Quantity:");

        b3.setText("Add to Cart");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboC_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(34, 34, 34)
                                .addComponent(comboC, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(14, 14, 14)
                                .addComponent(s3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addGap(37, 37, 37)
                                .addComponent(priceC, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b3)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboC_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(comboC, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(priceC, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s3)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b3)
                .addGap(37, 37, 37))
        );

        jPanel10.setBackground(new java.awt.Color(245, 223, 194));

        ComboD_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo D.png"))); // NOI18N
        ComboD_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboD_image.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboD_image.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboD_image.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboD_image.setSize(new java.awt.Dimension(147, 149));

        jLabel32.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel32.setText("Name:");

        jLabel34.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel34.setText("Price:");

        priceD.setText("0.00");

        jLabel36.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel36.setText("Quantity:");

        b4.setText("Add to Cart");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboD_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(34, 34, 34)
                        .addComponent(comboD, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(14, 14, 14)
                        .addComponent(s4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(37, 37, 37)
                        .addComponent(priceD, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(b4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(comboD, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(priceD, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s4)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b4)
                .addGap(37, 37, 37))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboD_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(245, 223, 194));

        ComboE_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo E.png"))); // NOI18N
        ComboE_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboE_image.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboE_image.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboE_image.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboE_image.setSize(new java.awt.Dimension(147, 149));

        jLabel38.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel38.setText("Name:");

        jLabel40.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel40.setText("Price:");

        priceE.setText("0.00");

        jLabel42.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel42.setText("Quantity:");

        b5.setText("Add to Cart");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboE_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(34, 34, 34)
                                .addComponent(comboE, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(14, 14, 14)
                                .addComponent(s5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addGap(37, 37, 37)
                                .addComponent(priceE, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b5)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboE_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(comboE, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(priceE, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s5)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b5)
                .addGap(37, 37, 37))
        );

        jPanel12.setBackground(new java.awt.Color(245, 223, 194));

        ComboF_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Combo F.png"))); // NOI18N
        ComboF_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboF_image.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboF_image.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboF_image.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboF_image.setSize(new java.awt.Dimension(147, 149));

        jLabel44.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel44.setText("Name:");

        jLabel46.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel46.setText("Price:");

        priceF.setText("0.00");

        jLabel48.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel48.setText("Quantity:");

        b6.setText("Add to Cart");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(ComboF_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addGap(34, 34, 34)
                                .addComponent(comboF, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addGap(14, 14, 14)
                                .addComponent(s6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addGap(37, 37, 37)
                                .addComponent(priceF, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b6))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboF_image, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(priceF, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s6)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b6)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        menutabbed.addTab("Combo", jPanel4);

        jPanel18.setBackground(new java.awt.Color(245, 223, 194));
        jPanel18.setAutoscrolls(true);

        jPanel19.setBackground(new java.awt.Color(245, 223, 194));

        ComboA_image5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/8.png"))); // NOI18N
        ComboA_image5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboA_image5.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboA_image5.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboA_image5.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboA_image5.setSize(new java.awt.Dimension(147, 149));

        jLabel39.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel39.setText("Name:");

        jLabel41.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel41.setText("Price:");

        price8.setText("0.00");

        jLabel43.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel43.setText("Quantity:");

        b7.setText("Add to Cart");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboA_image5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addGap(34, 34, 34)
                                .addComponent(Dessert8, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addGap(14, 14, 14)
                                .addComponent(s7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel41)
                                .addGap(37, 37, 37)
                                .addComponent(price8, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b7)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboA_image5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(Dessert8, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(price8, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s7)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b7)
                .addGap(37, 37, 37))
        );

        jPanel20.setBackground(new java.awt.Color(245, 223, 194));

        ComboB_image1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/9.png"))); // NOI18N
        ComboB_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboB_image1.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboB_image1.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboB_image1.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboB_image1.setSize(new java.awt.Dimension(147, 149));

        jLabel45.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel45.setText("Name:");

        jLabel47.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel47.setText("Price:");

        price9.setText("0.00");

        jLabel49.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel49.setText("Quantity:");

        b8.setText("Add to Cart");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboB_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addGap(34, 34, 34)
                                .addComponent(Dessert9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addGap(14, 14, 14)
                                .addComponent(s8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addGap(37, 37, 37)
                                .addComponent(price9, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b8)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboB_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Dessert9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(price9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s8)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b8)
                .addGap(37, 37, 37))
        );

        jPanel21.setBackground(new java.awt.Color(245, 223, 194));

        ComboC_image1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/10.png"))); // NOI18N
        ComboC_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboC_image1.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboC_image1.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboC_image1.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboC_image1.setSize(new java.awt.Dimension(147, 149));

        jLabel50.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel50.setText("Name:");

        jLabel51.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel51.setText("Price:");

        price10.setText("0.00");

        jLabel52.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel52.setText("Quantity:");

        b9.setText("Add to Cart");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboC_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addGap(34, 34, 34)
                                .addComponent(Dessert10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addGap(14, 14, 14)
                                .addComponent(s9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addGap(37, 37, 37)
                                .addComponent(price10, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b9)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboC_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(Dessert10, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(price10, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s9)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b9)
                .addGap(37, 37, 37))
        );

        jPanel22.setBackground(new java.awt.Color(245, 223, 194));

        ComboD_image1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/11.png"))); // NOI18N
        ComboD_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboD_image1.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboD_image1.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboD_image1.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboD_image1.setSize(new java.awt.Dimension(147, 149));

        jLabel53.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel53.setText("Name:");

        jLabel54.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel54.setText("Price:");

        price11.setText("0.00");

        jLabel55.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel55.setText("Quantity:");

        b10.setText("Add to Cart");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboD_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addGap(34, 34, 34)
                        .addComponent(Dessert11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel55)
                        .addGap(14, 14, 14)
                        .addComponent(s10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(37, 37, 37)
                        .addComponent(price11, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(b10)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(Dessert11, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(price11, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s10)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b10)
                .addGap(37, 37, 37))
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboD_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel23.setBackground(new java.awt.Color(245, 223, 194));

        ComboE_image1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/12.png"))); // NOI18N
        ComboE_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboE_image1.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboE_image1.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboE_image1.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboE_image1.setSize(new java.awt.Dimension(147, 149));

        jLabel56.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel56.setText("Name:");

        jLabel57.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel57.setText("Price:");

        price12.setText("0.00");

        jLabel58.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel58.setText("Quantity:");

        b11.setText("Add to Cart");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboE_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGap(34, 34, 34)
                                .addComponent(Dessert12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addGap(14, 14, 14)
                                .addComponent(s11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(jLabel57)
                                .addGap(37, 37, 37)
                                .addComponent(price12, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b11)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboE_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(Dessert12, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(price12, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s11)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b11)
                .addGap(37, 37, 37))
        );

        jPanel24.setBackground(new java.awt.Color(245, 223, 194));

        ComboF_image1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/13.png"))); // NOI18N
        ComboF_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ComboF_image1.setMaximumSize(new java.awt.Dimension(147, 149));
        ComboF_image1.setMinimumSize(new java.awt.Dimension(147, 149));
        ComboF_image1.setPreferredSize(new java.awt.Dimension(147, 149));
        ComboF_image1.setSize(new java.awt.Dimension(147, 149));

        jLabel59.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel59.setText("Name:");

        jLabel60.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel60.setText("Price:");

        price13.setText("0.00");

        jLabel61.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel61.setText("Quantity:");

        b12.setText("Add to Cart");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ComboF_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jLabel59)
                                .addGap(34, 34, 34)
                                .addComponent(Dessert13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addGap(14, 14, 14)
                                .addComponent(s12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addGap(37, 37, 37)
                                .addComponent(price13, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(b12)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(ComboF_image1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Dessert13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(price13, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(s12)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(b12)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        menutabbed.addTab("Dessert", jPanel18);

        getContentPane().add(menutabbed, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 57, 684, -1));

        jLabel74.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel74.setText("Tax:");
        getContentPane().add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(726, 489, 37, -1));

        jLabel75.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel75.setText("Sub Total:");
        getContentPane().add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(701, 524, 68, -1));

        jLabel76.setFont(new java.awt.Font("Trebuchet MS", 1, 20)); // NOI18N
        jLabel76.setText("Total:");
        getContentPane().add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(708, 552, -1, -1));

        total.setEditable(false);
        total.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        getContentPane().add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(781, 552, 148, 28));

        subtotal.setEditable(false);
        subtotal.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        getContentPane().add(subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(781, 517, 146, -1));

        tax.setEditable(false);
        tax.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        getContentPane().add(tax, new org.netbeans.lib.awtextra.AbsoluteConstraints(781, 482, 145, -1));

        remove.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        remove.setText("Remove");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        getContentPane().add(remove, new org.netbeans.lib.awtextra.AbsoluteConstraints(823, 598, -1, -1));

        purchase.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        purchase.setText("Purchase");
        purchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseActionPerformed(evt);
            }
        });
        getContentPane().add(purchase, new org.netbeans.lib.awtextra.AbsoluteConstraints(725, 598, -1, -1));

        print.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        print.setText("Print Reciept");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });
        getContentPane().add(print, new org.netbeans.lib.awtextra.AbsoluteConstraints(1018, 598, -1, -1));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 630, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/background_menu.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 680));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void purchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseActionPerformed
       
    double totalAmount = getTotalAmount();
    List<OrderItem_Customer> orderItems = getOrderItems();

    // Complete the order
    completeOrder(customerID, orderItems, totalAmount);
        
    }//GEN-LAST:event_purchaseActionPerformed

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked

        Login lg = new Login();
        lg.setVisible(true);
        lg.pack();
        lg.setLocationRelativeTo(null);
        this.dispose();
        
    }//GEN-LAST:event_logoutMouseClicked

    private void accountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_accountMouseClicked
            if (customerID > 0) {
            Customer_UserDetails userDetails = new Customer_UserDetails();
            userDetails.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "User not logged in", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_accountMouseClicked

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        int selectedRow = cart_table.getSelectedRow();

    // If no row is selected, show a message
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an item to remove.");
        return;
    }

    // Call the remove method to remove the item from the table only (not from database)
    removeItemFromCart(selectedRow);
    
    }//GEN-LAST:event_removeActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        printReceipt();
    }//GEN-LAST:event_printActionPerformed

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
       
        
    }//GEN-LAST:event_b1ActionPerformed

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
            java.util.logging.Logger.getLogger(dashboard_menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dashboard_menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dashboard_menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dashboard_menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               String username = Login.getCurrentUser(); 
               if (username != null && !username.isEmpty()) {
            new dashboard_menu().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "No user logged in", "Error", JOptionPane.ERROR_MESSAGE);
        }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ComboA_image4;
    private javax.swing.JLabel ComboA_image5;
    private javax.swing.JLabel ComboB_image;
    private javax.swing.JLabel ComboB_image1;
    private javax.swing.JLabel ComboC_image;
    private javax.swing.JLabel ComboC_image1;
    private javax.swing.JLabel ComboD_image;
    private javax.swing.JLabel ComboD_image1;
    private javax.swing.JLabel ComboE_image;
    private javax.swing.JLabel ComboE_image1;
    private javax.swing.JLabel ComboF_image;
    private javax.swing.JLabel ComboF_image1;
    private javax.swing.JLabel Dessert10;
    private javax.swing.JLabel Dessert11;
    private javax.swing.JLabel Dessert12;
    private javax.swing.JLabel Dessert13;
    private javax.swing.JLabel Dessert8;
    private javax.swing.JLabel Dessert9;
    private javax.swing.JLabel account;
    private javax.swing.JButton b1;
    private javax.swing.JButton b10;
    private javax.swing.JButton b11;
    private javax.swing.JButton b12;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JButton b5;
    private javax.swing.JButton b6;
    private javax.swing.JButton b7;
    private javax.swing.JButton b8;
    private javax.swing.JButton b9;
    private javax.swing.JTable cart_table;
    private javax.swing.JLabel comboA;
    private javax.swing.JLabel comboB;
    private javax.swing.JLabel comboC;
    private javax.swing.JLabel comboD;
    private javax.swing.JLabel comboE;
    private javax.swing.JLabel comboF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel logout;
    private javax.swing.JTabbedPane menutabbed;
    private javax.swing.JLabel price10;
    private javax.swing.JLabel price11;
    private javax.swing.JLabel price12;
    private javax.swing.JLabel price13;
    private javax.swing.JLabel price8;
    private javax.swing.JLabel price9;
    private javax.swing.JLabel priceA;
    private javax.swing.JLabel priceB;
    private javax.swing.JLabel priceC;
    private javax.swing.JLabel priceD;
    private javax.swing.JLabel priceE;
    private javax.swing.JLabel priceF;
    private javax.swing.JButton print;
    private javax.swing.JButton purchase;
    private javax.swing.JTextArea reciept;
    private javax.swing.JButton remove;
    private javax.swing.JSpinner s1;
    private javax.swing.JSpinner s10;
    private javax.swing.JSpinner s11;
    private javax.swing.JSpinner s12;
    private javax.swing.JSpinner s2;
    private javax.swing.JSpinner s3;
    private javax.swing.JSpinner s4;
    private javax.swing.JSpinner s5;
    private javax.swing.JSpinner s6;
    private javax.swing.JSpinner s7;
    private javax.swing.JSpinner s8;
    private javax.swing.JSpinner s9;
    private javax.swing.JTextField subtotal;
    private javax.swing.JTextField tax;
    private javax.swing.JTextField total;
    // End of variables declaration//GEN-END:variables
}
