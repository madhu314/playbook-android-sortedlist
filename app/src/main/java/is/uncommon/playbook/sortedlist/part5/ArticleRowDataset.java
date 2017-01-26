package is.uncommon.playbook.sortedlist.part5;

import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;
import java.util.List;

public class ArticleRowDataset {

  private static final String DATASET = "dataset";
  private static final int DAYS_PRIOR = 20;
  private final SortedList<ArticleRow> sortedList;

  public ArticleRowDataset(final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
    this.sortedList = new SortedList<>(ArticleRow.class,
        new SortedList.BatchedCallback<>(new SortedListAdapterCallback<ArticleRow>(adapter) {
          @Override public int compare(ArticleRow a1, ArticleRow a2) {
            return a1.compare(a2);
          }

          @Override public boolean areContentsTheSame(ArticleRow oldItem, ArticleRow newItem) {
            return oldItem.areContentsTheSame(newItem);
          }

          @Override public boolean areItemsTheSame(ArticleRow item1, ArticleRow item2) {
            return item1.areItemsTheSame(item2);
          }

          @Override public void onInserted(int position, int count) {
            super.onInserted(position, count);
            recyclerView.scrollToPosition(position);
          }
        }));
    addInitialData();
  }

  private void addInitialData() {
    sortedList.beginBatchedUpdates();
    sortedList.add(ArticleRow.fullscreenLoader());
    sortedList.endBatchedUpdates();
  }

  public void restore(Bundle bundle) {
    if (bundle != null) {
      ArrayList<ArticleRow> articles = bundle.getParcelableArrayList(DATASET);
      sortedList.beginBatchedUpdates();
      sortedList.addAll(articles);
      sortedList.endBatchedUpdates();
    }
  }

  public Bundle asBundle() {
    ArrayList<ArticleRow> articles = new ArrayList<>();
    for (int i = 0; i < sortedList.size(); i++) {
      articles.add(sortedList.get(i));
    }
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(DATASET, articles);
    return bundle;
  }

  public int size() {
    return sortedList.size();
  }

  public ArticleRow get(int position) {
    return sortedList.get(position);
  }

  public void addArticles(Page page, List<Article> articles) {
    List<ArticleRow> articleList = new ArrayList<>();
    sortedList.beginBatchedUpdates();
    for (int i = 0; i < articles.size(); i++) {
      articleList.add(ArticleRow.ofArticle(articles.get(i), (page.number() * page.size()) + i));
    }
    sortedList.remove(ArticleRow.fullscreenLoader());
    sortedList.addAll(articleList);
    if (articles.size() < page.size()) {
      sortedList.remove(ArticleRow.pagingIndicator());
    } else {
      sortedList.add(ArticleRow.pagingIndicator());
    }
    sortedList.endBatchedUpdates();
  }

  public void reset() {
    sortedList.beginBatchedUpdates();
    sortedList.clear();
    sortedList.endBatchedUpdates();
    addInitialData();
  }

  public boolean hasPagingIndicator() {
    return sortedList.get(sortedList.size() - 1).isPagingIndicator() || sortedList.get(
        sortedList.size() - 1).isFullscreenLoader();
  }
}
