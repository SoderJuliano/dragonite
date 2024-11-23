package org.app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * Hashes a password using BCrypt.
     *
     * @param password The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if the provided password matches the hashed password.
     *
     * @param password The password to check
     * @param hashedPassword The hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}