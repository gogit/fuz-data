Proxy requests to other REST APIs by passing the

X-DEST header

Example:

Assume that you have a REST API running on 

http://localhost:8085/api/persist/find?id=1&type=Customer

that returns Json

{
  "id": 1,
  "firstName": "Johnny",
  "lastName": "Doe",
  "address": null
}

To proxy a Call to this REST API

1. Run this microservice 

2. Pass the X-DEST HTTP header

   X-DEST = http://localhost:8085
   
3. Invoke the call on the proxy running on 9090

http://localhost:9090/api/persist/find?id=1&type=Customer
   
The call gets forwarded by the service running on port 9090 to http://localhost:8085  
     
You'll be able to round robin or whatever by changing the

X-DEST header.

## Building it

Clone the repo and build it using

persist$ mvn clean package

## Running the microservice

proxy$ java -jar target/proxy-0.0.1-SNAPSHOT.jar


