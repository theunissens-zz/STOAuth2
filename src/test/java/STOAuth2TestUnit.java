import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.handler.iClientHandler;
import co.za.st.exceptions.ClientExistsException;
import co.za.st.exceptions.ClientNotFoundException;
import co.za.st.handler.iTokenHandler;
import co.za.st.springconfig.STWebMvcConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import util.TestUtil;

/**
 * Created by stevy on 2017/02/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = {STWebMvcConfig.class})
public class STOAuth2TestUnit {

    @Autowired
    private iClientHandler clientHandler;

    @Autowired
    private iTokenHandler tokenHandler;

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
        Client client = TestUtil.createTestClient();

        this.clientHandler.saveClient(client);
        this.clientHandler.saveClient(client);
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
}
