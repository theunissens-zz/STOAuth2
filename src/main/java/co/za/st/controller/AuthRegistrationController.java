package co.za.st.controller;

import co.za.st.dto.Client;
import co.za.st.exceptions.ClientExistsException;
import co.za.st.handler.iClientHandler;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by StevenT on 2017/02/21.
 */
@Controller
public class AuthRegistrationController {

    @Autowired
    private iClientHandler clientHandler;

    @RequestMapping(value = "/register", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
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

            Client client = new Client();
            client.setName(name);
            client.setType(type);
            client.setUrl(url);
            client.setRedirectUrl(redirectUrl);
            client.setDescription(description);

            Client generatedClient;

            try {
                generatedClient = this.clientHandler.saveClient(client);
            } catch (ClientExistsException ex) {
                return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
            }

            OAuthResponse response = OAuthServerRegistrationResponse
                    .status(HttpServletResponse.SC_OK)
                    .setClientId(generatedClient.getClientId())
                    .setClientSecret(generatedClient.getSecret())
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
}
