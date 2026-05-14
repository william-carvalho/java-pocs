# Render Template Single Class

Java 8 POC where the same template can be rendered as HTML, PDF, or CSV.

The production code is intentionally in one class:

```text
src/main/java/com/example/rendertemplate/RenderTemplate.java
```

## Example

```java
RenderTemplate renderer = RenderTemplate.builder()
        .template("invoice", "Invoice {{number}} for {{customer}} total {{total}}")
        .build();

Map<String, Object> data = new LinkedHashMap<>();
data.put("number", "INV-1");
data.put("customer", "Ana");
data.put("total", "99.90");

String html = renderer.render("invoice", data, RenderTemplate.Format.HTML).asText();
String csv = renderer.render("invoice", data, RenderTemplate.Format.CSV).asText();
byte[] pdf = renderer.render("invoice", data, RenderTemplate.Format.PDF).asBytes();
```

## Test

```bash
mvn test
```
