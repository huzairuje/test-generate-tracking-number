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
curl --location 'localhost:9090/next-tracking-number?origin_country_id=US&destination_country_id=GB&weight=344.343&created_at=2025-07-22T10%3A00%3A00Z&customer_id=3fa85f64-5717-4562-b3fc-2c963f66afa6&customer_name=Acme%20Corporation%20New&customer_slug=acme-corporation-new'
```

**Example response:**

```json
{
  "tracking_number": "ACMMDE1XVQBMDE1K",
  "created_at": "2025-07-22T10:00:00Z",
  "origin_country_id": "US",
  "destination_country_id": "GB",
  "weight": 344.343,
  "customer_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "customer_name": "Acme Corporation New",
  "customer_slug": "acme-corporation-new"
}
```
