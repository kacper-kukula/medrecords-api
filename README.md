# Med Records API

This project provides a seamless experience for managing medical records, including user authentication and save/read operations on drug records in the database.

The application is built using the MVC architecture and integrates with external FDA API.
## Technologies and Tools

<p align="center">
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" alt="Java" title="Java"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207242-07d5a700-adf4-11eb-975e-be04e62b984b.png" alt="Maven" title="Maven"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183891303-41f257f8-6b3d-487c-aa56-c497b880d0fb.png" alt="Spring Boot" title="Spring Boot"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192107858-fe19f043-c502-4009-8c47-476fc89718ad.png" alt="REST" title="REST"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" alt="JUnit" title="JUnit"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183892181-ad32b69e-3603-418c-b8e7-99e976c2a784.png" alt="mockito" title="mockito"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/182884177-d48a8579-2cd0-447a-b9a6-ffc7cb02560e.png" alt="MongoDB" title="MongoDB"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207330-263ba280-adf4-11eb-9b97-0ac5b40bc3be.png" alt="Docker" title="Docker"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/186711335-a3729606-5a78-4496-9a36-06efcc74f800.png" alt="Swagger" title="Swagger"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108372-f71d70ac-7ae6-4c0d-8395-51d8870c2ef0.png" alt="Git" title="Git"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108890-200809d1-439c-4e23-90d3-b090cf9a4eea.png" alt="IntelliJ" title="IntelliJ"/></code>
</p>
<br />

- **As well as**: JWT, Jackson, Lombok, MapStruct, Spring Security, SLF4J, Embedded Mongo, JaCoCo

## Unique Functionalities

### Authentication Controller

- **[public] ```POST /api/v1/auth/registration```**: Allows new users to register securely.
- **[public] ```POST /api/v1/auth/login```**: Enables existing users to authenticate securely using JWT.

### Drug Record Controller

- **[user] `GET /api/v1/drug-records/search`**: Search for drug records based on manufacturer and/or brand name.
- **[user] `GET /api/v1/drug-records/save/{applicationNumber}`**: Save a drug record by its application number (fetched from FDA).
- **[user] `GET /api/v1/drug-records`**: Retrieve all stored drug records with pagination support.
- **[user] `GET /api/v1/drug-records/{applicationNumber}`**: Fetch a stored drug record by its application number.



## Test Coverage
<p align="center">
<img src="https://i.imgur.com/vI1X9sF.png" alt="Coverage"/>
  
- 87% Test Coverage (JaCoCo)
</p>

## Getting Started (Windows)

1. Make sure to install [Maven](https://maven.apache.org/download.cgi), [Docker](https://www.docker.com/products/docker-desktop/), [JDK 17+](https://www.oracle.com/pl/java/technologies/downloads/), [MongoDB](https://www.mongodb.com/try/download/community)
2. Clone the repository.
3. Configure the `.env` file with your database credentials and ports and add it to root project directory. Working example:
```
MONGODB_LOCAL_PORT=27017
MONGODB_DOCKER_PORT=27017
MONGODB_ROOT_USERNAME=admin
MONGODB_ROOT_PASSWORD=admin
MONGODB_DATABASE=medrecords

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005

JWT_SECRET_STRING=superLong12345AndStrong12345SecretString
```
(optionally you can obtain API Key from [FDA](https://open.fda.gov/apis/authentication/) offical site and put it in the above `.env` file as `FDA_API_KEY=yourkeyhere`. It will let you do significantly more requests per day, but will work totally fine without it.)

4. We need to build the project using Maven first. It is essential to set up the environmental variable for `JWT_SECRET_STRING` as it's used for tests to succesfully build the project. You can do that on PowerShell with following command (ensure you're in the project root directory): `$env:JWT_SECRET_STRING = "your_secret_value"` and then `mvn clean package` or on Unix based shells just simply put it in one command: `JWT_SECRET_STRING=superLong12345AndStrong12345SecretString mvn clean package`. The string is only used for tests, later you can use your own in the .env file.
4. Ensure Docker Desktop is running.
5. Ensure MongoDB is running.
6. Build and run the application using Docker: `docker-compose up --build` (must be in root project directory).
7. Access the API documentation at Swagger UI: `http://localhost:8080/api/swagger-ui/index.html#/`.

If you want to run the tests in your IDE, running `mvn clean test` or `mvn clean package` might produce error if your `JWT_SECRET_STRING` environmental variable isn't set. Set it on your own preference or simply attach it in front of the `mvn clean package` command: `JWT_SECRET_STRING=superLong12345AndStrong12345SecretString mvn clean package`.

You can now access the endpoints using `Swagger` or `Postman`. To access the functionality, you must first register, and you will be granted `User` role.

After logging in, you receive a `Bearer Token` which you must then provide as authorization to access the endpoints. Another approach on Swagger UI and Postman would be to simply enter your email and password in `Basic Auth` Auth Type.
