## Task Description
You should be able to start the example application by executing MyApplication, which starts a webserver on port 8080 (http://localhost:8080) and serves SwaggerUI where can inspect and try existing endpoints.

The project is based on a small web service which uses the following technologies:

* Java 1.8
* Spring Boot
* Database H2 (In-Memory)
* Maven


You should be aware of the following conventions while you are working on this exercise:

 * All new entities should have an ID with type of Long and a date_created with type of ZonedDateTime.
 * The architecture of the web service is built with the following components:
 	* DataTransferObjects: Objects which are used for outside communication via the API
    * Controller: Implements the processing logic of the web service, parsing of parameters and validation of in- and outputs.
    * Service: Implements the business logic and handles the access to the DataAccessObjects.
    * DataAccessObjects: Interface for the database. Inserts, updates, deletes and reads objects from the database.
    * DomainObjects: Functional Objects which might be persisted in the database.
 * TestDrivenDevelopment is a good choice, but it's up to you how you are testing your code.
 * Feel free to use Java as well as Kotlin
 * We do provide code formatter for IntelliJ IDEA and Eclipse in the etc folder
---
## Task 1
 * Write a new Controller for maintaining cars (CRUD).
   * Decide on your own how the methods should look like.
   * Entity Car: Should have at least the following characteristics: license_plate, seat_count, convertible, rating, engine_type (electric, gas, ...)
   * Entity Manufacturer: Decide on your own if you will use a new table or just a string column in the car table.
 * Extend the DriverController to enable drivers to select a car they are driving with.
 * Extend the DriverController to enable drivers to deselect a car.
 * Extend the DriverDo to map the selected car to the driver.
 * Add example data to resources/data.sql
---
## Task 2
* First come first serve: A car can be selected by exactly one ONLINE Driver. If a second driver tries to select a already used car you should throw a CarAlreadyInUseException.
---
## Task 3
Imagine a driver management frontend that is used internally by employees to create and edit driver related data. For a new search functionality, we need an endpoint to search for drivers. It should be possible to search for drivers by their attributes (username, online_status) as well as car characteristics (license plate, rating, etc).

* implement a new endpoint for searching or extend an existing one
* driver/car attributes as input parameters
* return list of drivers
---
## Task 4 (optional)

This task is _voluntarily_, if you can't get enough of hacking tech challenges, implement security.
Secure the API so that authentication is needed to access it. The details are up to you. 
Please include instructions how to authenticate/login, so that we can test the endpoints you implemented!

------------------------------------------------------------------------------------------------------------------------------
## HOW TO USE THE APP

The endpoints are secured as described in task 4. Swagger docs are publicly available but if you want to access H2 console, there is a line in SecurityConfig.configure method to uncomment. For securing the endpoints the most basic approach was taken

The tests are in taxi_dispatcher/src/test/java/de/br/aff package.Every endpoint that exists in the app has it's test. That's why the usage of data.sql script is redundant. But in case you still want to access the app "from the outside" you may use it. It's entity ids are negative numbers, so they don't affect integration tests. 

There are also scripts to help with docker and make a calls to some of endpoints:
To start the app you can use docker or do it directly from your IDE. If you want to use docker there is a script ./scripts/docker/create-docker-image.sh to create an image and ./scripts/docker/run-docker-image.sh to run it. 

Once you have the app running, first try ./scripts/rest-requests/get-cars.sh and you'll get Http status 401, proving that this endpoint is secured
To authorise as basic user execute ./scripts/rest-requests/login-as-basic-user.sh - it will log you in as basic user - it creates cookie.txt in scripts folder and that cookie should be used in all subsequent requests
If you execute ./scripts/rest-requests/get-cars.sh now, you'll get cars. ./scripts/rest-requests/get-driver.sh returns a driver with id=-4

For selecting car please use ./scripts/rest-requests/select-car.sh. It assigns a car to the driver with id=-4 so calling again ./scripts/rest-requests/get-driver.sh should show that car are selected. To deselect car, there is a script ./scripts/rest-requests/deselect-car.sh

If you call ./scripts/rest-requests/get-drivers-internal.sh it will return 403 because this is internal endpoint and it can be accessed only by admins. To log in as ADMIN execute ./scripts/rest-requests/login-as-admin.sh
As an admin you can now call ./scripts/rest-requests/get-drivers-internal.sh but also 
./scripts/rest-requests/get-cars.sh and ./scripts/rest-requests/get-driver.sh

Please bare in mind that there are many other endpoints not covered by scripts. For the whole overview it's best to use swagger docs.

------------------------------------------------------------------------------------------------------------------------------
## WHAT COULD HAVE BE DONE BETTER


* inconsistent behaviour for deleting entities (drivers soft, cars hard) -- ( partially valid) but drivers are not
 deleted
, they are
 "detached" from the car. On another hand, deleting car could be done as soft delete so there wouldn't be any
  inconsistencies
  
* evaluation of deleted flags in InternalDriverController (this should be filtered directly in SQL
) -- ( valid) it is
 about
 InternalDriverController.filterDeletedDriversOut method and it should indeed have been done on repository level
 
* duplicate handling of empty optionals received by CarService.find(id) (this could be wrapped in a findChecked(id
) method just like DriverService). We can understand that you were sometimes throwing different exceptions based on
 the context the method being called in. However the underlying problem is always the same being that the callee is
  trying to access a resource that it not "there". This should be handled consistently. -- (completely valid)
  
* search not implemented as requested (multiple endpoints for each individual search criteria) -- ( valid) it's about
 InternalDriverController and having two methods to get a driver by car characteristics. It just doesn't scale well
 . Having one POST controller and providing json with search criteria would be better. It is indeed "restful" to
  fetch data with GET but there are situations
  where getting data with POST is justified. More can be found here https://en
  .wikipedia.org/wiki/POST_(HTTP), looking for a section starting with "There are times when HTTP GET is less
   suitable even for data retrieval". But it is justified in this situation also

* no use of @Transactional - ( not valid) there is indeed no place where @Transactional should be used as there are no
 situations where more state changing operations were required to execute atomically.



* Unnecessary constructors, that are not being used ( It is better to minimize the entry points for instance creation
) for example DriverDTO @NoArgsConstructor, this can redundant when using @JsonCreator annotation -- ( partially
 valid) this is nitpicking but interesting point


* CarDTO.engineType is of type String -> this could be declared of type EngineType and automatically mapped by
 jackson mapper -- ( partially valid) again nitpicking, but it would be nicer this way
 
* JPA query methods could be optimized ( some examples):
1. DefaultCarService.create: car data is not needed so carRepository.findByLicensePlate(…) -> carRepository
.existsByLicensePlate(licensePlate) -- ( valid) it is faster query
2. DefaultDriverService.findByCarRating: can be simplified to -> return driverRepository.findByCarRating(rating)
&& DefaultDriverService.findByLicensePlate: can be simplified to -> return driverRepository.findByCarLicensePlate
(licensePlate) -- ( valid) especially stands for findByCarRating as it would be much faster if done on repository level


