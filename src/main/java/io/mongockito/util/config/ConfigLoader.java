package io.mongockito.util.config;

import java.io.InputStream;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data
public class ConfigLoader {

	private static final String DEFAULT_CONFIG_FILE_NAME = "default-config.yml";
	private String defaultId;
	private boolean serializeNulls;

	public ConfigLoader() {

		InputStream defaultConfigStream = ConfigLoader.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
		Yaml yaml = new Yaml();
		ConfigLoader defaultConfig = yaml.loadAs(defaultConfigStream, ConfigLoader.class);

		this.defaultId = defaultConfig.getDefaultId();
		this.serializeNulls = defaultConfig.isSerializeNulls();

	}
}
