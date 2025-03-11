# Description

[SPSP](https://github.com/Xamarsia/spsp-deployment) backend, implemented using Spring framework. 

Links: 
- Frontend: https://github.com/Xamarsia/photo-sharing-platform-frontend
- Main repository: https://github.com/Xamarsia/spsp-deployment


# Setup

### Create `.env` file in the root of the project with following content (see `.env.template`)
- `POSTGRES_DB` -  your postgres db name.
- `POSTGRES_USER` -  your postgres db user name.
- `POSTGRES_PASSWORD` -  your postgres db password.
- `PUBLIC_FIREBASE_PROJECT_ID` - check [Firebase setup](https://github.com/Xamarsia/spsp-deployment/tree/main#setup-firebase) article. 
- `DOMAIN_NAME`- your domain name.

### Create `.aws-credentials` file in the root of the project (see `.aws-credentials.template`)
- `AWS_ACCESS_KEY_ID` - `Access key` from IAM Security credentials. Check [AWS setup](https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws) article.
- `AWS_SECRET_ACCESS_KEY` - `Secret access key` from IAM Security credentials. Check [AWS setup](https://github.com/Xamarsia/spsp-deployment/tree/main#setup-aws) article.
- `AWS_REGION` - region of S3 buckets.
- `AWS_S3_POSTS_BUCKET_NAME` - AWS S3 posts bucket name.
- `AWS_S3_PROFILES_BUCKET_NAME` - AWS S3 profiles bucket name.


# Run back-end locally 

1. Open project in VS Code.
2. [Reopen project in Dev Container](https://code.visualstudio.com/docs/devcontainers/containers)
3. To run project, open any .java file and press `Run Java` button on the top right menu.
s
