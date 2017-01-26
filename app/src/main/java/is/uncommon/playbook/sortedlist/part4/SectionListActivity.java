package is.uncommon.playbook.sortedlist.part4;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import is.uncommon.playbook.sortedlist.R;
import timber.log.Timber;

public class SectionListActivity extends AppCompatActivity {
  private static final String DATA = "data";
  @BindView(R.id.recycler) RecyclerView recyclerView;
  private GroupedArticleDataset dataset;
  private SectionsAdapter adapter;
  private Unbinder unbinder;

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_section_list);
    unbinder = ButterKnife.bind(this);
    recyclerView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            setupRecycler(savedInstanceState);
          }
        });
  }

  private void setupRecycler(Bundle savedInstanceState) {
    GridLayoutManager gridManager = new GridLayoutManager(this, 2);
    gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return dataset.getArticle(position).isGrouping() ? 2 : 1;
      }
    });
    recyclerView.setLayoutManager(gridManager);
    adapter = new SectionsAdapter();
    dataset = new GroupedArticleDataset(recyclerView, adapter);
    adapter.dataset(dataset);
    if(savedInstanceState != null) {
      dataset.restore(savedInstanceState.getBundle(DATA));
    } else {
      dataset.generateRandom();
    }
    recyclerView.setAdapter(adapter);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_group_sort, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_sort) {
      showSortOptionsDialog();
      return true;
    } else if (item.getItemId() == R.id.menu_group) {
      showGroupOptionsDialog();
      return true;
    } else if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showGroupOptionsDialog() {
    String[] strings = new String[GroupedArticleDataset.GroupingType.values().length];
    for (int i = 0; i < GroupedArticleDataset.GroupingType.values().length; i++) {
      strings[i] = GroupedArticleDataset.GroupingType.values()[i].toString();
    }

    ArrayAdapter<String> arrayAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
    new AlertDialog.Builder(this).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, final int i) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            dataset.changeGroupingType(GroupedArticleDataset.GroupingType.values()[i]);
          }
        });
      }
    }).show();
  }

  private void showSortOptionsDialog() {
    String[] strings = new String[GroupedArticleDataset.SortType.values().length];
    for (int i = 0; i < GroupedArticleDataset.SortType.values().length; i++) {
      strings[i] = GroupedArticleDataset.SortType.values()[i].toString();
    }

    ArrayAdapter<String> arrayAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
    new AlertDialog.Builder(this).setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, final int i) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            dataset.changeSortType(GroupedArticleDataset.SortType.values()[i]);
          }
        });
      }
    }).show();
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    Timber.d("On saving instance state");
    outState.putBundle(DATA, dataset.asBundle());
    super.onSaveInstanceState(outState);
  }
}
