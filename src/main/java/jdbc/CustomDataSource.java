package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("app.properties");
        try {
            props.load(stream);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static CustomDataSource getInstance() {
        if(instance == null) {
            synchronized (CustomDataSource.class) {
                Properties properties = loadProperties();
                String driver = null;
                String url = null;
                String password = null;
                String name = null;
                if (properties != null) {
                    driver = properties.getProperty("postgres.driver");
                    url = properties.getProperty("postgres.url");
                    password = properties.getProperty("postgres.password");
                    name = properties.getProperty("postgres.name");
                }
                instance = new CustomDataSource(driver, url, name, password);
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
