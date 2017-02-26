package co.za.st.db;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.sql.*;
import java.util.Properties;

/**
 * Created by StevenT on 2017/02/21.
 * We are just going to use this shitty DB implementation to persist and get data for now
 */
public class AuthDb implements iAuthDb {

    private String user = "postgres";
    private String password = "mjollnir24";

    public AuthDb() {

    }

    public void insertClient(Client client) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            String sql = String.format("INSERT INTO \"handler\"(\"name\", \"clientid\", \"secret\", \"type\", \"url\", \"redirecturl\", \"description\") VALUES(\'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\')", client.getName(), client.getClientId(), client.getSecret(), client.getType(), client.getUrl(), client.getRedirectUrl(), client.getDescription());
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Client getClient(String clientId, String secret) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            String sql = String.format("SELECT * FROM handler WHERE clientid = \'%s\' AND secret = \'%s\'", clientId, secret);

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String url = rs.getString("url");
                String redirectUrl = rs.getString("redirecturl");
                String description = rs.getString("description");

                Client client = new Client();
                client.setName(name);
                client.setClientId(clientId);
                client.setSecret(secret);
                client.setType(type);
                client.setUrl(url);
                client.setRedirectUrl(redirectUrl);
                client.setDescription(description);

                return client;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void insertToken(Client client, Token token) {

    }

    public Token getToken(String clientId) {
        return null;
    }

    public boolean clientExists(String clientId) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            String sql = String.format("SELECT * FROM handler WHERE clientid = \'%s\'", clientId);

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        try {
            Connection connection = DriverManager.getConnection(getPostgresConnectionJdbcString(true), props);
            return connection;
        } catch (SQLException ex) {
            // maybe the db doesnt exist, and now we create it!
            createClientTable();
            Connection connection = DriverManager.getConnection(getPostgresConnectionJdbcString(true), props);
            return connection;
        }
    }

    public String getPostgresConnectionJdbcString(boolean withDb) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (withDb)
            return String.format("jdbc:postgresql://%s:%s/%s", "localhost", "5432", "oauth2");
        else
            return String.format("jdbc:postgresql://%s:%s/", "localhost", "5432");
    }

    private void createClientTable()  {
        try {
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            Connection connection = DriverManager.getConnection(getPostgresConnectionJdbcString(false), props);
            Statement stm = connection.createStatement();
            stm.execute("CREATE DATABASE oauth2");
            connection = DriverManager.getConnection(getPostgresConnectionJdbcString(true), props);
            stm = connection.createStatement();
            stm.execute("CREATE TABLE client (id serial primary key, name varchar(64), clientid varchar(256), secret varchar(256), type varchar(64), url varchar(256), redirecturl varchar(256), description text);");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}