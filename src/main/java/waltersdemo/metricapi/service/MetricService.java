package waltersdemo.metricapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import waltersdemo.metricapi.persistence.IMetricRepository;
import waltersdemo.metricapi.persistence.data.Metric;
import waltersdemo.metricapi.rest.data.AnalyticsResponse;
import waltersdemo.metricapi.rest.data.MetricMessage;
import waltersdemo.metricapi.rest.data.MetricResponse;
import waltersdemo.metricapi.rest.data.RestMetric;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricService {
    private static final Logger logger = LoggerFactory.getLogger(MetricService.class);

    private final IMetricRepository metricRepository;

    public MetricService(IMetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    /**
     * This method queries all the metrics in the service's repository.
     * @param requestMessage The incoming request containing the message id to be returned and logged.
     * @return The rest response containing a list of queried metrics.
     */
    public MetricMessage<RestMetric> queryAll(MetricMessage<?> requestMessage) {
        logger.info(String.format("Processing queryAll for request message %s", requestMessage.getMessageId()));
        List<Metric> results = metricRepository.queryAll();
        MetricMessage<RestMetric> responseMessage = new MetricMessage<>(requestMessage.getMessageId());
        responseMessage.setmessageData(results.stream()
            .map(Metric::buildRestMetric)
            .collect(Collectors.toList()));
        responseMessage.ok();
        return responseMessage;
    }

    /**
     * This method queries a list of metrics by name and returns them in the response.
     * @param requestMessage The incoming request containing the metric data to be queried.
     * @return The rest response containing a list of queried metrics.
     */
    public MetricMessage<RestMetric> queryByName(MetricMessage<RestMetric> requestMessage) {
        logger.info(String.format("Processing queryByName for request message %s", requestMessage.getMessageId()));
        MetricMessage<RestMetric> responseMessage = new MetricMessage<>(requestMessage.getMessageId());
        List<RestMetric> requestMetrics = requestMessage.getmessageData();
        if(requestMetrics.isEmpty()) {
            logger.info(String.format("No request data sent in for queryByName request %s. Aborting search.", requestMessage.getMessageId()));
            responseMessage.setResponseMessage(String.format("No request data sent for request %s.", requestMessage.getMessageId()));
            return responseMessage;
        }

        Map<String, Metric> resultMap = metricRepository.queryMetricsByName(requestMetrics.stream()
            .map(RestMetric::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()))
            .stream()
            .collect(Collectors.toMap(Metric::getName, r->r));

        List<RestMetric> responseData = new ArrayList<>();
        for(RestMetric requestMetric : requestMetrics) {
            String name = requestMetric.getName();
            RestMetric responseMetric = new RestMetric(name);
            if(StringUtils.isEmpty(name)) {
                responseMetric.setErrorMessage("No name specified for request");
                responseData.add(responseMetric);
                continue;
            }

            Metric result = resultMap.get(name);
            if(result == null) {
                responseMetric.setErrorMessage(String.format("Error retrieving metric for name: %s", name));
                responseData.add(responseMetric);
                continue;
            }

            responseData.add(result.buildRestMetric());
        }

        responseMessage.setmessageData(responseData);
        responseMessage.ok();
        return responseMessage;
    }

    /**
     * This method inserts a list of metrics and returns the insertion response.
     * @param requestMessage The incoming request that contains a list of metrics to be inserted.
     * @return The rest response containing a list of the insertion results.
     */
    public MetricMessage<MetricResponse> insertMetrics(MetricMessage<RestMetric> requestMessage) {
        logger.info(String.format("Processing insertMetrics for request message %s", requestMessage.getMessageId()));
        MetricMessage<MetricResponse> responseMessage = new MetricMessage<>(requestMessage.getMessageId());
        List<MetricResponse> responseData = new ArrayList<>();
        List<RestMetric> requestMetrics = requestMessage.getmessageData();
        for(RestMetric restMetric : requestMetrics) {
            responseData.add(insertMetric(restMetric));
        }

        responseMessage.setmessageData(responseData);
        responseMessage.ok();
        return responseMessage;
    }

    /**
     * This method inserts a metric to the service's repository and returns the result.
     * @param restMetric The Metric to be inserted.
     * @return A response detailing the result of the insert.
     */
    public MetricResponse insertMetric(RestMetric restMetric) {
        logger.info(String.format("Attempting to insert restMetric %s", restMetric.getName()));
        if(StringUtils.isEmpty(restMetric.getName())) {
            MetricResponse metricResponse = new MetricResponse("");
            metricResponse.setResponseMessage("Error while performing insertion. No name set for request.");
            return metricResponse;
        }
        Metric metric = restMetric.buildMetric();
        Metric result = metricRepository.insertMetric(metric);
        MetricResponse metricResponse = new MetricResponse(restMetric.getName());
        if(result == null) {
            metricResponse.setResponseMessage(String.format("Error inserting metric %s", restMetric.getName()));
        } else {
            metricResponse.setResponseMessage("Loaded successfully");
        }
        return metricResponse;
    }

    /**
     * This method adds the values in the request RestMetric to the metric matching the name in the
     * associated RestMetric, and returns the updates.
     * @param requestMessage The incoming request containing a list of metrics with values to be added.
     * @return A rest response with the updated values.
     */
    public MetricMessage<RestMetric> addValuesToMetric(MetricMessage<RestMetric> requestMessage) {
        logger.info(String.format("Processing addValuesToMetric for request message %s", requestMessage.getMessageId()));
        MetricMessage<RestMetric> responseMessage = new MetricMessage<>(requestMessage.getMessageId());
        List<RestMetric> requestMetrics = requestMessage.getmessageData();
        if(requestMetrics.isEmpty()) {
            logger.info(String.format("No request data sent in for request %s. Returning empty response.", requestMessage.getMessageId()));
            responseMessage.setResponseMessage("No metric data in request.");
            return responseMessage;
        }

        List<RestMetric> responseMetrics = new ArrayList<>();
        for(RestMetric restMetric : requestMetrics) {
            Metric result = metricRepository.addValuesToMetric(restMetric.getName(), restMetric.getValues());
            if (result == null) {
                RestMetric responseMetric = new RestMetric(restMetric.getName());
                responseMetric.setErrorMessage(String.format("Error adding values for metric named %s", restMetric.getName()));
                responseMetrics.add(responseMetric);
            } else {
                responseMetrics.add(result.buildRestMetric());
            }
        }

        responseMessage.setmessageData(responseMetrics);
        responseMessage.ok();
        return responseMessage;
    }

    /**
     * This method builds a response containing the analytical data for the provided metrics.
     * @param requestMessage The incoming request containing the metrics to be analyzed.
     * @return A rest response MetricMessage containing a list of analytics for the provided metrics.
     */
    public MetricMessage<AnalyticsResponse> getMetricAnalytics(MetricMessage<RestMetric> requestMessage) {
        logger.info(String.format("Processing getMetricAnalytics for request message %s", requestMessage.getMessageId()));
        MetricMessage<AnalyticsResponse> responseMessage = new MetricMessage<>(requestMessage.getMessageId());
        List<RestMetric> requestMetrics = requestMessage.getmessageData();

        Map<String, Metric> resultMap = metricRepository.queryMetricsByName(requestMetrics.stream()
            .map(RestMetric::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()))
            .stream()
            .collect(Collectors.toMap(Metric::getName, r->r));

        List<AnalyticsResponse> analyticsResponses = new ArrayList<>();
        for(RestMetric requestMetric : requestMetrics) {
            AnalyticsResponse analyticsResponse = new AnalyticsResponse(requestMetric.getName());
            Metric result = resultMap.get(requestMetric.getName());
            if(result == null) {
                analyticsResponse.setErrorMessage("No metrics found matching request.");
                analyticsResponses.add(analyticsResponse);
                continue;
            }

            buildAnalytics(analyticsResponse, result);
            analyticsResponses.add(analyticsResponse);
        }

        responseMessage.setmessageData(analyticsResponses);
        responseMessage.ok();
        return responseMessage;
    }

    /**
     * This method is used to build an analytics response based on the provided metric's values.
     * @param analyticsResponse The rest response object that the metric's anayltics will be added to
     * @param metric The metric the analytics will be derived from
     */
    protected void buildAnalytics(AnalyticsResponse analyticsResponse, Metric metric) {
        if(StringUtils.isEmpty(metric.getName())) {
            analyticsResponse.setErrorMessage("Error building analytics. No name set for metric.");
            return;
        }

        logger.info(String.format("Building analytics for metric %s", metric.getName()));
        List<Double> sortedValues = metric.getValues().stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
        if(sortedValues.isEmpty()) {
            analyticsResponse.setErrorMessage(String.format("No values for metric: %s", metric.getName()));
            return;
        }

        OptionalDouble meanOpt = metric.getValues().stream()
            .mapToDouble(v -> v)
            .average();
        meanOpt.ifPresent(analyticsResponse::setMean);

        double median;
        int listSize = sortedValues.size();
        if (listSize % 2 == 0)
            median = (sortedValues.get(listSize/2) + sortedValues.get(listSize/2 - 1))/2;
        else
            median = sortedValues.get(listSize/2);
        analyticsResponse.setMedian(median);

        analyticsResponse.setMinValue(sortedValues.get(0));
        analyticsResponse.setMaxValue(sortedValues.get(sortedValues.size() - 1));
    }
}
