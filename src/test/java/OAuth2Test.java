import co.za.st.db.OAuth2Db;
import co.za.st.dto.Client;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by StevenT on 2017/02/21.
 */

public class OAuth2Test {

    @Test
    public void testPersist() {
        OAuth2Db db = new OAuth2Db();
        String clientid = "clientid";
        String clientsecret = "clientsecret";
        db.insertClient(new Client(clientid, clientsecret));
        Client client = db.retrieveClient();
        Assert.assertEquals(client.getClientId(), clientid);
        Assert.assertEquals(client.getClientSecret(), clientsecret);
    }
}