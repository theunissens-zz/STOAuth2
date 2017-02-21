package co.za.st.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by StevenT on 2017/02/21.
 */
@Getter
@Setter
public class Client {
    public Client(String clientName, String clientSecret) {
        this.clientName = clientName;
        this.clientSecret = clientSecret;
    }
    private String type;
    private String clientName;
    private String clientUrl;
    private String clientDescription;
    private String client;
    private String clientSecret;
}
