## offer-service

### Usage

All commands should be run from the project's root folder

#### Compile

mvn clean compile

#### Run the tests

mvn clean test

#### Package the application

mvn clean package

#### Run the application

mvn exec:java

### Rest API URI

http://localhost:8080/worldpay/

#### Endpoints

Resource | Allowed methods | Example request body (JSON) |
--- | --- | --- |
offers | GET | - |
offers/{id} | GET | - |
offers | POST | { "description":"Offer description", "price":20, "currency":"GBP", "validityPeriodInDays":30 } |
offers/{id} | DELETE | - |