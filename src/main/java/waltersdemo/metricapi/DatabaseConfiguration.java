package waltersdemo.metricapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import waltersdemo.metricapi.persistence.IMetricRepository;
import waltersdemo.metricapi.persistence.data.Metric;

import java.util.Random;

@Configuration
public class DatabaseConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    CommandLineRunner initDatabase(IMetricRepository repository) {
        return args -> {
            logger.info("Preloading test data");
            for(int i = 0; i < 5; i++) {
                repository.insertMetric(
                        buildTestMetric(String.format("test%s", i)));
            }
        };
    }

    private Metric buildTestMetric(String name) {
        Metric metric = new Metric(name);
        Random r = new Random();
        for(int i = 0; i < 10; i++) {
            metric.addValue(r.nextDouble());
        }

        return metric;
    }
}
