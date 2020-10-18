package waltersdemo.metricapi.persistence;

import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Repository;
import waltersdemo.metricapi.persistence.data.Metric;

import java.util.List;

@Repository
public interface IMetricRepository {
    Metric insertMetric(Metric metric);
    List<Metric> queryMetricsByName(List<String> names);
    Metric queryMetricByName(String name);
    List<Metric> queryAll();
    Metric addValuesToMetric(String name, List<Double> values);
    @TestOnly
    void clearDb();
}
