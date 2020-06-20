package hackit.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("hackit")
@EnableJpaRepositories("hackit.repository")
@Import(DataSourceConfig.class)
public class ApplicationConfig {

}
