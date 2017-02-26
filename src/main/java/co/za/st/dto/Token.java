package co.za.st.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by stevy on 2017/02/26.
 */
@Getter
@Setter
public class Token {

    private String accessToken;
    private String expires;

    public Token(String accessToken, String expires) {
        this.accessToken = accessToken;
        this.expires = expires;
    }
}
