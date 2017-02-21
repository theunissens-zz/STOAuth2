package co.za.st.db;

import co.za.st.dto.Client;
import org.postgresql.jdbc4.Jdbc4Connection;

import java.sql.*;
import java.util.Properties;

/**
 * Created by StevenT on 2017/02/21.
 * We are just going to use this shitty DB implementation to persist and get data for now
 */
public class OAuth2Db {

    private String user = "postgres";
    private String password = "mjollnir24";

    public OAuth2Db() {

    }

    public void insertClient(Client client) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            String sql = String.format("INSERT INTO \"client\"(\"clientid\", \"clientsecret\") VALUES(\'%s\', \'%s\')", client.getClientName(), client.getClientSecret());
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Client retrieveClient() {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM client");
            if(rs.next()) {
                String clientid = rs.getString("clientid");
                String clientsecret = rs.getString("clientsecret");
                return new Client(clientid, clientsecret);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
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
            //Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(getPostgresConnectionJdbcString(false), props);
            Statement stm = connection.createStatement();
            stm.execute("CREATE DATABASE oauth2");
            connection = DriverManager.getConnection(getPostgresConnectionJdbcString(true), props);
            stm = connection.createStatement();
            stm.execute("CREATE TABLE client (id serial primary key, clientid varchar(256), clientsecret varchar(256));");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        CREATE DATABASE client;
//\c testdatabase
//        CREATE TABLE client (id serial primary key, clientid varchar(256), clientsecret varchar(256));
    }
}
