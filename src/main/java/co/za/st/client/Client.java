package co.za.st.client;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by StevenT on 2017/02/21.
 */
@Getter
@Setter
public class Client {

    private String name;
    private String clientId;
    private String secret;
    private String description;
    private String url;
    private String redirectUrl;
    private String type;

    public Client() {
    }
}
