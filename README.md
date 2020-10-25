# hazelcast-bucket-demo

Simple Spring Boot web appliation demonstrating the use of the [Bucket4J](https://github.com/vladimir-bukhtoyarov/bucket4j)
library, using Hazelcast, via the JCache API (JSR 107).

Build and run two instances of the application:

```
./gradlew clean build

java -Dserver.port=8080 -jar build/libs/hazelcast-bucket-demo-1.0.jar
java -Dserver.port=8081 -jar build/libs/hazelcast-bucket-demo-1.0.jar
```

You can use `curl` to thest the application, by issuing the following request:

```
curl --include --location --request POST 'http://127.0.0.1:8080/send' \
--header 'Content-Type: application/json' \
--data-raw '{
    "shortNumber": "20000",
    "from": "555-1212",
    "to": "555-2323",
    "message": "Testing, testing..."
}'

```

The response should be something like this:

```
HTTP/1.1 204 
X-Rate-Limit-Remaining: 0
Date: Sat, 24 Oct 2020 21:38:45 GMT
```

We now run the same request, but on the other instance:

```
curl --include --location --request POST 'http://127.0.0.1:8081/send' \
--header 'Content-Type: application/json' \
--data-raw '{
    "shortNumber": "20000",
    "from": "555-1212",
    "to": "555-2323",
    "message": "Testing, testing..."
}'

```

You should now get different output:

```
HTTP/1.1 429 
Retry-After: 86367
Content-Length: 0
Date: Sat, 24 Oct 2020 21:39:17 GMT
```