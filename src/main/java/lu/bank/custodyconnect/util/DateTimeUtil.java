package lu.bank.custodyconnect.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {
  private DateTimeUtil(){}

  public static Instant parseIsoInstant(String s) { return (s==null||s.isBlank())? null : Instant.parse(s); }
  public static String formatIsoInstant(Instant i) { return i==null? null : DateTimeFormatter.ISO_INSTANT.format(i); }
}
