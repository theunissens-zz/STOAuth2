import co.za.st.db.iAuthDb;
import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.exceptions.ClientExistsException;
import co.za.st.exceptions.ClientNotFoundException;
import co.za.st.handler.iClientHandler;
import co.za.st.handler.iTokenHandler;
import co.za.st.springconfig.STWebMvcConfig;
import org.json.JSONObject;
import org.junit.Assert;
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
import util.TestUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by stevy on 2017/02/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = {STWebMvcConfig.class})
public class STOAuth2TestUnit {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private iClientHandler clientHandler;

    @Autowired
    private iTokenHandler tokenHandler;

    @Autowired
    private iAuthDb db;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        db.setup();
    }

    @Test
    public void testClientSaveAndGetExpectSuccess() throws ClientExistsException, ClientNotFoundException {
        Client client = TestUtil.createTestClient();

        Client generatedClient = this.clientHandler.saveClient(client);

        Client retrievedClient = this.clientHandler.getClient(generatedClient.getClientId(), generatedClient.getSecret());

        Assert.assertEquals(TestUtil.TESTCLIENTNAME, retrievedClient.getName());
        Assert.assertEquals(TestUtil.TESTCLIENTTYPE, retrievedClient.getType());
        Assert.assertEquals(TestUtil.TESTCLIENTURL, retrievedClient.getUrl());
        Assert.assertEquals(TestUtil.TESTCLIENTREDIRECTURL, retrievedClient.getRedirectUrl());
        Assert.assertEquals(TestUtil.TESTCLIENTDESCRIPTION, retrievedClient.getDescription());
    }

    @Test(expected=ClientExistsException.class)
    public void testClientSaveWithExistingClientAndExpectFailure() throws ClientExistsException, ClientNotFoundException {
        this.clientHandler.saveClient(TestUtil.createTestClient());
        this.clientHandler.saveClient(TestUtil.createTestClient());
    }

    @Test(expected=ClientNotFoundException.class)
    public void testClientGetWithNonSavedClientAndExpectFailure() throws ClientExistsException, ClientNotFoundException {
        this.clientHandler.getClient("randomClientId", "randomClientSecret");
    }

    @Test
    public void testTokenGenerationForClientExpectSuccess() throws ClientNotFoundException, ClientExistsException {
        Client client = TestUtil.createTestClient();

        this.clientHandler.saveClient(client);

        Token token = tokenHandler.generateToken(client, "someScope");
        Assert.assertNotNull(token);
    }

    @Test(expected=ClientNotFoundException.class)
    public void testTokenGenerationForClientNotSavedExpectFailure() throws ClientNotFoundException, ClientExistsException {
        Token token = tokenHandler.generateToken(TestUtil.createTestClient(), "someScope");
        Assert.assertNotNull(token);
    }

    @Test
    public void testValidForClientExpectSuccess() throws ClientNotFoundException, ClientExistsException {
        Client client = TestUtil.createTestClient();

        this.clientHandler.saveClient(client);

        Token token = tokenHandler.generateToken(client, "someScope");

        Assert.assertTrue(tokenHandler.validateToken(token.getAccessToken()));
    }

    @Test
    public void testTokenExpirationForClientExpectFailure() throws ClientNotFoundException, ClientExistsException, InterruptedException {
        Client client = TestUtil.createTestClient();

        this.clientHandler.saveClient(client);

        Token token = tokenHandler.generateToken(client, "someScope");

        Thread.sleep(token.getExpiresIn() + 1000);

        Assert.assertFalse(tokenHandler.validateToken(token.getAccessToken()));
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
