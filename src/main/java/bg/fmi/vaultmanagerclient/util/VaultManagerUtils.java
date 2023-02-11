package bg.fmi.vaultmanagerclient.util;

import bg.fmi.vaultmanagerclient.payload.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VaultManagerUtils {

    public static final String JWT_TOKEN = "jwt-token";
    public static final String API_AUTH_GET_TOKEN = "/api/auth/get-token";

    @Value("${vault.manager.username}")
    private String username;

    @Value("${vault.manager.password}")
    private String password;

    @Value("${vault.manager.api.url}")
    private String url;

    @Value("${vault.manager.auth.token.timeout:0}")
    private Integer tokenTimeout;

    private InMemoryCache inMemoryCache;

    public VaultManagerUtils(@Value("${vault.manager.auth.cache.timeout:0}") Integer cacheTimeout,
                             @Value("${vault.manager.auth.cache.enabled:false}") Boolean cacheEnabled) {
        if (cacheEnabled) {
            inMemoryCache = new InMemoryCache(cacheTimeout);
        }
    }

    public String getJwtToken() {
        String token = null;
        if(inMemoryCache != null) {
            token = (String) inMemoryCache.get(JWT_TOKEN);
        }

        if(token == null) {

            ResponseEntity<String> result =  new RestTemplate().postForEntity(
                    url + API_AUTH_GET_TOKEN,
                    LoginRequest.builder().username(username).password(password).build(),
                    String.class);
            token = result.getBody();
            if(inMemoryCache != null) {
                inMemoryCache.add(JWT_TOKEN, token, tokenTimeout * 1000);
            }
        }

        return token;
    }
}
