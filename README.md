# teamcity-automation

An automation script made in Kotlin that automates the process of:
- Creating a new project
- Setting up a build configuration
- Executing the build configuration
- Asserting that the build execution is successful

In JetBrains' TeamCity CI/CD tool, using their REST API.

### Running the script
In order to run the script one must have these prerequisites:
- TeamCity server up and running
- One available TeamCity Agent capable of running Maven

Fist, generate the API key through the platform using the TeamCity UI by going to Profile > Access keys

Then, create a `config.json` file inside project root, which contains a JSON object with two properties (you can see `config-sample.json` as an example):
- `API_KEY`: Access Token generated through TeamCity
- `BASE_URL`: Base URL for REST API of TeamCity Server (ex. http://localhost:8111/app/rest/)

Finally, you can run the test suite located inside src/test/kotlin/TeamCityTest.kt

The test suite automatically checks for the build status upon completion and asserts that it is successful.

_https://github.com/mkjetbrains/SimpleMavenSample repo is used as a testing one for the build_