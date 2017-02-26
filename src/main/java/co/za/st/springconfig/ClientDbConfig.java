package co.za.st.springconfig;

import co.za.st.client.iClient;
import co.za.st.db.ClientDb;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by stevy on 2017/02/25.
 */
@Configuration
@Profile("production")
public class ClientDbConfig {

    @Bean
    public iClient clientStorage() {
        return new ClientDb();
    }
}
