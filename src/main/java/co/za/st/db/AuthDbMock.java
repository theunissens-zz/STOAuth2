package co.za.st.db;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.util.HashMap;

/**
 * Created by stevy on 2017/02/25.
 */
public class AuthDbMock implements iAuthDb {

    private HashMap<String, Client> clients = new HashMap<String, Client>();
    private HashMap<String, Token> clientTokens = new HashMap<String, Token>();

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
}