package bg.fmi.vaultmanagerclient.configuration;

import bg.fmi.vaultmanagerclient.component.VaultManagerProvider;
import bg.fmi.vaultmanagerclient.util.VaultManagerUtils;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackageClasses = {VaultManagerProvider.class, VaultManagerUtils.class})
@Configuration
public class VaultManagerConfiguration {
}
