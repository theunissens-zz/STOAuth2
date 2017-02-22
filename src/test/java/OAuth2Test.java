import co.za.st.db.OAuth2Db;
import co.za.st.dto.Client;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by StevenT on 2017/02/21.
 */

public class OAuth2Test {

    private MockMvc mockMvc;

    @Test
    public void testClientPersistExpectClientRetrievedEqualToClientPersisted() {
        OAuth2Db db = new OAuth2Db();

        String name = "name";
        String clientId = "clientid";
        String secret = "secret";
        String type = "type";
        String url = "url";
        String redirectUrl = "redirectUrl";
        String description = "description";

        Client client = new Client();
        client.setName(name);
        client.setClientId(clientId);
        client.setSecret(secret);
        client.setType(type);
        client.setUrl(url);
        client.setRedirectUrl(redirectUrl);
        client.setDescription(description);

        db.insertClient(client);

        Client retrievedClient = db.retrieveClient(clientId);

        Assert.assertEquals(name, retrievedClient.getName());
        Assert.assertEquals(clientId, retrievedClient.getClientId());
        Assert.assertEquals(secret, retrievedClient.getSecret());
        Assert.assertEquals(type, retrievedClient.getType());
        Assert.assertEquals(url, retrievedClient.getUrl());
        Assert.assertEquals(redirectUrl, retrievedClient.getRedirectUrl());
        Assert.assertEquals(description, retrievedClient.getDescription());

    }

//    @Test
//    public void testPersist() {
//        mockMvc.perform(get)
//    }
}
