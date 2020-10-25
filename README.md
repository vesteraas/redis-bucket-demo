# redis-bucket-demo

Simple Spring Boot web appliation demonstrating the use of the [RateLimitJ](https://github.com/mokies/ratelimitj)
library, using Redis as a back-end. 

The integration test uses [TestContainers from JUnit 5](https://www.testcontainers.org/test_framework_integration/junit_5)
to start a Redis instance in a Docker container.

If you want to run the application, you should have a Redis server running.  The simplest way of starting one is by
using Docker:

```
docker run --name some-redis -p 6379:6379 -d redis --requirepass ok
```

Then, build and run the application:

```
./gradlew clean build

java -jar build/libs/redis-bucket-demo-1.0.jar
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
Date: Sat, 24 Oct 2020 21:38:45 GMT
```

If you run the same request once more, you should get different output:

```
HTTP/1.1 429 
Content-Length: 0
Date: Sat, 24 Oct 2020 21:39:17 GMT
```