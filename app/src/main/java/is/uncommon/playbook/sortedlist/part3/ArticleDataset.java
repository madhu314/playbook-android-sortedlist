package is.uncommon.playbook.sortedlist.part3;

import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.joda.time.DateTime;

public class ArticleDataset {

  private static final String DATASET = "dataset";
  private static final String SORT_TYPE = "sortType";

  public enum SortType {
    TIMESTAMP,
    CATEGORY,
    AUTHOR,
    CONTENT
  }

  private static final int DAYS_PRIOR = 20;
  SortedList<Article> sortedList = null;
  private SortType sortType = SortType.TIMESTAMP;

  public ArticleDataset(final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
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

          @Override public void onInserted(int position, int count) {
            super.onInserted(position, count);
            recyclerView.scrollToPosition(position);
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

  public void restore(Bundle bundle) {
    if (bundle != null) {
      ArrayList<Article> articles = bundle.getParcelableArrayList(DATASET);
      sortedList.beginBatchedUpdates();
      sortedList.addAll(articles);
      sortedList.endBatchedUpdates();
      this.sortType = (SortType) bundle.getSerializable(SORT_TYPE);
    }
  }

  public Bundle asBundle() {
    ArrayList<Article> articles = new ArrayList<>();
    for (int i = 0; i < sortedList.size(); i++) {
      articles.add(sortedList.get(i));
    }
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(DATASET, articles);
    bundle.putSerializable(SORT_TYPE, sortType);
    return bundle;
  }

  public int size() {
    return sortedList.size();
  }

  public void changeSortType(SortType sortType) {
    this.sortType = sortType;
    List<Article> items = new ArrayList<>();
    for (int j = 0; j < sortedList.size(); j++) {
      items.add(sortedList.get(j));
    }
    sortedList.clear();
    sortedList.addAll(items);
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
      case AUTHOR:
        return Article.authorComparator;
      case CATEGORY:
        return Article.categoryComparator;
      case CONTENT:
        return Article.contentComparator;
      case TIMESTAMP:
      default:
        return Article.timestampComparator;
    }
  }
}
