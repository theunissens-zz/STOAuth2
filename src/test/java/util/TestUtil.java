package util;

import co.za.st.dto.Client;

/**
 * Created by stevy on 2017/02/27.
 */
public class TestUtil {

    public static final String TESTCLIENTNAME = "name";
    public static final String TESTCLIENTTYPE = "type";
    public static final String TESTCLIENTURL = "url";
    public static final String TESTCLIENTREDIRECTURL = "redirectUrl";
    public static final String TESTCLIENTDESCRIPTION = "description";

    public static Client createTestClient() {
        Client client = new Client();
        client.setName(TESTCLIENTNAME);
        client.setType(TESTCLIENTTYPE);
        client.setUrl(TESTCLIENTURL);
        client.setRedirectUrl(TESTCLIENTREDIRECTURL);
        client.setDescription(TESTCLIENTDESCRIPTION);
        return client;
    }
}
