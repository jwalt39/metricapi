package waltersdemo.metricapi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import waltersdemo.metricapi.TestUtil;
import waltersdemo.metricapi.persistence.IMetricRepository;
import waltersdemo.metricapi.persistence.MetricRepository;
import waltersdemo.metricapi.persistence.data.Metric;
import waltersdemo.metricapi.rest.data.AnalyticsResponse;
import waltersdemo.metricapi.rest.data.MetricMessage;
import waltersdemo.metricapi.rest.data.RestMetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricServiceTest {
    @Test
    public void testInsertMetrics() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        RestMetric metric1 = TestUtil.buildTestMetric().buildRestMetric();
        RestMetric metric2 = TestUtil.buildTestMetric().buildRestMetric();
        requestMessage.setmessageData(Arrays.asList(metric1, metric2));

        spy.insertMetrics(requestMessage);

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        Mockito.verify(metricRepository, times(2)).insertMetric(captor.capture());
        List<Metric> values = captor.getAllValues();
        TestUtil.assertMetricListsEquals(requestMessage.getmessageData().stream()
            .map(RestMetric::buildMetric)
            .collect(Collectors.toList()), values);
    }

    @Test
    public void testInsertMetricsFailsMissingName() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        RestMetric metric1 = new RestMetric(null);
        RestMetric metric2 = TestUtil.buildTestMetric().buildRestMetric();
        requestMessage.setmessageData(Arrays.asList(metric1, metric2));

        spy.insertMetrics(requestMessage);

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        Mockito.verify(metricRepository, times(1)).insertMetric(captor.capture());
        List<Metric> values = captor.getAllValues();
        TestUtil.assertMetricListsEquals(Collections.singletonList(metric2.buildMetric()), values);
    }

    @Test
    public void testQueryAll() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        List<Metric> testMetrics = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Metric testMetric = TestUtil.buildTestMetric();
            testMetrics.add(testMetric);
        }
        Mockito.when(metricRepository.queryAll()).thenReturn(testMetrics);
        List<Metric> results = metricService.queryAll(new MetricMessage<>())
            .getmessageData()
            .stream()
            .map(RestMetric::buildMetric)
            .collect(Collectors.toList());
        TestUtil.assertMetricListsEquals(testMetrics, results);
    }

    @Test
    public void testQueryByName() {
        MetricRepository metricRepository = new MetricRepository();
        Metric testMetric = TestUtil.buildTestMetric();
        metricRepository.insertMetric(testMetric);
        MetricService metricService = new MetricService(metricRepository);
        MetricMessage<RestMetric> metricMessage = new MetricMessage<RestMetric>();
        metricMessage.setmessageData(Collections.singletonList(testMetric.buildRestMetric()));
        List<Metric> results = metricService.queryByName(metricMessage)
            .getmessageData()
            .stream()
            .map(RestMetric::buildMetric)
            .collect(Collectors.toList());
        TestUtil.assertMetricListsEquals(Collections.singletonList(testMetric), results);
    }

    @Test
    public void testAddValuesToMetric() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        RestMetric metric1 = TestUtil.buildTestMetric().buildRestMetric();
        RestMetric metric2 = TestUtil.buildTestMetric().buildRestMetric();
        requestMessage.setmessageData(Arrays.asList(metric1, metric2));

        spy.addValuesToMetric(requestMessage);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<Double>> doubleListCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(metricRepository, times(2)).addValuesToMetric(nameCaptor.capture(), doubleListCaptor.capture());
        List<String> names = nameCaptor.getAllValues();
        List<List<Double>> doubleLists = doubleListCaptor.getAllValues();
        assertTrue(names.contains(metric1.getName()));
        assertTrue(names.contains(metric2.getName()));
        assertEquals(2, doubleLists.size());
        List<Double> list1 = doubleLists.get(0);
        List<Double> metric1Values = metric1.getValues();
        list1.forEach(d -> {
            assertTrue(metric1Values.contains(d));
        });
        List<Double> list2 = doubleLists.get(1);
        List<Double> metric2Values = metric2.getValues();
        list2.forEach(d -> {
            assertTrue(metric2Values.contains(d));
        });
    }

    @Test
    public void testAddValuesToMetricFailsMissingMetric() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        RestMetric metric1 = TestUtil.buildTestMetric().buildRestMetric();
        requestMessage.setmessageData(Collections.singletonList(metric1));

        Mockito.when(metricRepository.addValuesToMetric(metric1.getName(), metric1.getValues())).thenReturn(null);
        MetricMessage<RestMetric> response = spy.addValuesToMetric(requestMessage);

        Mockito.verify(metricRepository, times(1)).addValuesToMetric(Mockito.any(), Mockito.any());
        assertEquals(1, response.getmessageData().size());
        RestMetric responseMetric = response.getmessageData().get(0);
        assertEquals(String.format("Error adding values for metric named %s", metric1.getName()), responseMetric.getErrorMessage());
    }

    @Test
    public void testGetMetricAnalytics() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        Metric metric1 = TestUtil.buildTestMetric();
        RestMetric restMetric1 = metric1.buildRestMetric();
        Metric metric2 = TestUtil.buildTestMetric();
        RestMetric restMetric2 = metric2.buildRestMetric();
        requestMessage.setmessageData(Arrays.asList(restMetric1, restMetric2));

        Mockito.when(metricRepository.queryMetricsByName(Arrays.asList(metric1.getName(), metric2.getName()))).thenReturn(Arrays.asList(metric1, metric2));

        spy.getMetricAnalytics(requestMessage);

        ArgumentCaptor<AnalyticsResponse> analyticsResponseCaptor = ArgumentCaptor.forClass(AnalyticsResponse.class);
        ArgumentCaptor<Metric> metricCaptor = ArgumentCaptor.forClass(Metric.class);
        Mockito.verify(spy, times(2)).buildAnalytics(analyticsResponseCaptor.capture(), metricCaptor.capture());

        List<Metric> metricResults = metricCaptor.getAllValues();
        List<AnalyticsResponse> analyticsResponseResults = analyticsResponseCaptor.getAllValues();
        assertEquals(2, metricResults.size());
        assertEquals(2, analyticsResponseResults.size());

        Metric resultMetric1 = metricResults.get(0);
        assertEquals(metric1, resultMetric1);
        AnalyticsResponse analyticsResponse1 = analyticsResponseResults.get(0);
        assertEquals(metric1.getName(), analyticsResponse1.getMetricName());
        Metric resultMetric2 = metricResults.get(1);
        assertEquals(metric2, resultMetric2);
        AnalyticsResponse analyticsResponse2 = analyticsResponseResults.get(1);
        assertEquals(metric2.getName(), analyticsResponse2.getMetricName());
    }

    @Test
    public void testGetMetricAnalyticsFailsMissingMetric() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        MetricService spy = Mockito.spy(metricService);

        MetricMessage<RestMetric> requestMessage = new MetricMessage<>();
        Metric metric1 = TestUtil.buildTestMetric();
        RestMetric restMetric1 = metric1.buildRestMetric();
        requestMessage.setmessageData(Collections.singletonList(restMetric1));

        Mockito.when(metricRepository.queryMetricByName(metric1.getName())).thenReturn(null);

        MetricMessage<AnalyticsResponse> response = spy.getMetricAnalytics(requestMessage);

        Mockito.verify(spy, times(0)).buildAnalytics(Mockito.any(), Mockito.any());
        assertEquals(1, response.getmessageData().size());
        AnalyticsResponse analyticsResponse = response.getmessageData().get(0);
        assertEquals("No metrics found matching request.", analyticsResponse.getErrorMessage());
    }


    @Test
    public void testBuildAnalytics() {
        IMetricRepository metricRepository = Mockito.mock(IMetricRepository.class);
        MetricService metricService = new MetricService(metricRepository);
        Metric testMetric1 = new Metric("test1");
        for(double i = 0.0; i < 11.0; i++) {
            testMetric1.addValue(i);
        }
        AnalyticsResponse response1 = new AnalyticsResponse(testMetric1.getName());
        metricService.buildAnalytics(response1, testMetric1);
        assertEquals(5.0, response1.getMean(), 0);
        assertEquals(0.0, response1.getMinValue(), 0);
        assertEquals(10.0, response1.getMaxValue(), 0);
        assertEquals(5.0, response1.getMedian(), 0);

        Metric testMetric2 = new Metric("test2");
        testMetric2.addValue(0.2);
        testMetric2.addValue(0.3);
        testMetric2.addValue(0.7);
        testMetric2.addValue(18.2);
        testMetric2.addValue(55.1);
        AnalyticsResponse response2 = new AnalyticsResponse(testMetric2.getName());
        metricService.buildAnalytics(response2, testMetric2);
        assertEquals(14.9, response2.getMean(), 0);
        assertEquals(0.2, response2.getMinValue(), 0);
        assertEquals(55.1, response2.getMaxValue(), 0);
        assertEquals(0.7, response2.getMedian(), 0);
    }
}
