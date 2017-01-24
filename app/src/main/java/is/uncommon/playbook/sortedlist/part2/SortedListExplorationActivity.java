package is.uncommon.playbook.sortedlist.part2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import is.uncommon.playbook.sortedlist.R;

public class SortedListExplorationActivity extends AppCompatActivity {
  @BindView(R.id.container) FrameLayout fragmentContainer;
  private Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sortedlist_exploration);
    unbinder = ButterKnife.bind(this);
    Fragment fragment = SortedListBatchOperationFragment.create();
    getFragmentManager().beginTransaction().add(fragmentContainer.getId(), fragment).commit();
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}
