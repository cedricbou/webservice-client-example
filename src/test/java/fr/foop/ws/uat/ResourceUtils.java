package fr.foop.ws.uat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.common.base.Charsets;

public class ResourceUtils {

	public static String content(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(ResourceUtils.class
					.getClassLoader().getResource(path).toURI())),
					Charsets.UTF_8);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static Properties props(String path) {
		try {
			final Properties props = new Properties();
			props.load(ResourceUtils.class.getClassLoader()
					.getResourceAsStream(path));
			return props;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
