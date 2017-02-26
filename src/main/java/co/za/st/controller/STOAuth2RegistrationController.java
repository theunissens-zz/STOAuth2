package co.za.st.controller;

import co.za.st.client.iClient;
import co.za.st.client.Client;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.JSONHttpServletRequestWrapper;
import org.apache.oltu.oauth2.ext.dynamicreg.server.request.OAuthServerRegistrationRequest;
import org.apache.oltu.oauth2.ext.dynamicreg.server.response.OAuthServerRegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/register")
public class STOAuth2RegistrationController {

    @Autowired
    private iClient clientStorage;

    @RequestMapping(consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity register(HttpServletRequest request) throws OAuthSystemException {
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
            client.setRedirectUrl(redirectUrl);
            client.setDescription(description);

            this.clientStorage.insertClient(client);

            OAuthResponse response = OAuthServerRegistrationResponse
                    .status(HttpServletResponse.SC_OK)
                    .setClientId(clientId)
                    .setClientSecret(secret)
                    .setIssuedAt(date.toString())
                    .setExpiresIn(expires)
                    .buildJSONMessage();
            return ResponseEntity.ok(response.getBody());
        } catch (OAuthProblemException ex) {
            ex.printStackTrace();
            return new ResponseEntity("Something went wrong with you", HttpStatus.BAD_REQUEST);
        } catch (OAuthSystemException ex) {
            ex.printStackTrace();
            return new ResponseEntity("Something went wrong with us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String generateClientId() {
        return UUID.randomUUID().toString();
    }

    public String generateClientSecret() {
        return UUID.randomUUID().toString();
    }

    public void setClientStorage(iClient clientStorage) {
        this.clientStorage = clientStorage;
    }
}
