package is.uncommon.playbook.sortedlist.part4;

import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.joda.time.DateTime;

public class GroupedArticleDataset {

  private static final String DATASET = "dataset";
  private static final String SORT_TYPE = "sortType";
  private static final String GROUPING_TYPE = "groupType";

  public enum SortType {
    TIMESTAMP,
    CATEGORY,
    AUTHOR,
    CONTENT
  }

  public enum GroupingType {
    TIMESTAMP,
    CATEGORY,
    AUTHOR,
    CONTENT
  }

  SortedList<GroupedArticle> sortedList = null;
  private SortType sortType = SortType.TIMESTAMP;
  private GroupingType groupingType = GroupingType.TIMESTAMP;

  public GroupedArticleDataset(final RecyclerView recyclerView,
      final RecyclerView.Adapter adapter) {
    this.sortedList = new SortedList<>(GroupedArticle.class,
        new SortedList.BatchedCallback<>(new SortedListAdapterCallback<GroupedArticle>(adapter) {
          @Override public int compare(GroupedArticle a1, GroupedArticle a2) {
            return a1.compare(a2, getComparator());
          }

          @Override
          public boolean areContentsTheSame(GroupedArticle oldItem, GroupedArticle newItem) {
            return oldItem.areContentsTheSame(newItem);
          }

          @Override public boolean areItemsTheSame(GroupedArticle item1, GroupedArticle item2) {
            return item1.areItemsTheSame(item2);
          }

          @Override public void onInserted(int position, int count) {
            super.onInserted(position, count);
            recyclerView.scrollToPosition(position);
          }
        }));
  }

  public String[] sortOptions() {
    String[] strings = new String[SortType.values().length];
    for (int i = 0; i < SortType.values().length; i++) {
      strings[i] = SortType.values()[i].toString();
    }
    return strings;
  }

  public void generateRandom() {
    List<GroupedArticle> articleList = new ArrayList<>();
    for (int i = 0; i < 14; i++) {
      for (int j = 0; j < 2; j++) {
        Article article = Article.builder()
            .publishedTime(DateTime.now().minusDays(i).plusHours(j).getMillis())
            .build();
        addGroupingForArticle(articleList, article);
      }
    }

    for (int i = 0; i < 14; i++) {
      for (int j = 0; j < 2; j++) {
        Article article = Article.builder()
            .publishedTime(DateTime.now().plusDays(i).plusHours(j).getMillis())
            .build();
        addGroupingForArticle(articleList, article);
      }
    }
    sortedList.beginBatchedUpdates();
    sortedList.addAll(articleList);
    sortedList.endBatchedUpdates();
  }

  private void addGroupingForArticle(List<GroupedArticle> articleList, Article article) {
    GroupedArticle[] arr = null;
    switch (this.groupingType) {
      case TIMESTAMP:
        arr = GroupedArticle.byTimestamp(article);
        break;
      case CATEGORY:
        arr = GroupedArticle.byCategory(article);
        break;
      case CONTENT:
        arr = GroupedArticle.byContent(article);
        break;
      case AUTHOR:
        arr = GroupedArticle.byAuthor(article);
        break;
    }
    articleList.add(arr[0]);
    articleList.add(arr[1]);
  }

  public void restore(Bundle bundle) {
    if (bundle != null) {
      ArrayList<GroupedArticle> articles = bundle.getParcelableArrayList(DATASET);
      this.sortType = (SortType) bundle.getSerializable(SORT_TYPE);
      this.groupingType = (GroupingType) bundle.getSerializable(GROUPING_TYPE);
      sortedList.beginBatchedUpdates();
      sortedList.addAll(articles);
      sortedList.endBatchedUpdates();
    }
  }

  public Bundle asBundle() {
    ArrayList<GroupedArticle> articles = new ArrayList<>();
    for (int i = 0; i < sortedList.size(); i++) {
      articles.add(sortedList.get(i));
    }
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList(DATASET, articles);
    bundle.putSerializable(SORT_TYPE, sortType);
    bundle.putSerializable(GROUPING_TYPE, groupingType);
    return bundle;
  }

  public int size() {
    return sortedList.size();
  }

  public void rebuildList() {
    List<GroupedArticle> items = new ArrayList<>();
    for (int j = 0; j < sortedList.size(); j++) {
      if (sortedList.get(j).isArticle()) {
        addGroupingForArticle(items, sortedList.get(j).article());
      }
    }
    sortedList.clear();
    sortedList.addAll(items);
    sortedList.endBatchedUpdates();
  }

  public void changeSortType(SortType sortType) {
    if (!this.sortType.equals(sortType)) {
      this.sortType = sortType;
      rebuildList();
    }
  }

  public void changeGroupingType(GroupingType groupingType) {
    if (!this.groupingType.equals(groupingType)) {
      this.groupingType = groupingType;
      rebuildList();
    }
  }

  public GroupedArticle getArticle(int position) {
    return sortedList.get(position);
  }

  public void remove(GroupedArticle article) {
    sortedList.beginBatchedUpdates();
    sortedList.remove(article);
    sortedList.endBatchedUpdates();
  }

  public void add(GroupedArticle article) {
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
