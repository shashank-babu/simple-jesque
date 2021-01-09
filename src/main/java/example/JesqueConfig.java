package example;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;

public class JesqueConfig {
    public static Config getConfig() {
        return new ConfigBuilder()
                .withHost("127.0.0.1")
                .withPort(6379)
                .build();
    }
}
