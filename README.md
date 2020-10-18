# API Description

## Compile Instructions
mvn clean package

This will generate a runnable 16.36mb spring-boot jar in the ./targets folder. 

## Run Instructions
mvn spring-boot:run

## API Endpoints

* **GET /metrics**

Returns all existing metrics in the repository.

*Time Complexity : O(1)*

* **GET /metrics/search**

Returns all metrics matching the names specified in the request

*Time Complexity : O(1)*

### Example Request
```json
{
    "messageId" : "test",
    "messageData" : [
        {
            "name" : "test1"
        },
        {
            "name" : "test4"
        }
    ]
}
```
This example returns the metric data of metrics test1 and test4 if they exist in the database.

* **GET /metrics/analytics**

Returns analytics for all metrics matching the names specified in the request

*Time Complexity : O(n)*

### Example Request
```json
{
    "messageId" : "test",
    "messageData" : [
        {
            "name" : "test1"
        },
        {
            "name" : "test4"
        }
    ]
}
```
This example returns the analytics for metrics test1 and test4 if they exist in the database.

* **POST /metrics**

Adds the values in the request RestMetric to the metric matching the name in the associated RestMetric,
and returns the updates.

*Time Complexity : O(n)*

### Example Request
```json
{
    "messageId" : "test",
    "messageData" : [
        {
            "name" : "test1",
            "values" : [
                0.5,
                0.2
            ]
        },
        {
            "name" : "test2",
            "values" : [
                0.6,
                0.3
            ]
        }
    ]
}
```
This example adds the values 0.5, 0.2 to metric test1 and 0.6, 0.3 to metric test2 if they exist in the database.

* **PUT /metrics**

Inserts a list of metrics and returns the insertion response.

*Time Complexity : O(n)*

### Example Request
```json
{
    "messageId" : "test",
    "messageData" : [
        {
            "name" : "test7",
            "values" : [
                0.5,
                0.2
            ]
        },
        {
            "name" : "test6",
            "values" : [
                0.6,
                0.3
            ]
        }
    ]
}
```
This example inserts metrics test7 and test6 with their defined values to the database.