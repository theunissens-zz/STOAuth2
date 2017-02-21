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

    public OAuth2Db() {

    }

    public void insertClient(Client client) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            String sql = String.format("INSERT INTO \"client\"(\"clientid\", \"clientsecret\") VALUES(\'%s\', \'%s\')", client.getClientId(), client.getClientSecret());
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
        props.setProperty("user", "imqs");
        props.setProperty("password", "1mq5p@55w0rd");
        return DriverManager.getConnection(getPostgresConnectionJdbcString(), props);
    }

    public String getPostgresConnectionJdbcString() {
        return String.format("jdbc:postgresql://%s:%s/%s", "localhost", "5432", "oauth2");
    }
}
