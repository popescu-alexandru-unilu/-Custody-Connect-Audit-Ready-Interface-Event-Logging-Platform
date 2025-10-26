package lu.bank.custodyconnect.util;

import java.util.*;

public final class DiffUtil {
  private DiffUtil(){}

  /** Returns map of key -> [left,right] for differing fields only */
  public static Map<String,List<Object>> diffMaps(Map<String,Object> left, Map<String,Object> right) {
    var keys = new HashSet<String>();
    if (left != null) keys.addAll(left.keySet());
    if (right!= null) keys.addAll(right.keySet());
    var out = new LinkedHashMap<String,List<Object>>();
    for (var k : keys) {
      var l = left==null? null : left.get(k);
      var r = right==null? null : right.get(k);
      if (!Objects.equals(l, r)) out.put(k, List.of(l, r));
    }
    return out;
  }
}
