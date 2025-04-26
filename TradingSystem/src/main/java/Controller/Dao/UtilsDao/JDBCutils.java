package Controller.Dao.UtilsDao;

import java.sql.*;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class JDBCutils {


    private static String driver = null;
    private static String url = null;
    private static String username = null;
    private static String password = null;

    static {

        try {
            InputStream in = JDBCutils.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(in);

            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {

            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
