package co.za.st.handler;

import co.za.st.db.iAuthDb;
import co.za.st.dto.Client;
import co.za.st.exceptions.ClientExistsException;
import co.za.st.exceptions.ClientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by stevy on 2017/02/26.
 */
@Slf4j
public class ClientHandler implements iClientHandler {

    @Autowired
    private iAuthDb clientDb;

    public ClientHandler() {
    }

    public Client saveClient(Client client) throws ClientExistsException {
        if (this.clientExists(client.getName())) {
            throw new ClientExistsException(client.getName());
        } else {
            client.setClientId(generateClientId());
            client.setSecret(generateClientSecret());
            clientDb.insertClient(client);
            return client;
        }
    }

    public boolean clientExists(String clientName) {
        return clientDb.clientExists(clientName);
    }

    public Client getClient(String clientId, String clientSecret) throws ClientNotFoundException {
        Client client = clientDb.getClient(clientId, clientSecret);
        if (client == null) {
            throw new ClientNotFoundException();
        }
        return client;
    }

    public boolean validateClient(String clientId, String clientSecret) {
        Client client = clientDb.getClient(clientId, clientSecret);
        if (client == null) {
            return false;
        }
        return true;
    }

    public String generateClientId() {
        return UUID.randomUUID().toString();
    }

    public String generateClientSecret() {
        return UUID.randomUUID().toString();
    }
}