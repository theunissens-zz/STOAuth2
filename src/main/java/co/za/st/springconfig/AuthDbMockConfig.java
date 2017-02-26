package co.za.st.springconfig;

import co.za.st.db.AuthDbMock;
import co.za.st.handler.ClientHandler;
import co.za.st.db.iAuthDb;
import co.za.st.handler.TokenHandler;
import co.za.st.handler.iClientHandler;
import co.za.st.handler.iTokenHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by stevy on 2017/02/25.
 */
@Configuration
@Profile("test")
@ComponentScan(basePackages = "co.za.st")
public class AuthDbMockConfig {

    @Bean
    public iAuthDb clientDb() {
        return new AuthDbMock();
    }

    @Bean
    public iClientHandler clientHandler() {
        return new ClientHandler();
    }

    @Bean
    public iTokenHandler tokenHandler() {
        return new TokenHandler();
    }
}
