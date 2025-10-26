package lu.bank.custodyconnect.util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public final class JsonUtil {
  private static final Jsonb JSONB = JsonbBuilder.create();
  private JsonUtil() {}
  public static String toJson(Object o) { return JSONB.toJson(o); }
  public static <T> T fromJson(String json, Class<T> type) { return JSONB.fromJson(json, type); }
  public static String pretty(String json) {
    try (var reader = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
      var obj = reader.readValue();
      var writer = jakarta.json.Json.createWriterFactory(java.util.Map.of("jakarta.json.stream.prettyPrinting", true))
                                    .createWriter(new java.io.StringWriter());
      var sw = new java.io.StringWriter();
      jakarta.json.Json.createWriterFactory(java.util.Map.of("jakarta.json.stream.prettyPrinting", true))
        .createWriter(sw).write(obj);
      return sw.toString();
    } catch (Exception e) { return json; }
  }
}
