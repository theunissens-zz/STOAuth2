package co.za.st.db;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;

/**
 * Created by stevy on 2017/02/25.
 */
public class AuthDbMock implements iAuthDb {

    private HashMap<String, Client> clients;
    private HashMap<String, Token> clientTokens;

    public void setup() {
        createAuthDb();
        createClientTable();
        createTokenTable();
    }

    public void createAuthDb() {
    }

    public void createClientTable() {
        clients = new HashMap<String, Client>();
    }

    public void createTokenTable() {
        clientTokens = new HashMap<String, Token>();
    }

    public void dropDb() {
        clients = null;
        clientTokens = null;
    }

    public void insertClient(Client client) {
        clients.put(client.getClientId(), client);
    }

    public boolean clientExists(String clientId) {
        if (clients.get(clientId) != null)
            return true;
        else
            return false;
    }

    public Client getClient(String clientId, String clientSecret) {
        Client client = this.clients.get(clientId);
        if (client != null && client.getSecret().equals(clientSecret))
            return client;
        else return null;
    }

    public void insertToken(Client client, Token token) {
        clientTokens.put(client.getClientId(), token);
    }

    public Token getToken(String clientId) {
        return clientTokens.get(clientId);
    }

    public void deleteTokens(String[] tokens) {

    }

    public void purgeTokens() {
        Object[] keys = clientTokens.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            Token token = clientTokens.get(keys[i]);
            long now = System.currentTimeMillis();
            long validFOr = token.getTimeIn() + token.getExpiresIn();
            if (now > validFOr) {
                clientTokens.remove(keys[i]);
            }
        }
    }

    public boolean tokenExists(String accessToken) {
        Object[] keys = clientTokens.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            Token token = clientTokens.get(keys[i]);
            if (token.getAccessToken().equals(accessToken)) {
                return true;
            }
        }
        return false;
    }
}