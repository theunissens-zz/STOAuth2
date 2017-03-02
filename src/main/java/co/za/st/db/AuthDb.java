package co.za.st.db;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

/**
 * Created by StevenT on 2017/02/21.
 * We are just going to use this shitty DB implementation to persist and get data for now
 */
public class AuthDb implements iAuthDb {

    private HikariDataSource dbPool;

    private String user = "postgres";
    private String password = "mjollnir24";

    public AuthDb() {

    }

    public void insertClient(Client client) {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            String sql = String.format("INSERT INTO \"client\"(\"name\", \"clientid\", \"secret\", \"type\", \"url\", \"redirecturl\", \"description\") VALUES(\'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\', \'%s\')", client.getName(), client.getClientId(), client.getSecret(), client.getType(), client.getUrl(), client.getRedirectUrl(), client.getDescription());
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Client getClient(String clientId, String secret) {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            String sql = String.format("SELECT * FROM client WHERE clientid = \'%s\' AND secret = \'%s\'", clientId, secret);

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
        // NOTE! hash the token when storing in db
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            long expires = token.getTimeIn() + token.getExpiresIn();
            String sql = String.format("INSERT INTO \"token\"(\"clientid\", \"token\", \"expires\") VALUES(\'%s\', \'%s\', \'%s\')", client.getClientId(), token.getAccessToken(), expires);
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Token getToken(String clientId) {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            String sql = String.format("SELECT * FROM token WHERE clientid = \'%s\'", clientId);

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                String accessToken = rs.getString("token");

                Token token = new Token();
                token.setAccessToken(accessToken);
                return token;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void purgeTokens() {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();

            long now = System.currentTimeMillis();
            String sql = String.format("DELETE FROM token WHERE %d > expires", now);

            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteTokens(String[] tokens) {
        Connection conn;
        try {
            conn = this.dbPool.getConnection();
            Statement batchStm = conn.createStatement();

            for(String token : tokens) {
                String sql = String.format("DELETE * FROM token WHERE id = %s", token);
                batchStm.addBatch(sql);
            }
            batchStm.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean tokenExists(String token) {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            String sql = String.format("SELECT * FROM token WHERE token = \'%s\'", token);

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean clientExists(String clientId) {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            String sql = String.format("SELECT * FROM client WHERE clientid = \'%s\'", clientId);

            ResultSet rs = stm.executeQuery(sql);
            if(rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setup() {
        try {
            this.dbPool = connectToAuthDb();
        } catch (Exception ex) {
            createAuthDb();
            createClientTable();
            createTokenTable();
        }
    }

    private HikariDataSource connectToAuthDb() throws Exception {
        // Try and connect to the auth db
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", "localhost", "5432", "oauth2"));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }

    public void createAuthDb()  {
        // Assume connection could not be made, because db doesnt exist
        // Create db and tables
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/", "localhost", "5432"));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        HikariDataSource dataSource = new HikariDataSource(config);

        Connection conn;
        Statement stm;

        try {
            conn = dataSource.getConnection();
            stm = conn.createStatement();
            stm.execute("CREATE DATABASE oauth2");
            this.dbPool = connectToAuthDb();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dataSource.close();
        }
    }

    public void createClientTable()  {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            stm.execute("CREATE TABLE client (id serial primary key, name varchar(64), clientid varchar(256), secret varchar(256), type varchar(64), url varchar(256), redirecturl varchar(256), description text);create unique index client_unique_idx on client (clientid);");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createTokenTable()  {
        Connection conn;
        Statement stm;
        try {
            conn = this.dbPool.getConnection();
            stm = conn.createStatement();
            stm.execute("CREATE TABLE token (id serial primary key, clientid varchar(256) references client(clientid), token varchar(256), expires bigint);");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dropDb() {
        this.dbPool.close();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/", "localhost", "5432"));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        HikariDataSource dataSource = new HikariDataSource(config);

        Connection conn;
        Statement stm;

        try {
            conn = dataSource.getConnection();
            stm = conn.createStatement();
            stm.execute("DROP DATABASE IF EXISTS oauth2;");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dataSource.close();
        }
    }
}