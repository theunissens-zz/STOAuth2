package co.za.st.client;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * Created by stevy on 2017/02/25.
 */
public interface iClient {
    void insertClient(Client client) throws OAuthSystemException;
    Client getClient(String clientId, String secret);
}
