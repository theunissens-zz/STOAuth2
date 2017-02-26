package co.za.st.springconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by StevenT on 2017/02/21.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "co.za.st"
})
public class STWebMvcConfig extends WebMvcConfigurerAdapter {
}
