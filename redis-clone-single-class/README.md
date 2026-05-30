# Redis Clone Single Class

Java 8 POC for a small Redis-style client/server supporting strings and maps.

The production code is intentionally in one class:

```text
src/main/java/com/example/redisclone/RedisClone.java
```

## Supported Operations

Strings:

- `set`
- `get`
- `remove`
- `append`

Maps:

- `mapSet`
- `mapGet`
- `mapKeys`
- `mapValues`

The class also supports a small text protocol through `client.execute(...)`.

## Example

```java
RedisClone.Server server = RedisClone.server();
RedisClone.Client client = RedisClone.client(server);

client.set("city", "Floripa");
client.append("city", "SC");

client.mapSet("person", "name", "William");
client.mapSet("person", "role", "CTO");
```

## Protocol Examples

```text
SET city Floripa
GET city
APPEND city SC
MAP_SET person name William
MAP_GET person name
MAP_KEYS person
MAP_VALUES person
```

## Test

```bash
mvn test
```
