package co.za.st.db;

import co.za.st.dto.Client;
import co.za.st.dto.Token;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by stevy on 2017/02/25.
 */
public interface iAuthDb {

    void setup();

    void createAuthDb();

    void createClientTable();

    void createTokenTable();

    void dropDb();

    void insertClient(Client client);

    boolean clientExists(String clientId);

    Client getClient(String clientId, String clientSecret);

    void insertToken(Client client, Token token);

    Token getToken(String clientId);

    void deleteTokens(String[] tokens);

    void purgeTokens();

    boolean tokenExists(String token);
}
