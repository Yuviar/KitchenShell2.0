package config;

/**
 *
 * @author Fazaa
 */
public class Session {
    private static String username, id, role;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Session.username = username;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Session.id = id;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        Session.role = role;
    }
    
}
