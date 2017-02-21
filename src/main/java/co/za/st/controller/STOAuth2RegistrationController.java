package co.za.st.controller;

import co.za.st.db.OAuth2Db;
import co.za.st.dto.Client;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by StevenT on 2017/02/21.
 */
@Controller
public class STOAuth2RegistrationController {

    @RequestMapping(value = "/register/", produces = "application/json", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> showIndex(@RequestParam("clientid") String clientId,
                                            @RequestParam("clientsecret") String clientSecret) {
        System.out.println("ClientId: " + clientId + " Client Secret: " + clientSecret);

        OAuth2Db oAuth2Db = new OAuth2Db();
        oAuth2Db.insertClient(new Client(clientId, clientSecret));

        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
