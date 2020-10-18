package waltersdemo.metricapi.rest.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import waltersdemo.metricapi.persistence.data.Metric;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestMetric {
    private final String name;
    private final List<Double> values = new ArrayList<>();
    private String errorMessage;

    public RestMetric(String name) {
        this.name = name;
    }

    public RestMetric(String name, List<Double> values) {
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

    public Metric buildMetric() {
        return new Metric(name, values);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
