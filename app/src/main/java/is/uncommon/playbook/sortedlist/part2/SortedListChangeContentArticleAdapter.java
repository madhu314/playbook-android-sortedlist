package is.uncommon.playbook.sortedlist.part2;

import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;

public class SortedListChangeContentArticleAdapter extends SortedListShouldBeSortedAdapter {

  public SortedListChangeContentArticleAdapter(ArrayList<Article> articles) {
    super(articles);
  }

  @Override
  public SortedListChangeContentArticleGridItemViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    return SortedListChangeContentArticleGridItemViewHolder.create(parent);
  }

  @Override
  public void onBindViewHolder(SortedListShouldBeSortedGridItemViewHolder holder, int position) {
    ((SortedListChangeContentArticleGridItemViewHolder) holder).bindTo(
        articleSortedList.get(position), this);
  }

  public void changeContent(Article article) {
    articleSortedList.add(article.dupe()
        .toBuilder()
        .author(Article.faker.name().firstName())
        .category(Article.CATEGORIES[2])
        .content(Article.faker.lorem().paragraph(2))
        .build());
  }
}
