package co.za.st.handler;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.exceptions.ClientNotFoundException;

import java.util.Set;

/**
 * Created by stevy on 2017/02/26.
 */
public interface iTokenHandler {

    Token generateToken(Client client, String scopes) throws ClientNotFoundException;

    boolean validateToken(String accessToken);
}
