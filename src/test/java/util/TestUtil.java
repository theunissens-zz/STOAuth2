package util;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import org.junit.Assert;

import java.util.UUID;

/**
 * Created by stevy on 2017/02/27.
 */
public class TestUtil {

    public static final String TESTCLIENTNAME = "name";
    public static final String TESTCLIENTTYPE = "type";
    public static final String TESTCLIENTURL = "url";
    public static final String TESTCLIENTREDIRECTURL = "redirectUrl";
    public static final String TESTCLIENTDESCRIPTION = "description";
    public static final String TESTCLIENTID = "clientid";
    public static final String TESTCLIENTSECRET = "clientsecret";

    public static final String TESTTOKEN = UUID.randomUUID().toString();
    public static final long TESTTOKENTIMEIN = System.currentTimeMillis();
    public static final String TESTTOKENSCOPE = "scope";
    public static final String TESTTOKENREFRESH = "refreshToken";
    public static final long TESTTOKENEXPIRES = TESTTOKENTIMEIN + 3600;

    public static Client createTestClient() {
        Client client = new Client();
        client.setName(TESTCLIENTNAME);
        client.setType(TESTCLIENTTYPE);
        client.setUrl(TESTCLIENTURL);
        client.setRedirectUrl(TESTCLIENTREDIRECTURL);
        client.setDescription(TESTCLIENTDESCRIPTION);
        client.setClientId(TESTCLIENTID);
        client.setSecret(TESTCLIENTSECRET);
        return client;
    }

    public static Token createTestToken() {
        Token token = new Token();
        token.setAccessToken(TESTTOKEN);
        token.setTimeIn(TESTTOKENTIMEIN);
        token.setScope(TESTTOKENSCOPE);
        token.setRefreshToken(TESTTOKENREFRESH);
        token.setExpiresIn(TESTTOKENEXPIRES);
        return token;
    }
}
