package co.za.st.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.oltu.oauth2.common.token.OAuthToken;

/**
 * Created by stevy on 2017/02/27.
 */
@Getter
@Setter
public class Token implements OAuthToken {

    private String accessToken;
    private String refreshToken;
    private String scope;
    private Long expiresIn;
    private Long timeIn;
}
