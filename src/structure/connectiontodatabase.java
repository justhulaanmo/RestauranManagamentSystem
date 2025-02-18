package structure;
import java.sql.Connection; 
import java.sql.DriverManager; 
//import java.sql.SQLException;
//import java.sql.ResultSet;

public class connectiontodatabase {
    
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/myresto"; // Replace with your database name
            String user = "bea"; // Replace with your MySQL username
            String password = "052021"; // Replace with your MySQL password
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

        


