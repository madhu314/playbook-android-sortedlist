package is.uncommon.playbook.sortedlist;

import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;

/**
 * Created by madhu on 29/11/16.
 */
public class TimestampMaker {
  static final AtomicInteger future = new AtomicInteger();
  static final AtomicInteger past = new AtomicInteger();

  static long future() {
    return DateTime.now().plusDays(future.incrementAndGet()).getMillis();
  }
  static long past() {
    return DateTime.now().plusDays(past.decrementAndGet()).getMillis();
  }
  static long current() {
    return DateTime.now().getMillis();
  }
}
