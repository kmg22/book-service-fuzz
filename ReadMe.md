## Env Setting ##
1. git clone Repository
    ```shell
    $ git clone https://github.com/kmg22/book-service-fuzz.git
    $ cd book-service-fuzz
    ```
2. PostgreSQL setting 
    - Open your psql and create Database(bookdb)
        ```shell
        CREATE DATABASE bookdb;
        CREATE USER YOUR_USERNAME WITH PASSWORD 'YOUR_PASSWORD';
        GRANT ALL PRIVILEGES ON DATABASE bookdb TO YOUR_USERNAME;
        ```
    - Edit `src/main/resources/application.yaml`
        ```yaml
        datasource:
            url: jdbc:postgresql://localhost:5432/bookdb
            username: YOUR_USERNAME
            password: YOUR_PASSWORD
            driver-class-name: org.postgresql.Driver
        ```
3. Aladin API Password
    - Get API Key
        https://www.aladin.co.kr/ttb/wblog_manage.aspx
    - edit `src/main/resources/application.yaml`
        ```yaml
        ALADIN_API_KEY: "YOUR_ALADIN_API_KEY"
        ```

## Swagger (API Specification)
1. run `BookServiceApplication.java`
2. get `http://localhost:8080/swagger-ui/index.html`


## Local Fuzz Test (Jazzer) ##
```shell
$ ./gradlew test --tests "com.kmg.BookService.fuzz.BookServiceFuzzTest" "-Djazzer.duration=60"
```