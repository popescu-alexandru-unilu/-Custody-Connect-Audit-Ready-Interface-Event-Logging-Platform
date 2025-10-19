package lu.dekabank.custodyconnect.util;

import java.util.List;
import java.util.stream.Collectors;

public final class CsvUtil {
  private CsvUtil() {}

  public static String toCsv(List<String> header, List<List<Object>> rows) {
    var sb = new StringBuilder();
    sb.append(String.join(",", header.stream().map(CsvUtil::cell).toList())).append("\n");
    for (var r : rows) {
      sb.append(r.stream().map(CsvUtil::cell).collect(Collectors.joining(","))).append("\n");
    }
    return sb.toString();
  }

  public static String cell(Object v) {
    if (v == null) return "\"\"";
    String s = String.valueOf(v).replace("\"", "\"\"");
    return "\"" + s + "\"";
  }
}
