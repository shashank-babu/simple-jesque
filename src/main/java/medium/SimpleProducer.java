package medium;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.client.Client;
import net.greghaines.jesque.client.ClientImpl;
import net.greghaines.jesque.json.ObjectMapperFactory;

import java.util.Random;

@Slf4j
public class SimpleProducer {
    public static void main(String[] args) {
        ObjectMapper objectMapper = ObjectMapperFactory.get();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
        final Config config = JesqueConfig.getConfig();
        final Client client = new ClientImpl(config);
        Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
            Integer id = rand.nextInt(100000);
            log.info("producing id : {}", id);
            client.enqueue("simple", new Job("SimpleWork", new SimpleJob(id)));
        }
        client.end();
    }
}
