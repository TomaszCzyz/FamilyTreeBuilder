package app.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Configuration singleton class creates a default config file in a user's home directory
 * if it does not exist and provides access to configuration data through out the application.
 * It also provides a method to modify configuration details and persist to file.
 */
public class Configuration {
    private static final Logger log = LogManager.getLogger();

    private Properties properties;
    public Properties getProperties() { return properties; }

    // Singleton
    public static Configuration getInstance() { return instance; }
    private static final Configuration instance = new Configuration();

    private Configuration() {
        log.debug("Loading Configuration");
        File configFile = new File(getConfigPath());

        if (!configFile.exists()) {
            log.info("Config file not found");
            File configFolder = configFile.getParentFile();
            if (!configFolder.exists()) {
                log.info("Config folder not found");
                if (!configFolder.mkdirs()) {
                    throw new IllegalStateException("Couldn't create dir: " + configFolder);
                } else {
                    log.info("Config folder created");
                }
            } else {
                log.debug("Config folder exists");
            }
            log.debug("Writing Default Config");
            updateProperties(DefaultConfig());
        }

        try {
            log.info("Reading Config from " + configFile.toString());
            String config = new String(Files.readAllBytes(configFile.toPath()));
            log.debug(config);
            log.debug("Parsing Config into Json");
            properties = new Gson().fromJson(config, Properties.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Properties DefaultConfig() {
        return new Properties(
                "",
                "",
                "",
                "",
                "",
                ""
        );
    }

    public static void updateProperties() {
        updateProperties(getInstance().getProperties());
    }

    private static void updateProperties(Properties properties) {
        File configFile = new File(getConfigPath());
        log.debug("Attempting to Update Config File: " + configFile.toString());

        try {
            log.debug("Converting Properties to Json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String contents = gson.toJson(properties, Properties.class);
            Path file = Paths.get(configFile.toURI());
            log.info("Writing Config");
            Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
            log.debug("Config Written");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Helper method to construct the Config path in an OS-agnostic way
     *
     * @return Full path of config file
     */
    private static String getConfigPath() {
//        return Utils.combinePath(System.getProperty("user.home"), Arrays.asList(".myApp", "config.json"));
        return Paths.get(System.getProperty("user.home"), ".FamilyTreeBuilder", "config.json").toString();
    }
}