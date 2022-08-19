package bg.fmi.vaultmanagerclient.configuration;

import bg.fmi.vaultmanagerclient.component.VaultManagerProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackageClasses = VaultManagerProvider.class)
@Configuration
public class VaultManagerConfiguration {
}
