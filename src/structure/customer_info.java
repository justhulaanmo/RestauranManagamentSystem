package structure;

public class customer_info {
    
    public static String username;
    private String name;
    private String address;
    private String email;
    private String mobilenumber;
    private String password;

    public customer_info(String username, String name, String address, String email, String mobilenumber, String password) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        
    }

    public static String getUsername() { 
        return username; }
    public String getFullname() { 
        return name; }
    public String getAddress() { 
        return address; }
    public String getEmail() { 
        return email; }
    public String getMobileNumber() { 
        return mobilenumber; }
    public String getPassword() { 
        return password; }
}



    

