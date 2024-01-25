# Swift Activities
An activity tracker

## Development Guide
Spawn relevant services with `docker`
```
docker-compose up -d
```

## Config
Create `application.properties` file with required properties. You can get started quickly by copying data from `application.properties.example` 
```
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### Build and Run
```
./gradlew bootRun
```
