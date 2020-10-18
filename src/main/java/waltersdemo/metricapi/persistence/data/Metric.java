package waltersdemo.metricapi.persistence.data;

import waltersdemo.metricapi.rest.data.RestMetric;

import java.util.ArrayList;
import java.util.List;

public class Metric {
    private final String name;
    private final List<Double> values = new ArrayList<>();

    public Metric(String name) {
        this.name = name;
    }

    public Metric(String name, List<Double> values) {
        this.name = name;
        setValues(values);
    }

    public void setValues(List<Double> values) {
        this.values.clear();
        if(values != null) {
            this.values.addAll(values);
        }
    }

    public void addValue(Double value) {
        this.values.add(value);
    }

    public List<Double> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public RestMetric buildRestMetric() {
        return new RestMetric(name, values);
    }

    public void addValues(List<Double> values) {
        this.values.addAll(values);
    }
}
