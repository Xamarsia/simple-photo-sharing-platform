
<h1 align="center">
  <p>Simple Photo Sharing Platform (SPSP Backend)</p>
  <h4 align="left">Demo: https://spspdemo.online/</h4>

  This projeect is a backend of [SPSP](https://github.com/Xamarsia/spsp-deployment) project. It implemented using Spring framework. Backend is stateless and implemented as REST API.
</h1>

## Table Of Content

- [Related Projects](#related-projects)
- [Development Stack](#development-stack)
- [Environment Setup](#environment-setup)
- [Build & Run](#build--run)
- [Error Handling](#error-handling)
  - [Error Codes](#error-codes)
- [List Of Endpoints](list-of-endpoints)
  - [Auth Controller](#auth-controller)
  - [User Controller](#user-controller)
  - [Post Controller](#post-controller)
  - [Like Controller](#like-controller)
- [Tests](#tests)
- [Future Enhancements](#future-enhancements)
- [License](#license)

## Related Projects

- [`photo-sharing-platform-frontend`](https://github.com/Xamarsia/photo-sharing-platform-frontend): frontend of the SPSP project.

   It is implemented using Next.js framework and Typescript.

- [`spsp-deployment`](https://github.com/Xamarsia/spsp-deployment): main repository of SPSP project.

## Development Stack

- `Spring` - for quickly build standalone backend application. Spring prowides dependency injection, auto-configuration, security features  and simplifies database integration.
- `Jakarta Validation` - to write constraints on object models via annotations.
- `Hibernate ORM` simplifies database interactions by mapping Java objects to database tables.
- `PostgreSQL` used as main database. Stores posts and users information.
- `Amazon S3` used as image storage (for posts and users).
- `Flyway`  control main database(postgresql) migration scripts.
- `JUnit` is used to write unit tests.
- `Mockito` - mocking framework for unit tests.
- `Docker` - helps create and manage an isolated environment for building, sharing, and running applications.

## Environment Setup

1. Install Visual Studio Code (`ms-vscode-remote.remote-containers` extension).
2. Install Docker Engine  [Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository) and  [Linux post-installation steps for Docker Engine](https://docs.docker.com/engine/install/linux-postinstall/).
3. Docker network `microservice_network` required for further communication with the frontend.

   Execute the following command to create the network if it has not been created previously:

   ```bash
   docker network create microservice_network
   ```

4. Clone project.
5. Setup the environment variables by creating `.env` and `.aws-credentials` files in the root of the project directory with the following content:

   ```ini
   # .env

   POSTGRES_DB="your postgres db name" 
   POSTGRES_USER="your postgres db user name"
   POSTGRES_PASSWORD="your postgres db password"
   PUBLIC_FIREBASE_PROJECT_ID="Part of Firebase setup"
   DOMAIN_NAME="your domain name"
   ```

   Refer to the [Firebase setup](https://github.com/Xamarsia/spsp-deployment/tree/main#setup-firebase) article to understand about the source of the `PUBLIC_FIREBASE_PROJECT_ID` variable.

   ```ini
   # .aws-credentials
   
   AWS_ACCESS_KEY_ID="Access key from IAM Security credentials. Check AWS setup article: https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws"
   AWS_SECRET_ACCESS_KEY="Secret access key from IAM Security credentials. Check AWS setup article: https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws"

   AWS_REGION="region of S3 buckets"
   AWS_S3_POSTS_BUCKET_NAME="AWS S3 posts bucket name"
   AWS_S3_PROFILES_BUCKET_NAME="AWS S3 profiles bucket name"
   ```

   See [`.env.template`](.env.template) & [`.aws-credentials.template`](.aws-credentials.template) files.

## Build & Run

1. Open project in VS Code.
2. [Reopen project in Dev Container](https://code.visualstudio.com/docs/devcontainers/containers).
3. To run project, open any .java file and press `Run Java` button on the top right menu corner. This will trigger the build process and then will run it.
4. Connect to the API using Postman on port 8080: [`http://localhost:8080`](http://localhost:8080).

   URL to connect from another Docker containers that share the same Docker network: [`http://server-api:8080`](http://server-api:8080).

## Error Handling

All exceptions in project are handled by `GlobalExceptionHandler` and returns `ResponseEntity` with `ErrorResponse` in it.

`ErrorResponse` is the special custom type of Error.
It consists of `code` and `message`.

```json
{
  "code": "1008",
  "message": "[SaveUser]: User with username Tom already exist.",
}
```

For example, if a user attempts to register an account with a non-unique username, the HTTP status 500 will be returned along with the corresponding `ErrorResponse` like above.

### Error Codes

Possible custom errors are listed in the following table.

| Error Code | HTTP Status | Description |
| --- | --- | --- |
| 1000 | 404 | Resource not found |
| 1001 | 400 | Method argument validation failed |
| 1002 | 401 | Unauthorized access |
| 1003 | 403 | Access denied |
| 1004 | 500 | Internal server error |
| 1005 | 500 | AWS S3 error |
| 1006 | 429 | Too many requests |
| 1007 | 400 | Constraint violation failed |
| 1008 | 500 | Unique username constraint failed |
| 1009 | 500 | Illegal argument exception |
| 1010 | 500 | Unique auth constraint failed |

## List Of Endpoints

To keep things concise, a brief description of the endpoints is provided.

For detailed documentation on the endpoints, please refer to the specific controller file. You will find Doxygen documentation there.

In this project, there are several controllers that manage all endpoints. Each controller has its own specific responsibilities, and the endpoints are organized accordingly.

### Auth Controller

This controller manages authentication operations endpoints.

It creates, saves, and operates on a unique Auth object, which is based on the authentication generated by the external authentication server. This step is essential before user registration.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/auth/isUsed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/AuthController.java) | It checks if the authentication generated by the authentication server is in use. This authentication must be unique to continue with the registration process. | + |
| POST | [`/auth/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/AuthController.java) | A required step before user registration. It involves creating and saving an `Auth` object based on the authentication generated by the authentication server. | + |

### User Controller

Controller for managing user-related operations endpoints.

It handles endpoints for user authentication, registration, and profile management.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/user/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) |  Retrieve the authenticated user's information. | + |
| GET | [`/user/isUsernameUsed/{username}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Check if a username is already in use. | - |
| GET | [`/user/isRegistered`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Check if the authenticated user is registered.| + |
| GET | [`/user/{username}/profile`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) |  Retrieve the user profile information by username. | + |
| GET | [`/user/{username}/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) |  Retrieve the profile image of a user by username. | - |
| GET | [`/user/{username}/followers`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) |  Retrieve a paginated list of followers for a user by username.| + |
| GET | [`/user/{username}/followings`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) |  Retrieve a paginated list of followings for a user by username. | + |
| GET | [`/user/{postId}/liked`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieve a paginated list of users who liked a specific post. | + |
| GET | [`/user/search`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Search for users based on a query. Users are selected by username or full name. | + |
| POST | [`/user/register`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Register a new user. | + |
| PUT | [`/user/follow/{followerUsername}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Add a follower for the authenticated user. | + |
| PUT | [`/user/deleteFollowing/{followerUsername}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Remove a follower for the authenticated user. | + |
| PUT | [`/user/updateUserInfo`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Update the authenticated user's information. | + |
| PUT | [`/user/updateUsername`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Update the authenticated user's username. | + |
| PUT | [`/user/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Upload a new profile image for the authenticated user. | + |
| DELETE | [`/user/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Delete the profile image of the authenticated user. | + |
| DELETE | [`/user/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Delete the authenticated user account. | + |

### Post Controller

Controller for handling post-related operations endpoints.

It manages the endpoints related to posts, including creating, retrieving, updating, and deleting posts.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/post/{postId}/detailed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) |  Retrieves detailed information about a post by its ID. | + |
| GET | [`/post/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) |  Retrieves a post by its ID. | - |
| GET | [`/post/{postId}/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves the image associated with a post by its ID. | - |
| GET | [`/post/preview/{username}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieve a paginated preview of posts for specific user by username. | - |
| GET | [`/post/newsFeed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieve a paginated news feed of posts. | + |
| POST | [`/post/create`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Creates a new post. | + |
| PUT | [`/post/{postId}/updateImage`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Update the image of an existing post. | + |
| PUT | [`/post/{postId}/updatePostInfo`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Update the information of an existing post by its ID. | + |
| DELETE | [`/post/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Delete a specific post identified by its ID. | + |

### Like Controller

Controller for handling like operations on posts endpoints.

It provides endpoints for users to like and unlike posts.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| POST | [`/like/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/LikeController.java) | Like a post by its ID. | + |
| DELETE | [`/like/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/LikeController.java) | Unlike a post by its ID. | + |

## Tests

Unit tests are implemented for controllers, DTOs and services.

Tests were implemented using JUnit and Mockito mocking framework.

## Future Enhancements

- [ ] Utilizing `slf4j` logging.
- [ ] Adding functionality for comments and tags.
- [ ] Increase test coverage.

## License

Licensed under the MIT License. See [LICENSE](./LICENSE) file for more details.
