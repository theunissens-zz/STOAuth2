package co.za.st.handler;

import co.za.st.db.iAuthDb;
import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.exceptions.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by stevy on 2017/02/26.
 */
public class TokenHandler implements iTokenHandler {

    @Autowired
    private iAuthDb authDb;

    @Autowired
    private iClientHandler clientHandler;

    private final String expires = "3600";

    public Token generateToken(Client client) throws ClientNotFoundException {
        if (clientHandler.validateClient(client.getClientId(), client.getSecret())) {
            Token token = new Token(generateAccessToken(), expires);
            authDb.insertToken(client, token);
            return token;
        } else {
            throw new ClientNotFoundException(client.getName());
        }
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString();
    }
}
