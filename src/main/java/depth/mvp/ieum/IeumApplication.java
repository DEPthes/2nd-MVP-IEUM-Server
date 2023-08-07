package depth.mvp.ieum;

import depth.mvp.ieum.global.config.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = {"classpath:database/application-database.yml"}, factory = YamlPropertySourceFactory.class)
@PropertySource(value = {"classpath:auth/application-auth.yml"}, factory = YamlPropertySourceFactory.class)
public class IeumApplication {

	public static void main(String[] args) {
		SpringApplication.run(IeumApplication.class, args);
	}

}
