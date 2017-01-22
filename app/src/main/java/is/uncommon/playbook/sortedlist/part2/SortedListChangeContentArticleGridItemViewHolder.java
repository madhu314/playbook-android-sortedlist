package is.uncommon.playbook.sortedlist.part2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;

public class SortedListChangeContentArticleGridItemViewHolder
    extends SortedListShouldBeSortedGridItemViewHolder {

  public static SortedListChangeContentArticleGridItemViewHolder create(ViewGroup parent) {
    ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_grid_article, parent, false);
    ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
    layoutParams.width = parent.getWidth() / 2;
    layoutParams.height = layoutParams.width;
    itemView.setLayoutParams(layoutParams);
    return new SortedListChangeContentArticleGridItemViewHolder(itemView);
  }

  public SortedListChangeContentArticleGridItemViewHolder(View itemView) {
    super(itemView);
  }

  public void bindTo(final Article article, final SortedListChangeContentArticleAdapter adapter) {
    super.bindTo(article);
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        adapter.changeContent(article);
      }
    });
  }
}
