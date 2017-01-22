package is.uncommon.playbook.sortedlist.part2;

import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;

public class SortedListRemoveArticleAdapter extends SortedListShouldBeSortedAdapter {

  public SortedListRemoveArticleAdapter(ArrayList<Article> articles) {
    super(articles);
  }

  @Override public SortedListShouldBeSortedGridItemViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    return SortedListRemoveArticleGridItemViewHolder.create(parent);
  }

  @Override
  public void onBindViewHolder(SortedListShouldBeSortedGridItemViewHolder holder, int position) {
    ((SortedListRemoveArticleGridItemViewHolder) holder).bindTo(articleSortedList.get(position),
        this);
  }

  public void removeArticle(Article article) {
    articleSortedList.remove(article);
  }
}
