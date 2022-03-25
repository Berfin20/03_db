import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

    private static final String HOST     = "localhost";
    private static final int    PORT     = 0000;
    private static final String DATABASE = "DatabaseNavn";
    private static final String USERNAME = "DatabaseUser";
    private static final String PASSWORD = "DatabasePassword";

    private static final String DELIMITER = ";;";

    private Connection connection = null;

    Connector() {
        try {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            System.out.println("Connection made");
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace(System.err);
        } finally {
            if (connection == null) System.exit(-1);
        }
    }

    Connection getConnection() {
        return connection;
    }
}