package co.za.st.handler;

import co.za.st.db.iAuthDb;
import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.exceptions.ClientNotFoundException;
import org.apache.oltu.oauth2.jwt.JWT;
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

    private final Long expires = new Long(3600);

    public Token generateToken(Client client, String scope) throws ClientNotFoundException {
        if (clientHandler.validateClient(client.getClientId(), client.getSecret())) {
            Token token = new Token();
            token.setAccessToken(generateAccessToken());
            token.setExpiresIn(expires);
            token.setTimeIn(new Long(System.currentTimeMillis()));
            token.setRefreshToken(generateRefreshToken());
            token.setScope(scope);
            authDb.insertToken(client, token);
            return token;
        } else {
            throw new ClientNotFoundException(client.getName());
        }
    }

    public boolean validateToken(String token) {
        authDb.purgeTokens();
        return authDb.tokenExists(token);
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString();
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

//    private String generateAccessToken() {
//        JWT jwt = new JWT.Builder("this is a test").build();
//        return jwt.toString();
//    }
//
//    private String generateRefreshToken() {
//        JWT jwt = new JWT.Builder("this is a test").build();
//        return jwt.toString();
//    }
}
