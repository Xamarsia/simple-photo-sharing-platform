<h1 align="center">
  Simple Photo Sharing Platform (SPSP Backend)
</h1>

__Demo: [spspdemo.online](https://spspdemo.online/)__

This repository contains the backend for the [SPSP](https://github.com/Xamarsia/spsp-deployment) project. It is implemented using Spring framework.

## Table Of Content

- [Description](#description)
- [Related Projects](#related-projects)
- [Development Stack](#development-stack)
- [Project Features](#project-features)
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

## Description

The Simple Photo Sharing Platform is a backend of [SPSP](https://github.com/Xamarsia/spsp-deployment) project.

The service is stateless and implemented as a REST API using Java and the Spring framework.

## Related Projects

- [`photo-sharing-platform-frontend`](https://github.com/Xamarsia/photo-sharing-platform-frontend): frontend of the SPSP project.

- [`spsp-deployment`](https://github.com/Xamarsia/spsp-deployment): main repository of the SPSP project.

## Development Stack

- `Spring` - main development framework. Used for dependency injection, auto-configuration, security features and simplified database integration.
- `Jakarta Validation` - to write constraints on object models via annotations.
- `Hibernate ORM` - to simplify database interactions by mapping Java objects to database tables.
- `PostgreSQL` - used as the main database. Stores posts and users information.
- `Amazon S3` - used for image storage (for posts and user profiles).
- `Flyway` - manages database migration scripts for PostgreSQL.
- `JUnit` - to write unit tests.
- `Mockito` - to write mocks for unit tests.
- `Docker` - for isolated development enviroment and deployment.

## Project Features

- __User Authentication:__ Sign In, Sign Up, and Sign Out functionalities, including password reset option.
  - Supports authentication via email and password or external identity provider ( Google ).
- __Unauthorized Preview:__ Non-authenticated users can view a news feed, posts, and other user's profiles.
- __User Profiles:__ Customizable profiles with profile picture, bios and posts.
  - Users can delete their profiles.
- __User Interaction:__ Follow and unfollow functionality.
  - Only authorized users are permitted to follow or unfollow users.
  - All users can view the list of followers or followings.
- __User Search:__ Search for users by username or full name.
- __Content Sharing:__ Intuitive interface for viewing, creating, updating, or deleting posts.
- __Content Interaction:__ Like and dislike features for posts to enhance user engagement.
  - Only authorized users are permitted to like or dislike posts.
  - All users can view the list of users who liked a post.
- __News Feed:__ A news feed of posts is displayed for all users.
- __Security:__
  - Authentication is implemented using OAuth 2.0 ( Firebase Authentication ).
  - Strict validation for user inputs and data integrity.
  - Custom exception handler for error identification and debugging.

## Environment Setup

1. Install Visual Studio Code (`ms-vscode-remote.remote-containers` extension).
2. [Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository) and follow the [Linux post-installation steps for Docker Engine](https://docs.docker.com/engine/install/linux-postinstall/).
3. Clone the project repository.
4. Docker network `microservice_network` required for further communication between the frontend and the backend.

   Execute the following command to create the network if it has not been created previously:

   ```bash
   docker network create microservice_network
   ```

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
   
   AWS_ACCESS_KEY_ID="Access key from IAM Security credentials"
   AWS_SECRET_ACCESS_KEY="Secret access key from IAM Security credentials"

   AWS_REGION="region of S3 buckets"
   AWS_S3_POSTS_BUCKET_NAME="AWS S3 posts bucket name"
   AWS_S3_PROFILES_BUCKET_NAME="AWS S3 profiles bucket name"
   ```

   Refer to the [AWS setup](https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws) article to understand about the source of the `AWS_*` variables.

   See [`.env.template`](.env.template) & [`.aws-credentials.template`](.aws-credentials.template) files.

## Build & Run

1. Open the project in VS Code.
2. [Reopen the project in Dev Container](https://code.visualstudio.com/docs/devcontainers/containers).
3. To run the project, open any .java file and press `Run Java` button in the top right corner. This will trigger the build process and run the application.
4. Connect to the API using Postman on port 8080: [`http://localhost:8080`](http://localhost:8080).

   To connect from another Docker containers that share the same Docker network, use: [`http://server-api:8080`](http://server-api:8080).

## Error Handling

All exceptions in the project are handled by `GlobalExceptionHandler` which returns `ResponseEntity` with `ErrorResponse` in it.

`ErrorResponse` is a custom Error type. It consists of `code` and `message`.

Example of error response:

```json
{
  "code": "1008",
  "message": "[SaveUser]: User with username Tom already exist.",
}
```

For instance, if a user attempts to register with a non-unique username, the HTTP status 500 will be returned along with the corresponding `ErrorResponse` like above.

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

For detailed documentation on the endpoints, please refer to the specific controller file. You will find Doxygen documentation there.

In this project, there are several controllers that manage all endpoints. Each controller has its own specific responsibilities, and the endpoints are organized accordingly.

### Auth Controller

Manages authentication operations endpoints.

It creates, saves, and operates on an unique `Auth` object, which is based on the authentication generated by the external authentication provider. This step is essential before user registration.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/auth/isUsed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/AuthController.java) | Checks if the authentication generated by the authentication provider is in use. This authentication must be unique to continue with the registration process. | + |
| POST | [`/auth/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/AuthController.java) | A required step before user registration. Creates and saves an `Auth` object based on the authentication generated by the authentication provider. | + |

### User Controller

Manages user-related operation endpoints, including user authentication, registration and profile management.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/user/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves the authenticated user's information. | + |
| GET | [`/user/isUsernameUsed/{username}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Checks if a username is already in use. | - |
| GET | [`/user/isRegistered`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Checks if the authenticated user is registered.| + |
| GET | [`/user/{username}/profile`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves the user profile information by username. | + |
| GET | [`/user/{username}/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves the profile image of a user by username. | - |
| GET | [`/user/{username}/followers`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves a paginated list of followers for a user by username.| + |
| GET | [`/user/{username}/followings`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves a paginated list of followings for a user by username. | + |
| GET | [`/user/{postId}/liked`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Retrieves a paginated list of users who liked a specific post. | + |
| GET | [`/user/search`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Searches for users based on a query (username or full name). | + |
| POST | [`/user/register`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Registers a new user. | + |
| PUT | [`/user/follow/{followerUsername}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Adds a follower for the authenticated user. | + |
| PUT | [`/user/deleteFollowing/{followerUsername}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Removes a follower for the authenticated user. | + |
| PUT | [`/user/updateUserInfo`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Updates the authenticated user's information. | + |
| PUT | [`/user/updateUsername`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Updates the authenticated user's username. | + |
| PUT | [`/user/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Uploads a new profile image for the authenticated user. | + |
| DELETE | [`/user/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Deletes the profile image of the authenticated user. | + |
| DELETE | [`/user/`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/UserController.java) | Deletes the authenticated user account. | + |

### Post Controller

Handles endpoints related to posts, including creating, retrieving, updating and deleting.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| GET | [`/post/{postId}/detailed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves detailed information about a post by its ID. | + |
| GET | [`/post/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves a post by its ID. | - |
| GET | [`/post/{postId}/image`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves the image associated with a post by its ID. | - |
| GET | [`/post/preview/{username}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves a paginated preview of posts for specific user by username. | - |
| GET | [`/post/newsFeed`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Retrieves a paginated news feed of posts. | + |
| POST | [`/post/create`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Creates a new post. | + |
| PUT | [`/post/{postId}/updateImage`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Updates the image of an existing post. | + |
| PUT | [`/post/{postId}/updatePostInfo`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Updates the information of an existing post by its ID. | + |
| DELETE | [`/post/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/PostController.java) | Deletes a specific post identified by its ID. | + |

### Like Controller

Handles like operations endpoints on posts.

| Method | URI Template | Action | Authorisation Required |
| --- | --- | --- | :-: |
| POST | [`/like/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/LikeController.java) | Likes a post by its ID. | + |
| DELETE | [`/like/{postId}`](./src/main/java/com/xamarsia/simplephotosharingplatform/controller/LikeController.java) | Unlikes a post by its ID. | + |

## Tests

Unit tests are implemented for controllers, DTOs and services.

Tests were implemented using JUnit and Mockito mocking framework.

## Future Enhancements

- [ ] Utilize `slf4j` logging.
- [ ] Add functionality for comments and hashtags.
- [ ] Increase test coverage.

## License

Licensed under the MIT License. See [LICENSE](./LICENSE) file for more details.
