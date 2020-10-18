package waltersdemo.metricapi.rest.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricMessage<T> {
    // messageId enables us to log messages and find them by id
    private String messageId;
    private final List<T> messageData = new ArrayList<>();
    private String responseMessage;

    public MetricMessage() {
        messageId = UUID.randomUUID().toString();
    }

    public MetricMessage(String messageId) {
        this.messageId = messageId;
    }

    public MetricMessage(String messageId, List<T> messageData) {
        this.messageId = messageId;
        setmessageData(messageData);
    }

    public List<T> getmessageData() {
        return messageData;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setmessageData(List<T> messageData) {
        this.messageData.clear();
        if(messageData != null) {
            this.messageData.addAll(messageData);
        }
    }

    public void ok() {
        responseMessage = "ok";
    }
}
