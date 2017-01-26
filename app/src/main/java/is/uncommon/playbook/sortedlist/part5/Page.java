package is.uncommon.playbook.sortedlist.part5;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class Page implements Parcelable {
  private static final int SIZE = 10;

  public static Page start() {
    return new AutoValue_Page(0, SIZE);
  }

  public abstract int number();

  public abstract int size();

  public Page next() {
    return new AutoValue_Page(this.number() + 1, this.size());
  }
}
