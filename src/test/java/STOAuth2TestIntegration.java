import co.za.st.springconfig.STWebMvcConfig;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by StevenT on 2017/02/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = {STWebMvcConfig.class})
public class STOAuth2TestIntegration {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

//        try {
//            String createDbScript = ResourceUtils.getFile("classpath:scripts/create-oauth2-db.sql").toURI().toString();
//            String createTableScript = ResourceUtils.getFile("classpath:scripts/create-oauth2-db.sql").toURI().toString();
//
//            EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
//                    .setType(EmbeddedDatabaseType.HSQL)
//                    .addScript(createDbScript)
//                    .addScript(createTableScript)
//                    .build();
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        }
    }


    @Test
    public void testRegisterAndGetTokenAndAuthTokenExpectOk()  throws Exception{
        String type = "pull";
        String clientName = "test-app";
        String url = "localhost:8080";
        String description = "example app";
        String redirectUrl = "localhost:8080/callback";
        MvcResult result = this.mockMvc.perform(post("/register")
                .header("Content-Type", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(String.format("{\n" +
                        "    \"type\": \"%s\",\n" +
                        "    \"client_name\": \"%s\",\n" +
                        "    \"client_url\": \"%s\",\n" +
                        "    \"client_description\": \"%s\",\n" +
                        "    \"redirect_url\": \"%s\"\n" +
                        "}", type, clientName, url, description, redirectUrl)))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JSONObject obj = new JSONObject(responseBody);
        String clientId = obj.get("client_id").toString();
        String secret = obj.get("client_secret").toString();

        result = this.mockMvc.perform(post(String.format("/token?redirect_uri=/redirect&grant_type=client_credentials&code=known_authz_code&client_id=%s&client_secret=%s", clientId, secret))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        responseBody = result.getResponse().getContentAsString();
        obj = new JSONObject(responseBody);
        String token = obj.get("access_token").toString();

        this.mockMvc.perform(get(String.format("/auth_resource?access_token=%s", token))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();

    }
}


