package io.mongockito.util.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mongockito")
public class MongockitoConfiguration {

	private String defaultId = "_id";
	private boolean serializeNulls = true;

}
