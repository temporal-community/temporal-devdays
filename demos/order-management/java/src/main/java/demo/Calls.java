package demo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Calls {
    public static final List<String> all = new CopyOnWriteArrayList<>();  // lets the tests see what ran
    public static final Set<String> charged = ConcurrentHashMap.newKeySet();  // orders whose card was charged, so the refund can tell
}
