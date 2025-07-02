
<h1 align="center">
  <p>Simple Photo Sharing Platform (Backend)</p>
  <h4 align="left">Demo: https://spspdemo.online/</h4>

  This projeect is a backend of [SPSP](https://github.com/Xamarsia/spsp-deployment) project, implemented using Spring framework. Backend is stateless and implemented as REST API.
</h1>

## Table Of Content

- [Links](#links)
- [Development](#development)
  - [Setup environment](setup-environment)
  - [Setup environment variables](Setup-environment-variables)
  - [Run](Run)
- [Development stack](#development-stack)
  - [Backend](#backend)
  - [General](#general)
- [Tests](#tests)
- [Future Enhancements](#future-enhancements)
- [License](#license)

## Links

- [`photo-sharing-platform-frontend`](https://github.com/Xamarsia/photo-sharing-platform-frontend): frontend of the SPSP project.

It is implemented using Next.js framework and Typescript.

- [`spsp-deployment`](https://github.com/Xamarsia/spsp-deployment): main repository of SPSP project.

## Development

### Setup environment

1. Install Visual Studio Code (`ms-vscode-remote.remote-containers` extension)
2. Install Docker Engine  [Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository) and  [Linux post-installation steps for Docker Engine](https://docs.docker.com/engine/install/linux-postinstall/)  
3. Create docker network:

```bash
docker network create microservice_network
```

`microservice_network` are required for further communication with the frontend.

4. Clone project

### Setup environment variables

Create `.env` file in the root of the project with following content (see `.env.template`)

```ini
# .env

POSTGRES_DB="your postgres db name" 
POSTGRES_USER="your postgres db user name"
POSTGRES_PASSWORD="your postgres db password"
PUBLIC_FIREBASE_PROJECT_ID="Part of Firebase setup" # See https://github.com/Xamarsia/spsp-deployment/tree/main#setup-firebase for more details
DOMAIN_NAME="your domain name"
```

Create `.aws-credentials` file in the root of the project (see `.aws-credentials.template`)

```ini
# .aws-credentials

AWS_ACCESS_KEY_ID="Access key from IAM Security credentials. Check AWS setup article: https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws"
AWS_SECRET_ACCESS_KEY="Secret access key from IAM Security credentials. Check AWS setup article: https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws"

AWS_REGION="region of S3 buckets"
AWS_S3_POSTS_BUCKET_NAME="AWS S3 posts bucket name"
AWS_S3_PROFILES_BUCKET_NAME="AWS S3 profiles bucket name"
```

### Run

1. Open project in VS Code.
2. [Reopen project in Dev Container](https://code.visualstudio.com/docs/devcontainers/containers)
3. To run project, open any .java file and press `Run Java` button on the top right menu.

## Development stack

### Backend

- `Spring` - for quickly build standalone backend application. Spring prowides dependency injection, auto-configuration, security features  and simplifies database integration.
- `Jakarta Validation` - to write constraints on object models via annotations.
- `Hibernate ORM` simplifies database interactions by mapping Java objects to database tables.
- `PostgreSQL` used as main database. Stores posts and users information.
- `Amazon S3` used as image storage (for posts and users).
- `Flyway`  control main database(postgresql) migration scripts.
- `JUnit` is used to write unit tests.
- `Mockito` - mocking framework for unit tests.

### General

- `Docker` - helps create and manage an isolated environment for building, sharing, and running applications.
- `Kubernetes` for deploying and managing containerized applications.
- `Visual Studio Code` provide customizeble development environment.
- `Jira` for project management and task tracking.
- `GitHub` (`Git`) - for code storage, sharing, and management.

## Tests

Unit tests are implemented for controllers, DTOs and services.

Tests were implemented using JUnit and Mockito mocking framework.

## Future Enhancements

- Utilizing `slf4j` logging.
- Adding functionality for comments and tags.
- Increase test coverage.

## License

Licensed under the MIT License. See [LICENSE](./LICENSE) file for more details.

