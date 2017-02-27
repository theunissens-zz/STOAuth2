package co.za.st.controller;

import co.za.st.handler.iTokenHandler;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by stevy on 2017/02/27.
 */
@Controller
public class AuthResourceAuthenticator {

    @Autowired
    private iTokenHandler tokenHandler;

    @RequestMapping(value = "/auth_resource", produces = "text/html", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity register(HttpServletRequest request) throws OAuthSystemException {
        try {
            OAuthAccessResourceRequest req = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            String accessToken = req.getAccessToken();
            if (tokenHandler.validateToken(accessToken)) {
                ResponseEntity.ok(accessToken);
            } else {
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm("someResourceServerName")
                        .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                        .buildHeaderMessage();
                return new ResponseEntity(oauthResponse.getBody(), HttpStatus.UNAUTHORIZED);
            }

        } catch(OAuthProblemException ex) {
            return new ResponseEntity("Something went wrong with you", HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}
