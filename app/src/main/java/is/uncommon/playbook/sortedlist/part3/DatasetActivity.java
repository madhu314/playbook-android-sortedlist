package is.uncommon.playbook.sortedlist.part3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.ArticleSortOptionsActivity;
import is.uncommon.playbook.sortedlist.R;
import timber.log.Timber;

public class DatasetActivity extends AppCompatActivity implements ActivityBus {
  private static final String HORIZONTAL_DATA = "horizontalDataset";
  private static final String GRID_DATA = "gridDataset";
  @BindView(R.id.gridRecycler) RecyclerView gridRecyclerView;
  @BindView(R.id.horizontalRecycler) RecyclerView horizontalRecyclerView;
  private DatasetHorizontalAdapter horizontalAdapter;
  private ArticleDataset horizonalDataset;
  private ArticleDataset gridDataset;
  private Unbinder unbinder;
  private DatasetGridAdapter gridAdapter;
  private ArticleDataset.SortType newSortType;

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dataset);
    unbinder = ButterKnife.bind(this);
    gridRecyclerView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            gridRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            setupRecycler();
            setupDatasets(savedInstanceState);
          }
        });
  }

  private void setupDatasets(Bundle savedInstanceState) {
    horizontalAdapter = new DatasetHorizontalAdapter();
    horizonalDataset = new ArticleDataset(horizontalRecyclerView, horizontalAdapter);
    if (savedInstanceState != null) {
      Timber.d("Restoring horizontal data from saved state");
      horizonalDataset.restore(savedInstanceState.getBundle(HORIZONTAL_DATA));
    } else {
      horizonalDataset.generateRandom();
    }
    horizontalAdapter.dataset(horizonalDataset);
    horizontalRecyclerView.setAdapter(horizontalAdapter);

    gridAdapter = new DatasetGridAdapter();
    gridDataset = new ArticleDataset(gridRecyclerView, gridAdapter);
    if (savedInstanceState != null) {
      Timber.d("Restoring grid data from saved state");
      gridDataset.restore(savedInstanceState.getBundle(GRID_DATA));
    }
    gridAdapter.dataset(gridDataset);
    gridRecyclerView.setAdapter(gridAdapter);
  }

  private void setupRecycler() {
    horizontalRecyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    gridRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    Timber.d("On saving instance state");
    outState.putBundle(HORIZONTAL_DATA, horizonalDataset.asBundle());
    outState.putBundle(GRID_DATA, gridDataset.asBundle());
    super.onSaveInstanceState(outState);
  }

  @Override public void onBusEvent(String eventName, Object data) {
    Timber.d("On bus event %s - %s", eventName, data);
    if (ActivityBus.HORIZONTAL_ITEM_CLICKED.equals(eventName)) {
      horizonalDataset.remove((Article) data);
      gridDataset.add((Article) data);
    } else if (ActivityBus.GRID_ITEM_CLICKED.equals(eventName)) {
      horizonalDataset.add((Article) data);
      gridDataset.remove((Article) data);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_sort, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_sort) {
      showSortOptionsDialog();
      return true;
    } else if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showSortOptionsDialog() {
    final ArticleDataset.SortType[] sortTypes = new ArticleDataset.SortType[] {
        ArticleDataset.SortType.TIMESTAMP, ArticleDataset.SortType.CATEGORY, ArticleDataset.SortType.AUTHOR,
        ArticleDataset.SortType.CONTENT
    };

    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[] {
            "Timestamp", "Category", "Author", "Content"
        });
    new AlertDialog.Builder(this).setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, int position) {
        DatasetActivity.this.newSortType = sortTypes[position];
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialogInterface) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
              gridDataset.changeSortType(DatasetActivity.this.newSortType);
              horizonalDataset.changeSortType(DatasetActivity.this.newSortType);
            }
          });
      }
    }).show();
  }
}
