package waltersdemo.metricapi;

import waltersdemo.metricapi.persistence.data.Metric;
import waltersdemo.metricapi.rest.data.RestMetric;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
    public static void assertMetricListsEquals(List<Metric> testMetrics, List<Metric> results) {
        assertEquals(testMetrics.size(), results.size());
        testMetrics.forEach(testMetric -> {
            String testName = testMetric.getName();
            Optional<Metric> optResult = testMetrics.stream()
                    .filter(metric -> testName.equals(metric.getName()))
                    .findFirst();
            assertTrue(optResult.isPresent());
            Metric result = optResult.get();
            List<Double> testDoubles = testMetric.getValues();
            List<Double> resultDoubles = result.getValues();
            for(Double testDouble : testDoubles) {
                assertTrue(resultDoubles.contains(testDouble));
            }
        });
    }

    public static void assertRestMetricListsEquals(List<RestMetric> testMetrics, List<RestMetric> results) {
        assertEquals(testMetrics.size(), results.size());
        testMetrics.forEach(testMetric -> {
            String testName = testMetric.getName();
            Optional<RestMetric> optResult = testMetrics.stream()
                    .filter(metric -> testName.equals(metric.getName()))
                    .findFirst();
            assertTrue(optResult.isPresent());
            RestMetric result = optResult.get();
            List<Double> testDoubles = testMetric.getValues();
            List<Double> resultDoubles = result.getValues();
            for(Double testDouble : testDoubles) {
                assertTrue(resultDoubles.contains(testDouble));
            }
        });
    }

    public static Metric buildTestMetric() {
        Metric metric = new Metric(UUID.randomUUID().toString());
        Random random = new Random();
        for(int i = 0; i < 5; i++) {
            metric.addValue(random.nextDouble());
        }
        return metric;
    }
}
