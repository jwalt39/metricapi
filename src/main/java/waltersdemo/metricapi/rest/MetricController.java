package waltersdemo.metricapi.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import waltersdemo.metricapi.rest.data.AnalyticsResponse;
import waltersdemo.metricapi.rest.data.MetricMessage;
import waltersdemo.metricapi.rest.data.MetricResponse;
import waltersdemo.metricapi.rest.data.RestMetric;
import waltersdemo.metricapi.service.MetricService;


@RestController
@RequestMapping("/metric")
public class MetricController {
    @Autowired
    private MetricService metricService;

    /**
     * This method queries all the metrics in the service's repository.
     * @return The rest response containing a list of queried metrics.
     */
    @GetMapping
    public MetricMessage<RestMetric> getMetrics() {
        return metricService.queryAll(new MetricMessage<>());
    }

    /**
     * This method queries a list of metrics by name and returns them in the response.
     * @param requestMessage The incoming request containing the metric data to be queried.
     * @return The rest response containing a list of queried metrics.
     */
    @GetMapping(value = "/search")
    public MetricMessage<RestMetric> searchMetrics(@RequestBody MetricMessage<RestMetric> requestMessage) {
        return metricService.queryByName(requestMessage);
    }

    /**
     * This method builds a response containing the analytical data for the provided metrics.
     * @param requestMessage The incoming request containing the metrics to be analyzed.
     * @return A rest response MetricMessage containing a list of analytics for the provided metrics.
     */
    @GetMapping(value = "/analytics")
    public MetricMessage<AnalyticsResponse> getMetricAnalytics(@RequestBody MetricMessage<RestMetric> requestMessage) {
        return metricService.getMetricAnalytics(requestMessage);
    }

    /**
     * This method adds the values in the request RestMetric to the metric matching the name in the
     * associated RestMetric, and returns the updates.
     * @param requestMessage The incoming request containing a list of metrics with values to be added.
     * @return A rest response with the updated values.
     */
    @PostMapping
    public MetricMessage<RestMetric> addValuesToMetrics(@RequestBody MetricMessage<RestMetric> requestMessage) {
        return metricService.addValuesToMetric(requestMessage);
    }

    /**
     * This method inserts a list of metrics and returns the insertion response.
     * @param requestMessage The incoming request that contains a list of metrics to be inserted.
     * @return The rest response containing a list of the insertion results.
     */
    @PutMapping
    public MetricMessage<MetricResponse> insertMetrics(@RequestBody MetricMessage<RestMetric> requestMessage) {
        return metricService.insertMetrics(requestMessage);
    }
}
