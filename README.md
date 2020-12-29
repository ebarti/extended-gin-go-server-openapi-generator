# OpenAPI Generator for the extended-gin-go-server library


## Usage

1. Clone repository
2. Build the generator using the provided Dockerfile
3. Use the previously built docker image to generate your server stub


## What I explained above, but in code:

#### Step 1
```shell
git clone https://github.com/ebarti/extended-gin-go-server-openapi-generator.git
cd extended-gin-go-server-openapi-generator
```

#### Step 2
```shell
docker build . -t ext-go-gin-generator
```


#### Step 3
```shell
docker run --rm -v ${PWD}:/local gext-go-gin-generator generate \
  -i /local/mylocalPath/spec/myApiSpec.yaml \
  -c /local/mylocalPath/config/myConfig.yaml \
  -g extended-gin-go-server \
  -o /local/generated
```


### Example of a configuration file

```yaml
additionalProperties:
  hideGenerationTimestamp: true
  packageVersion: "0.0.2"
  serverPort: "8080"
  gitHost: "github.com"
  gitUserId: "ebarti"
  gitRepoId: "myRepo"
  sourceFolder: "go-myrepo-server"
  infoEmail: "me@eloibarti.com"
  infoUrl: "eloibarti.com"
```