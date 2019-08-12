# giraffe

[![gradle-version](https://img.shields.io/badge/gradle-5.5.1-brightgreen)](https://img.shields.io/badge/gradle-5.5.1-brightgreen)

Export twitter lists from twitter account to json formatted file. 

### Usage
clone project and set required properties in giraffe.properties file, and then execute: 

```groovy
gradle run
```

#### elephant.properties

```properties
giraffe-consumer-key=
giraffe-consumer-secret=
giraffe-access-token=
giraffe-access-token-secret=
```

#### example file

```json
[
  {
    "name": "twitter-list",
    "description": "",
    "members": [
      {
        "id": 1,
        "screenName": "a"
      },
      {
        "id": 2,
        "screenName": "b"
      },
      {
        "id": 3,
        "screenName": "c"
      }
    ]
  }
]
```