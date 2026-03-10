package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static util.ConnectionConst.*;


public class DBUtil {

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return conn;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
