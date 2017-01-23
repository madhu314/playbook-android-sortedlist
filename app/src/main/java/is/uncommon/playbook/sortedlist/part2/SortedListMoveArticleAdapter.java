package is.uncommon.playbook.sortedlist.part2;

import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;
import org.joda.time.DateTime;

public class SortedListMoveArticleAdapter extends SortedListShouldBeSortedAdapter {

  public SortedListMoveArticleAdapter(ArrayList<Article> articles) {
    super(articles);
  }

  @Override
  public SortedListMoveGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return SortedListMoveGridItemViewHolder.create(parent);
  }

  @Override
  public void onBindViewHolder(SortedListShouldBeSortedGridItemViewHolder holder, int position) {
    ((SortedListMoveGridItemViewHolder) holder).bindTo(articleSortedList.get(position), this);
  }

  public void changeTimestamp(Article article) {
    Article changedArticle = article.dupe()
        .toBuilder()
        .publishedTime(new DateTime(article.publishedTime()).minusDays(30).getMillis())
        .build();
    int itemIndex = articleSortedList.indexOf(article);
    articleSortedList.updateItemAt(itemIndex, changedArticle);
  }
}
