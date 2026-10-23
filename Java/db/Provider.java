package oes.db;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
public class Provider {
    public static Connection getOracleConnection()
    {
        Connection con = null;
	try
        { 
            Properties props = new Properties();
            try (InputStream input = Provider.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (Exception e) {
                System.out.println("Failed to load db.properties: " + e.getMessage());
            }
            String driver = props.getProperty("driver", "oracle.jdbc.OracleDriver");
            String url = props.getProperty("url", "jdbc:oracle:thin:@localhost:1521:xe");
            String user = props.getProperty("user", "system");
            String pass = props.getProperty("password", "pass123");

            try {
                Class.forName(driver);
                con = DriverManager.getConnection(url, user, pass);
                System.out.println("Oracle connection successful!");
            } catch (Exception oracleEx) {
                System.out.println("Could not connect to Oracle (" + oracleEx.getMessage() + "). Attempting local DB fallback...");
                try {
                    Class.forName("org.h2.Driver");
                    String h2Url = "jdbc:h2:./quizdb;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";
                    con = DriverManager.getConnection(h2Url, "system", "pass123");
                    System.out.println("Connected to local database fallback successfully!");
                } catch (Exception h2Ex) {
                    System.out.println("Local fallback connection failed: " + h2Ex.getMessage());
                    throw oracleEx;
                }
            }
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
        return con;
    }
}
	