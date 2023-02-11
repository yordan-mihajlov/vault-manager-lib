package bg.fmi.vaultmanagerclient.component;

import bg.fmi.vaultmanagerclient.payload.ProjectResponse;
import bg.fmi.vaultmanagerclient.util.InMemoryCache;
import bg.fmi.vaultmanagerclient.util.VaultManagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class VaultManagerProvider {

    public static final String API_PROJECT_GET_CONFIGS = "/api/project/get-configs?projectName=";

    @Value("#{'${vault.manager.projectnames}'.split(',')}")
    private List<String> projectNames;

    @Value("${vault.manager.api.url}")
    private String url;

    @Autowired
    private VaultManagerUtils vaultManagerUtils;

    private InMemoryCache inMemoryCache;

    private final Integer cacheTimeout;

    public VaultManagerProvider(@Value("${vault.manager.config.cache.timeout:0}") Integer cacheTimeout,
                                @Value("${vault.manager.config.cache.enabled:false}") Boolean cacheEnabled) {
        if (cacheEnabled) {
            inMemoryCache = new InMemoryCache(cacheTimeout);
        }
        this.cacheTimeout = cacheTimeout;
    }

    public Map<String, Map<String, String>> getProperties() {
        Map<String, Map<String, String>> projectsProps= new HashMap<>();
        projectNames.forEach(projectName -> {
            Map<String, String> props = null;
            if(inMemoryCache != null) {
                props = (Map) inMemoryCache.get(projectName);
            }

            if(props == null) {
                System.out.println("FROM SERVICE");
                props = new RestTemplate().exchange
                        (url + API_PROJECT_GET_CONFIGS + projectName,
                                HttpMethod.GET, new HttpEntity<>(createAuthHeader()), ProjectResponse.class)
                        .getBody().getConfigurations();
                if(inMemoryCache != null) {
                    inMemoryCache.add(projectName, props, cacheTimeout * 1000);
                }
            }
            projectsProps.put(projectName, props);
        });

        return projectsProps;
    }

    private HttpHeaders createAuthHeader() {
        return new HttpHeaders() {{
            add(HttpHeaders.AUTHORIZATION, vaultManagerUtils.getJwtToken());
        }};
    }
}
