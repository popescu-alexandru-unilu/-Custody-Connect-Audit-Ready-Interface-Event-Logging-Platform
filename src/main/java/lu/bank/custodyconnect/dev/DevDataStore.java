package lu.bank.custodyconnect.dev;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DevDataStore {
  public static class DevEvent {
    public String id, type, status, sourceSystem;
    public Instant eventTimestamp;
    public Map<String,Object> payload;
  }
  public static class DevAudit {
    public String id, eventId, action, level, message;
    public Instant loggedAt;
  }

  private final List<DevEvent> events = new ArrayList<>();
  private final List<DevAudit> audits = new ArrayList<>();

  @PostConstruct
  void init() {
    var types   = List.of("CUSTODY_DEPOSIT_SETTLED","WITHDRAWAL_REQUESTED","WITHDRAWAL_FAILED","SETTLEMENT_CONFIRMED","LEDGER_ADJUSTMENT");
    var sources = List.of("EUROCLEAR","CLEARSTREAM","CUSTODY_APP","OPS_PORTAL");
    var statuses= List.of("processed","processed","pending","failed","processed");
    var rnd = ThreadLocalRandom.current();

    for (int i=1;i<=120;i++){
      var e = new DevEvent();
      e.id = "EVT-%04d".formatted(i);
      e.type = types.get(i % types.size());
      e.status = statuses.get(i % statuses.size());
      e.sourceSystem = sources.get(i % sources.size());
      e.eventTimestamp = Instant.now().minusSeconds(rnd.nextInt(0, 7*24*3600));
      e.payload = Map.of(
        "accountId","ACC-%03d".formatted(1+(i%25)),
        "asset", List.of("ISIN:LU1234567890","BTC","ETH","USDC").get(i%4),
        "txId","TX-%05d".formatted(i),
        "quantity", Math.round(rnd.nextDouble(0.1,5000.0)*100.0)/100.0
      );
      events.add(e);

      var a = new DevAudit();
      a.id = "AUD-%04d".formatted(i);
      a.eventId = e.id;
      a.action = switch (e.status) {
        case "failed" -> "PROCESS_FAILED";
        case "pending" -> "PROCESS_QUEUED";
        default -> "PROCESS_COMPLETED";
      };
      a.level = e.status.equals("failed") ? "ERROR" : "INFO";
      a.message = "%s for %s".formatted(a.action, e.id);
      a.loggedAt = e.eventTimestamp.plusSeconds(60);
      audits.add(a);
    }
    events.sort(Comparator.comparing(ev -> ev.eventTimestamp, Comparator.reverseOrder()));
    audits.sort(Comparator.comparing(au -> au.loggedAt, Comparator.reverseOrder()));
  }

  public Map<String,Object> pageEvents(int page, int size, String status) {
    var stream = events.stream();
    if (status != null && !status.isBlank()) stream = stream.filter(e -> e.status.equalsIgnoreCase(status));
    var list = stream.toList();
    int total = list.size();
    int from = Math.min(page*size, total);
    int to   = Math.min(from+size, total);
    var items = list.subList(from, to).stream().map(e -> Map.of(
      "id", e.id,
      "type", e.type,
      "status", e.status,
      "sourceSystem", e.sourceSystem,
      "eventTimestamp", e.eventTimestamp.toString(),
      "payload", e.payload
    )).collect(Collectors.toList());
    int totalPages = (int)Math.ceil(total/(double)size);
    return Map.of("items", items, "total", total, "page", page, "size", size, "totalPages", totalPages);
  }

  public long count(String status) {
    return (status==null || status.isBlank())
      ? events.size()
      : events.stream().filter(e -> e.status.equalsIgnoreCase(status)).count();
  }

  public List<Map<String,Object>> latestAudits(int max) {
    return audits.stream().limit(Math.max(1, Math.min(100, max))).map(a -> Map.of(
      "id", (Object) a.id, "eventId", (Object) a.eventId, "action", (Object) a.action, "level", (Object) a.level,
      "message", (Object) a.message, "loggedAt", (Object) a.loggedAt.toString()
    )).toList();
  }
}
