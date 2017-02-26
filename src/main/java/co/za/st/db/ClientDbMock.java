package co.za.st.db;

import co.za.st.client.Client;
import co.za.st.client.iClient;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by stevy on 2017/02/25.
 */
public class ClientDbMock implements iClient {

    private HashMap<String, Client> clients;

    public void insertClient(Client client) throws OAuthSystemException {
        if (clients == null) {
            clients = new HashMap<String, Client>();
        }
        clients.put(client.getClientId() + client.getSecret(), client);
    }

    public Client getClient(String clientId, String secret) {
        return clients.get(clientId + secret);
    }
}