package is.uncommon.playbook.sortedlist;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by madhu on 29/11/16.
 */
public class IdMaker {
  static final AtomicInteger atomicInteger = new AtomicInteger();

  static int next() {
    return atomicInteger.incrementAndGet();
  }
}
