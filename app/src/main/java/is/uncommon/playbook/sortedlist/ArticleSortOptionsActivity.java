package is.uncommon.playbook.sortedlist;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by madhu on 08/01/17.
 */

public class ArticleSortOptionsActivity extends AppCompatActivity {
  private static final int SORT_TYPE_TIMESTAMP = 0;
  private static final int SORT_TYPE_CATEGORY = 1;
  private static final int SORT_TYPE_AUTHOR = 2;
  private static final int SORT_TYPE_CONTENT = 3;

  @BindView(R.id.recycler) RecyclerView recyclerView;
  private Unbinder binder;
  private ArticlesAdapter adapter;
  private ArticleDataset dataset;
  private int sortType = SORT_TYPE_TIMESTAMP;
  private int newSortType = sortType;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sort_options);
    binder = ButterKnife.bind(this);
    setupRecycler();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_sort, menu);
    return true;
  }

  private void setupRecycler() {
    recyclerView.setLayoutManager(
        new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
    adapter = new ArticlesAdapter();
    dataset = new ArticleDataset(adapter);
    dataset.generateRandom();
    adapter.articleDataset(dataset);
    recyclerView.setAdapter(adapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        float offsetFloat =
            2f * parent.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        int halfOffset = Math.round(offsetFloat / 2f);
        int offset = Math.round(offsetFloat);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
          outRect.set(offset, offset, offset, halfOffset);
        } else if (position > 0 && position < parent.getAdapter().getItemCount() - 1) {
          outRect.set(offset, halfOffset, offset, halfOffset);
        } else if (position == parent.getAdapter().getItemCount() - 1) {
          outRect.set(offset, halfOffset, offset, offset);
        }
      }
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_sort) {
      showSortOptionsDialog();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showSortOptionsDialog() {
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[] {
            "Timestamp", "Category", "Author", "Content"
        });
    new AlertDialog.Builder(this).setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, int position) {
        ArticleSortOptionsActivity.this.newSortType = position;
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialogInterface) {
        if (ArticleSortOptionsActivity.this.newSortType
            != ArticleSortOptionsActivity.this.sortType) {
          runOnUiThread(new Runnable() {
            @Override public void run() {
              ArticleSortOptionsActivity.this.sortType = ArticleSortOptionsActivity.this.newSortType;
              ArticleSortOptionsActivity.this.dataset.changeSortType(
                  ArticleSortOptionsActivity.this.sortType);
            }
          });
        }
      }
    }).show();
  }

  @Override protected void onDestroy() {
    binder.unbind();
    super.onDestroy();
  }

  public static class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ItemViewHolder> {
    private ArticleDataset dataset;

    @Override public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return ItemViewHolder.create(parent);
    }

    @Override public void onBindViewHolder(ItemViewHolder holder, int position) {
      holder.bindTo(dataset.getArticle(position), this);
    }

    @Override public int getItemCount() {
      return dataset.size();
    }

    public void articleDataset(ArticleDataset dataset) {
      this.dataset = dataset;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

      public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMM. d");

      private final TextView contentView;
      private final TextView dateView;
      private final TextView authorView;
      private final TextView categoryView;

      public ItemViewHolder(View itemView) {
        super(itemView);
        contentView = (TextView) itemView.findViewById(R.id.content);
        dateView = (TextView) itemView.findViewById(R.id.date);
        authorView = (TextView) itemView.findViewById(R.id.author);
        categoryView = (TextView) itemView.findViewById(R.id.category);
      }

      public static ItemViewHolder create(ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_article_row, parent, false);
        return new ItemViewHolder(view);
      }

      public void bindTo(final Article article, final ArticlesAdapter adapter) {
        contentView.setText(article.content());
        DateTime dateTime = new DateTime(article.publishedTime());
        dateView.setText(dateTime.toString(dateTimeFormatter));
        categoryView.setText(article.category());
        authorView.setText("By " + article.author());
      }
    }
  }

  public static class ArticleDataset {

    private static final int DAYS_PRIOR = 20;
    SortedList<Article> sortedList = null;
    private int sortType = SORT_TYPE_TIMESTAMP;

    public ArticleDataset(RecyclerView.Adapter adapter) {
      this.sortedList = new SortedList<>(Article.class,
          new SortedList.BatchedCallback<>(new SortedListAdapterCallback<Article>(adapter) {
            @Override public int compare(Article a1, Article a2) {
              return getComparator().compare(a1, a2);
            }

            @Override public boolean areContentsTheSame(Article oldItem, Article newItem) {
              return oldItem.areContentsTheSame(newItem);
            }

            @Override public boolean areItemsTheSame(Article item1, Article item2) {
              return item1.areItemsTheSame(item2);
            }
          }));
    }

    public void generateRandom() {
      List<Article> articleList = new ArrayList<>();
      for (int i = 0; i < DAYS_PRIOR; i++) {
        articleList.add(
            Article.builder().publishedTime(DateTime.now().minusDays(i).getMillis()).build());
      }
      sortedList.beginBatchedUpdates();
      sortedList.addAll(articleList);
      sortedList.endBatchedUpdates();
    }

    public int size() {
      return sortedList.size();
    }

    public void changeSortType(int sortType) {
      List<Article> articles = new ArrayList<>();
      for (int i = 0; i < sortedList.size(); i++) {
        articles.add(sortedList.get(i));
      }
      sortedList.beginBatchedUpdates();
      sortedList.clear();
      sortedList.endBatchedUpdates();

      sortedList.beginBatchedUpdates();
      this.sortType = sortType;
      sortedList.addAll(articles);
      sortedList.endBatchedUpdates();

    }

    public Article getArticle(int position) {
      return sortedList.get(position);
    }

    public void remove(Article article) {
      sortedList.beginBatchedUpdates();
      sortedList.remove(article);
      sortedList.endBatchedUpdates();
    }

    public void add(Article article) {
      sortedList.beginBatchedUpdates();
      sortedList.add(article);
      sortedList.endBatchedUpdates();
    }

    private Comparator<Article> getComparator() {
      switch (sortType) {
        case SORT_TYPE_AUTHOR:
          return Article.authorComparator;
        case SORT_TYPE_CATEGORY:
          return Article.categoryComparator;
        case SORT_TYPE_CONTENT:
          return Article.contentComparator;
        case SORT_TYPE_TIMESTAMP:
        default:
          return Article.timestampComparator;
      }
    }
  }
}
