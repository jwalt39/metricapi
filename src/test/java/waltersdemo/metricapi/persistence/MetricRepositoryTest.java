package waltersdemo.metricapi.persistence;

import waltersdemo.metricapi.TestUtil;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import waltersdemo.metricapi.persistence.IMetricRepository;
import waltersdemo.metricapi.persistence.MetricRepository;
import waltersdemo.metricapi.persistence.data.Metric;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricRepositoryTest {
    @Autowired
    private IMetricRepository metricRepository;

    @Before
    public void setup() {
        metricRepository.clearDb();
    }

    @AfterEach
    public void reset() {
        metricRepository.clearDb();
    }

    @Test
    public void testQueryAll() {
        List<Metric> testMetrics  = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Metric testMetric = TestUtil.buildTestMetric();
            metricRepository.insertMetric(testMetric);
            testMetrics.add(testMetric);
        }

        List<Metric> results = metricRepository.queryAll();

        TestUtil.assertMetricListsEquals(testMetrics, results);
    }

    @Test
    public void testAddValuesToMetric() {
        Metric testMetric = new Metric("test");
        testMetric.setValues(Arrays.asList(0.1, 0.2));
        metricRepository.insertMetric(testMetric);
        metricRepository.addValuesToMetric(testMetric.getName(), Arrays.asList(0.5, 0.6));
        Metric result = metricRepository.queryMetricByName(testMetric.getName());
        assertEquals(4, result.getValues().size());
        List<Double> values = result.getValues();
        assertTrue(values.contains(0.1));
        assertTrue(values.contains(0.2));
        assertTrue(values.contains(0.5));
        assertTrue(values.contains(0.6));
    }

    @Test
    public void testInsertMetric() {
        MetricRepository mockRepo = Mockito.mock(MetricRepository.class);
        List<Metric> testMetrics  = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Metric testMetric = TestUtil.buildTestMetric();
            testMetrics.add(testMetric);
        }
        Mockito.when(mockRepo.queryAll()).thenReturn(testMetrics);

        List<Metric> results = mockRepo.queryAll();

        TestUtil.assertMetricListsEquals(testMetrics, results);
    }

    @Test
    public void testQueryMetricsByName() {
        List<Metric> testMetrics  = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Metric testMetric = TestUtil.buildTestMetric();
            metricRepository.insertMetric(testMetric);
            testMetrics.add(testMetric);
        }

        Metric testMetric1 = testMetrics.get(0);
        Metric testMetric2 = testMetrics.get(1);

        List<Metric> results = metricRepository.queryMetricsByName(Arrays.asList(testMetric1.getName(), testMetric2.getName()));
        TestUtil.assertMetricListsEquals(Arrays.asList(testMetric1, testMetric2), results);
    }

    @Test
    public void testQueryMetricByName() {
        Metric testMetric = new Metric("test");
        testMetric.addValue(0.5);
        metricRepository.insertMetric(testMetric);

        Metric result = metricRepository.queryMetricByName(testMetric.getName());
        TestUtil.assertMetricListsEquals(Collections.singletonList(testMetric), Collections.singletonList(result));
    }
}
