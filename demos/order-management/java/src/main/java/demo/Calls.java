package demo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Calls {
    public static final List<String> all = new CopyOnWriteArrayList<>();  // lets the tests see what ran
}
