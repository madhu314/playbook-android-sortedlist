package is.uncommon.playbook.sortedlist.part2;

import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;
import org.joda.time.DateTime;

public class SortedListBatchOperationsAdapter extends SortedListShouldBeSortedAdapter {
  public SortedListBatchOperationsAdapter(ArrayList<Article> articles) {
    super(articles);
    articleSortedList = new SortedList<>(Article.class,
        new SortedList.BatchedCallback<>(new SortedListAdapterCallback<Article>(this) {
          @Override public int compare(Article o1, Article o2) {
            return o1.compare(o2);
          }

          @Override public boolean areContentsTheSame(Article oldItem, Article newItem) {
            return oldItem.areContentsTheSame(newItem);
          }

          @Override public boolean areItemsTheSame(Article item1, Article item2) {
            return item1.areItemsTheSame(item2);
          }
        }));
    articleSortedList.beginBatchedUpdates();
    articleSortedList.addAll(articles);
    articleSortedList.endBatchedUpdates();
  }

  public void performBatchOperation() {
    articleSortedList.beginBatchedUpdates();

    //change third item
    Article third = articleSortedList.get(2);
    Article thirdChanged = third.dupe()
        .toBuilder()
        .author(Article.faker.name().firstName())
        .category(Article.CATEGORIES[2])
        .content(Article.faker.lorem().paragraph(2))
        .build();
    articleSortedList.add(thirdChanged);

    //remove first two
    articleSortedList.removeItemAt(0);
    articleSortedList.removeItemAt(0);

    //move last item to beginning
    Article lastArticle = articleSortedList.get(articleSortedList.size() - 1);
    Article changedLastArticle = lastArticle.dupe()
        .toBuilder()
        .publishedTime(new DateTime(lastArticle.publishedTime()).minusDays(30).getMillis())
        .build();
    articleSortedList.updateItemAt(articleSortedList.size() - 1, changedLastArticle);

    //add two new articles at the beginning
    Article first = Article.builder().publishedTime(DateTime.now().minusDays(50).getMillis()).build();
    Article second = Article.builder().publishedTime(DateTime.now().minusDays(52).getMillis()).build();
    articleSortedList.add(first);
    articleSortedList.add(second);

    articleSortedList.endBatchedUpdates();
  }
}
