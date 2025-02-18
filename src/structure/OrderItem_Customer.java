package structure;

//used in dashboard_menu frame

public class OrderItem_Customer {
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    public OrderItem_Customer(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price * quantity;  // Calculate total for this product
    }

    // Getters
    public String getProductName() { 
        
        return productName; }
    
    public int getQuantity() { 
        
        return quantity; }
    
    public double getPrice() { 
        
        return price; }
    
    public double getSubtotal() { 
        
        return subtotal; }
    
    public int getProductId() {
        return productId;
    }
    
    
}

