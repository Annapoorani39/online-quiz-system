package oes.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import oes.db.Provider;

public class DbSchemaInitializer {
    public static void initializeTables() {
        try (Connection con = Provider.getOracleConnection()) {
            if (con == null) {
                System.out.println("DbSchemaInitializer: Connection is null, skipping schema check.");
                return;
            }
            try (Statement st = con.createStatement()) {
                // Create admintable if not exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS admintable (" +
                        "userid VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(50)" +
                        ")");

                // Create studenttable if not exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS studenttable (" +
                        "userid VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(50), " +
                        "name VARCHAR(100)" +
                        ")");

                // Create instructiontable if not exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS instructiontable (" +
                        "instruction VARCHAR(500)" +
                        ")");

                // Create questiontable if not exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS questiontable (" +
                        "question VARCHAR(500), " +
                        "a VARCHAR(200), " +
                        "b VARCHAR(200), " +
                        "c VARCHAR(200), " +
                        "d VARCHAR(200), " +
                        "answer VARCHAR(50)" +
                        ")");

                // If admintable is empty, seed initial admin
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM admintable")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        st.executeUpdate("INSERT INTO admintable (userid, password) VALUES ('admin', 'admin')");
                        System.out.println("DbSchemaInitializer: Initial admin seeded (admin/admin).");
                    }
                }

                // If instructiontable is empty, seed a sample instruction
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM instructiontable")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        st.executeUpdate("INSERT INTO instructiontable (instruction) VALUES ('All questions are compulsory.')");
                        st.executeUpdate("INSERT INTO instructiontable (instruction) VALUES ('Each question carries equal marks.')");
                        System.out.println("DbSchemaInitializer: Initial instructions seeded.");
                    }
                }

                // If questiontable is empty, seed a sample question
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM questiontable")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        st.executeUpdate("INSERT INTO questiontable (question, a, b, c, d, answer) VALUES ('What is Java?', 'A programming language', 'A coffee brand only', 'An OS', 'A browser', 'a')");
                        System.out.println("DbSchemaInitializer: Initial question seeded.");
                    }
                }
            }
            System.out.println("DbSchemaInitializer: Database tables verified successfully.");
        } catch (Exception e) {
            System.out.println("DbSchemaInitializer: Schema init notice - " + e.getMessage());
        }
    }
}
