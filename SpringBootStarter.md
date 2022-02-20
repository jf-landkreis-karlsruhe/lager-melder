# Basic Spring Boot

- spring-boot-starter-web
- spring-boot-starter-data-jpa

## Spring Boot Folder Strukur

This is my idea for a folder structure for spring boot applications. It can change and is maybe not optimal.

src/main/java/<package>
_ core (Here is the logic of the program)
_ model
_ persistance (Everything depending to the database)
_ entity (objects which are stored in the database)
_ repositories (Functions to crud data on the database)
_ service (Receives core entities converts them to database entities and calls the repository methods. Responses from the repository methods are database entities which are converted to core entities and send to the controller.)
_ exceptions
_ rest (This is the folder for everything that can be used on the rest api. It is a abstraction of the core logic)
_ controller (Handles the request and is called wtih the Api entities. Translates the Api entities to core entities and calls the core services. Converts the core entities from the core service to api entities and returns them to the rest api.)
_ exceptionHandler (exceptions to HTTP Status code and message)
_ model (Objects with are served by the rest api)
_ security \* config

### How a request is processed

| controller                             | service                                           | persistance                             |
| -------------------------------------- | ------------------------------------------------- | --------------------------------------- |
| -> `api entity`                        |                                                   |                                         |
| converts `api entity` to `core entity` |                                                   |                                         |
| calls service with `core entity` ->    |                                                   |                                         |
|                                        | -> `core entity`                                  |                                         |
|                                        | validation checks ect.                            |                                         |
|                                        | converts `core entity` to `database entity`       |                                         |
|                                        | calls repository method with `database entity` -> |                                         |
|                                        |                                                   | -> `database entity`                    |
|                                        |                                                   | crud on database                        |
|                                        |                                                   | <- returns `database entity` to service |
|                                        | `database entity` <-                              |                                         |
|                                        | validation checks ect.                            |                                         |
|                                        | <- returns `core entity` to controller            |                                         |
|                                        | converts `database entity` to `core entity`       |                                         |
| `core entity` <-                       |                                                   |                                         |
| converts `core entity` to `api entity` |                                                   |                                         |
| <- `api entity`                        |                                                   |                                         |
