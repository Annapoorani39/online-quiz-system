package oes.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oes.db.Provider;

/**
 * Checks whether the {@code admintable} contains at least one admin record.
 * If the table is empty, a new admin is created using the environment variables
 * {@code ADMIN_USER} and {@code ADMIN_PASS}. This method is intended to be called
 * once at application startup (e.g., from a servlet context listener) and does
 * not run on every request.
 */
public class AdminInitializer {
    public static void ensureAdminExists() {
        try (Connection con = Provider.getOracleConnection()) {
            // 1. Check for existing admins
            String countSql = "SELECT COUNT(*) FROM admintable";
            try (PreparedStatement ps = con.prepareStatement(countSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Admin already exists – nothing to do
                    return;
                }
            }

            // 2. No admin found – create one from env vars (fallback to defaults)
            String user = System.getenv("ADMIN_USER");
            String pass = System.getenv("ADMIN_PASS");
            if (user == null || pass == null) {
                // Fallback values – encourage the user to set env vars
                user = "admin";
                pass = "admin";
                System.out.println("[AdminInitializer] Environment variables ADMIN_USER/ADMIN_PASS not set. Using default credentials 'admin/admin'.");
            }
            String insertSql = "INSERT INTO admintable (userid, password) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.executeUpdate();
                System.out.println("[AdminInitializer] Default admin account created: " + user);
            }
        } catch (Exception e) {
            System.out.println("[AdminInitializer] Error while ensuring admin existence: " + e.getMessage());
        }
    }
}
