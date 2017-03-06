import co.za.st.db.iAuthDb;
import co.za.st.dto.Client;
import co.za.st.dto.Token;
import co.za.st.springconfig.STWebMvcConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import util.TestUtil;

/**
 * Created by StevenT on 2017/02/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles(profiles = "integration")
@ContextConfiguration(classes = {STWebMvcConfig.class})
public class STOAuth2TestDb {
    @Autowired
    private iAuthDb db;

    @Before
    public void setup() {
        db.setup();
    }

    @After
    public void teardown() {
        db.dropDb();
    }

    @Test
    public void testPersistClient() {
        Client client = TestUtil.createTestClient();
        db.insertClient(client);

        Assert.assertTrue(db.clientExists(client.getName()));

        Client retrievedClient = db.getClient(client.getClientId(), client.getSecret());

        Assert.assertEquals(TestUtil.TESTCLIENTNAME, retrievedClient.getName());
        Assert.assertEquals(TestUtil.TESTCLIENTTYPE, retrievedClient.getType());
        Assert.assertEquals(TestUtil.TESTCLIENTURL, retrievedClient.getUrl());
        Assert.assertEquals(TestUtil.TESTCLIENTREDIRECTURL, retrievedClient.getRedirectUrl());
        Assert.assertEquals(TestUtil.TESTCLIENTDESCRIPTION, retrievedClient.getDescription());
    }

    @Test
    public void testPersistToken() {
        Client client = TestUtil.createTestClient();
        Token token = TestUtil.createTestToken();
        db.insertClient(client);

        db.insertToken(client, token);
        Assert.assertTrue(db.tokenExists(token.getAccessToken()));
        Token retrievedToken = db.getToken(client.getClientId());

        Assert.assertEquals(TestUtil.TESTTOKEN, retrievedToken.getAccessToken());
        Assert.assertEquals(TestUtil.TESTTOKENSCOPE, retrievedToken.getScope());
        Assert.assertEquals(TestUtil.TESTTOKENEXPIRES, retrievedToken.getExpiresIn().longValue());
        Assert.assertEquals(TestUtil.TESTTOKENTIMEIN, retrievedToken.getTimeIn().longValue());
        Assert.assertEquals(TestUtil.TESTTOKENREFRESH, retrievedToken.getRefreshToken());
    }
}


