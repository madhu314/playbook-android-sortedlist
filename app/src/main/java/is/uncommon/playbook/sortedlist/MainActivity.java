package is.uncommon.playbook.sortedlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by madhu on 14/01/17.
 */

public class MainActivity extends AppCompatActivity {

  private Unbinder binder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    binder = ButterKnife.bind(this);
  }

  @Override protected void onDestroy() {
    binder.unbind();
    super.onDestroy();
  }
}
