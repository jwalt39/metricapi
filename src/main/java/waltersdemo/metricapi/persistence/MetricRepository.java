package waltersdemo.metricapi.persistence;

import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import waltersdemo.metricapi.persistence.data.Metric;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MetricRepository implements IMetricRepository {
    public final Map<String, Metric> metricMap = new HashMap<>(); // Metrics mapped by name

    public MetricRepository() {

    }

    @Override
    public List<Metric> queryAll() {
        return new ArrayList<>(metricMap.values());
    }

    @Override
    public Metric addValuesToMetric(String name, List<Double> values) {
        if(!metricMap.containsKey(name)){
            return null;
        }

        Metric metric = metricMap.get(name);
        metric.addValues(values);
        metricMap.put(metric.getName(), metric);
        return metric;
    }

    @Override
    public Metric insertMetric(Metric metric) {
        String metricName = metric.getName();
        if(StringUtils.isEmpty(metricName) || metricMap.containsKey(metricName)) {
            return null;
        }

        metricMap.put(metric.getName(), metric);
        return metric;
    }

    @Override
    public List<Metric> queryMetricsByName(List<String> names) {
        return names.stream()
            .map(metricMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public Metric queryMetricByName(String name) {
        return metricMap.getOrDefault(name, null);
    }

    @TestOnly
    public void clearDb() {
        metricMap.clear();
    }
}
