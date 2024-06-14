Runs Services in the Core-Library
============

To run the services in the Core-Library, you need to have the following installed:
* Maven
* Java 17
* mvn cli


After you cloned the repo, replace the application.properties with the values:

#### xarani.settings.password= {password} (without quotes)
#### xarani.settings.username= {username} (without quotes)

Then run the following command, at the root of the project to start the application:

```shell
 mvn install:install-file -Dfile=./core-module-1.4-SNAPSHOT.jar -DgroupId=rw.eccelanza -DartifactId=Core-Module -Dversion=1.4-SNAPSHOT -Dpackaging=jar
 mvn spring-boot:run
```
After the service is up and running, you can access the following endpoints:
* http://localhost:8080/id/verify/?{ID-NUMBER} - GET


Using curl:
```shell
curl --location --request GET 'http://localhost:8080/id/verify?id=00-1234567A00ABCD'
```
* For the happy path use Id: 00-1234567A00ABCD
* For the unhappy path use any other numbers and letters


For Minio, you can start a local instance in  ocker with the following command:
```shell

docker run \
   -p 9000:9000 \
   -p 9001:9001 \
   --name minio \
   -v ~/minio/data:/data \
   -e "MINIO_ROOT_USER=minioadmin" \
   -e "MINIO_ROOT_PASSWORD=minioadmin" \
   quay.io/minio/minio server /data --console-address ":9001"
```
with the ui accessible at http://localhost:9001
or connect to a remote instance by changing the values in the application.properties file.

```shell
# Minio Host
spring.minio.url=http://localhost:9000
# Minio Bucket name for your application
spring.minio.bucketName=Attachments
# Minio access key (login)
spring.minio.access-key=minioadmin
# Minio secret key (password)
spring.minio.secret-key=minioadmin
```
NB: The application does not create the bucket, you need to create the bucket manually in the minio UI.

Swagger is available at http://localhost:8080/swagger-ui/index.htm

