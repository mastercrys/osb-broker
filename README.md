# Cloud Provisioner Microservice

## Overview

This microservice implements the Open Service Broker (OSB) API version 2.15 for managing AWS EC2 instances. It provides endpoints for provisioning, deprovisioning, and managing the lifecycle of EC2 instances.

I choose to not implement the async part of the osb api specification because i think it must be implemented with a strong and solid queue management (example Postgres + RabbitMQ) due to the polling operations api so this will take more than the 10 hours of the test but at this state of the microservices it could be added.

## Configuration

Update `src/main/resources/application.properties` with your AWS credentials and region:
```properties
aws.accessKeyId=YOUR_ACCESS_KEY_ID
aws.secretAccessKey=YOUR_SECRET_ACCESS_KEY
aws.region=eu-west-1
broker.tests.volumeId=YOUR_AWS_VOLUME_ID_TO_TEST

quarkus.jackson.fail-on-empty-beans=false
quarkus.http.auth.basic=true
quarkus.security.users.embedded.enabled=true
quarkus.security.users.embedded.plain-text=true
quarkus.security.users.embedded.users.admin=password
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Packaging and running the application using Docker

Before building the container image run:

```shell script
./mvnw package
```

Then, build the image with:

```shell script
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/osb-broker-jvm .
```

Then run the container using:

```shell script
docker run -i --rm -p 8080:8080 quarkus/osb-broker-jvm
```

More details can be found in the src/main/docker folder.

## Application Test

The application Test Class create an ec2 instance, bind and unbind the volume and then terminate the instance. 

It may take some minutes, in this case remember to add the -DskipTests parameter

## Call endpoints

Always include Basic Authentication based on `quarkus.security.users.embedded.users.admin=password` (in this case admin is the username and password is the password)

Create EC2 Instance

```shell script
curl -X PUT \
  'http://localhost:8080/v2/service_instances/i-06fefe56a8abd9195?accepts_incomplete=false' \
  --header 'Accept: */*' \
  --header 'X-Broker-API-Version: 2.17' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "service_id": "service-offering-id-here",
  "plan_id": "service-plan-id-here",
  "context": {
    "platform": "myplatform"
  },
  "organization_guid": "org-guid-here",
  "space_guid": "space-guid-here",
  "parameters": {
    "amiId": "ami-068d1303a1458fb15",
    "instanceType": "t2.micro",
    "minCount": 1,
    "maxCount": 1
  },
  "maintenance_info": {
    "version": "2.1.1+abcdef"
  }
}'
```

Bind a volume

```shell script
curl  -X PUT \
  'http://localhost:8080/v2/service_instances/i-06fefe56a8abd9195/service_bindings/12345?accepts_incomplete=false' \
  --header 'Accept: */*' \
  --header 'X-Broker-API-Version: 2.17' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "context": {
    "platform": "myplatform"
  },
  "service_id": "service-offering-id-here",
  "plan_id": "service-plan-id-here",
  "bind_resource": {
    "app_guid": "app-guid-here"
  },
  "parameters": {
    "volumeId": "vol-04b9fd236a8b96759",
    "deviceName": "/dev/sdf"
  }
}'
```

Unbind a volume

```shell script
curl  -X DELETE \
  'http://localhost:8080/v2/service_instances/i-06fefe56a8abd9195/service_bindings/vol-04b9fd236a8b96759?service_id=service-id&plan_id=plan-id&accepts_incomplete=false' \
  --header 'Accept: */*' \
  --header 'X-Broker-API-Version: 2.17'
```

Start, Stop, Reboot instance

Edit parameters.operation according to the required action: start, stop, reboot

```shell script
curl  -X PATCH \
  'http://localhost:8080/v2/service_instances/i-06fefe56a8abd9195?accepts_incomplete=false' \
  --header 'Accept: */*' \
  --header 'X-Broker-API-Version: 2.17' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "context": {
    "platform": "cloudfoundry",
    "some_field": "some-contextual-data"
  },
  "service_id": "service-offering-id-here",
  "plan_id": "service-plan-id-here",
  "parameters": {
    "operation": "start"
  },
  "previous_values": {
    "plan_id": "old-service-plan-id-here",
    "service_id": "service-offering-id-here",
    "organization_id": "org-guid-here",
    "space_id": "space-guid-here",
    "maintenance_info": {
      "version": "2.1.1+abcdef"
    }
  },
  "maintenance_info": {
    "version": "2.1.1+abcdef"
  }
}'
```

Deprovision Instance

```shell script
curl  -X DELETE \
  'http://localhost:8080/v2/service_instances/i-06fefe56a8abd9195?service_id=service-id&plan_id=plan-id&accepts_incomplete=false' \
  --header 'Accept: */*' \
  --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
  --header 'X-Broker-API-Version: 2.17'
```
