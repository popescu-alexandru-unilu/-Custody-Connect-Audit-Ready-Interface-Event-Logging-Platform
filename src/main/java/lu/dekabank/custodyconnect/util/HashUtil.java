package lu.dekabank.custodyconnect.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class HashUtil {
  private HashUtil(){}
  public static String sha256(String s) {
    try {
      var md = MessageDigest.getInstance("SHA-256");
      byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
      var sb = new StringBuilder();
      for (byte x : b) sb.append(String.format("%02x", x));
      return sb.toString();
    } catch (Exception e) { throw new RuntimeException(e); }
  }
}
