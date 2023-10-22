/**
 * @author Mathew Nuval
 * Comp 440
 * Professor Ebrahimi
 * 10/09/2023
 */

package edu.csun.desktopapp440.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Establish user's connection between
 * the user and database for
 * database manipulation
 */
public class UsersDatabase {

    /**
     * Log variable
     */
    private static final Logger log;

    /**
     * Logger for debugging
     */
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(UsersDatabase.class.getName());
    }

    /**
     * Establish and provide access in database
     *
     * @return connection
     */
    public Connection getDatabaseConnection() {
        try {
            log.info("Loading application properties");
            Properties properties = new Properties();
            properties.load(UsersDatabase.class.getClassLoader().getResourceAsStream("application.properties"));

            log.info("Connecting to the database");
            Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
            log.info("Database connection test: " + connection.getCatalog());

            return connection;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}