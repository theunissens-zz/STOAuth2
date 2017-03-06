package co.za.st.handler;

import co.za.st.dto.Client;
import co.za.st.exceptions.ClientExistsException;
import co.za.st.exceptions.ClientNotFoundException;

/**
 * Created by stevy on 2017/02/26.
 */
public interface iClientHandler {
    boolean clientExists(String clientName) throws ClientNotFoundException;

    Client saveClient(Client client) throws ClientExistsException;

    Client getClient(String clientId, String clientSecret) throws ClientNotFoundException;

    boolean validateClient(String clientId, String clientSecret);

    String generateClientId();

    String generateClientSecret();
}
