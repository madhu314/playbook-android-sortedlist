package is.uncommon.playbook.sortedlist;

import android.app.Application;
import net.danlew.android.joda.JodaTimeAndroid;
import timber.log.Timber;

/**
 * Created by madhu on 29/11/16.
 */

public class MainApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();
    JodaTimeAndroid.init(this);
    Timber.plant(new Timber.DebugTree());
  }
}
