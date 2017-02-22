package co.za.st.controller;

import co.za.st.db.OAuth2Db;
import co.za.st.dto.Client;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.JSONHttpServletRequestWrapper;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.OAuthServerRegistrationRequest;
import org.apache.oltu.oauth2.ext.dynamicreg.server.response.OAuthServerRegistrationResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

/**
 * Created by StevenT on 2017/02/21.
 */
@Controller
public class STOAuth2RegistrationController {

    @RequestMapping(value = "/register", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public OAuthResponse showIndex(HttpServletRequest request) throws OAuthSystemException {
        try {
            OAuthServerRegistrationRequest oauthRegRequest = new OAuthServerRegistrationRequest(new JSONHttpServletRequestWrapper(request));
            String type = oauthRegRequest.getType();
            oauthRegRequest.discover();
            String name = oauthRegRequest.getClientName();
            String url = oauthRegRequest.getClientUrl();
            String description = oauthRegRequest.getClientDescription();
            String redirectUrl = oauthRegRequest.getRedirectURI();

            long issuedAt = System.currentTimeMillis();
            // Expires in one day
            long expires = issuedAt + 86400000;
            Date date = new Date(System.currentTimeMillis());

            String clientId = generateClientId();
            String secret = generateClientSecret();

            Client client = new Client();
            client.setName(name);
            client.setClientId(clientId);
            client.setSecret(secret);
            client.setType(type);
            client.setUrl(url);
            client.setDescription(description);

            OAuth2Db oAuth2Db = new OAuth2Db();
            oAuth2Db.insertClient(client);

            OAuthResponse response = OAuthServerRegistrationResponse
                    .status(HttpServletResponse.SC_OK)
                    .setClientId(clientId)
                    .setClientSecret(secret)
                    .setIssuedAt(date.toString())
                    .setExpiresIn(expires)
                    .buildJSONMessage();
            return response;
        } catch (OAuthProblemException ex) {
            ex.printStackTrace();
        } catch (OAuthSystemException ex) {
            ex.printStackTrace();
        }

        return OAuthServerRegistrationResponse.status(500).buildJSONMessage();
    }

    public String generateClientId() {
        return UUID.randomUUID().toString();
    }

    public String generateClientSecret() {
        return UUID.randomUUID().toString();
    }
}
