# Tracking Number Generator API

A RESTful API that generates unique tracking numbers for parcels.

## How to set up, run, and test the application

### Prerequisites

* Java 17
* Maven

### Set up

1. Clone the repository.
2. Open a terminal and navigate to the project root directory.
3. Run `mvn clean install` to build the project and install the dependencies.

### Run

1. Run `mvn spring-boot:run` to start the application.
2. The application will be running at `http://localhost:8080`.

### Test

1. Run `mvn test` to run the unit tests.
2. To test the API, you can use a tool like `curl` or Postman.

**Example request:**

```
curl --location --request GET 'http://localhost:8080/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32+08:00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics'
```

**Example response:**

```json
{
    "tracking_number": "SOME_TRACKING_NUMBER",
    "created_at": "2023-10-27T10:00:00Z"
}
```
