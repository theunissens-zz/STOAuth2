package co.za.st.controller;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.handler.TokenHandler;
import co.za.st.handler.iClientHandler;
import co.za.st.exceptions.ClientNotFoundException;
import co.za.st.handler.iTokenHandler;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by stevy on 2017/02/25.
 */
@Controller
public class STOAuth2TokenController {

    // For now, we will accept the clientid and handler secret in url0 as params, but we really
    // want to base64 encode the clientid and secret like this (clientid:clientsecret) in the future

    @Autowired
    private iClientHandler clientHandler;

    @Autowired
    private iTokenHandler tokenHandler;

    @RequestMapping(value = "/token", consumes = "application/x-www-form-urlencoded", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity token(HttpServletRequest request) {
        try {
            OAuthTokenRequest tokenRequest = new OAuthTokenRequest(request);
            if (tokenRequest.getGrantType().equalsIgnoreCase(GrantType.CLIENT_CREDENTIALS.toString())) {
                String clientid = tokenRequest.getClientId();
                String secret = tokenRequest.getClientSecret();

                try {
                    Client client = clientHandler.getClient(clientid, secret);

                    Token token = tokenHandler.generateToken(client);

                    OAuthResponse response = OAuthASResponse
                            .tokenResponse(HttpServletResponse.SC_OK)
                            .setAccessToken(token.getAccessToken())
                            .setExpiresIn(token.getExpires())
                            .buildJSONMessage();
                    return ResponseEntity.ok(response.getBody());
                } catch (ClientNotFoundException ex) {
                    return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity("Grant type not supported yet", HttpStatus.BAD_REQUEST);
            }
        } catch (OAuthProblemException ex) {
            ex.printStackTrace();
            return new ResponseEntity("Something went wrong with you", HttpStatus.BAD_REQUEST);
        } catch (OAuthSystemException ex) {
            ex.printStackTrace();
            return new ResponseEntity("Something went wrong with us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
