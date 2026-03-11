package util;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static util.ConnectionConst.*;

public class DBUtil {

    private static final DataSource dataSource;

    static {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(URL);
        ds.setUser(USERNAME);
        ds.setPassword(PASSWORD);
        dataSource = ds;
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
