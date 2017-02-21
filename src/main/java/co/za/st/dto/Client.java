package co.za.st.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by StevenT on 2017/02/21.
 */
@Getter
@Setter
public class Client {
    public Client(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    private String clientId;
    private String clientSecret;
}
