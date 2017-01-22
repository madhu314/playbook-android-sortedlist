package is.uncommon.playbook.sortedlist.part2;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;

public class SortedListShouldBeSortedAdapter
    extends RecyclerView.Adapter<SortedListShouldBeSortedGridItemViewHolder> {
  SortedList<Article> articleSortedList;

  public SortedListShouldBeSortedAdapter(ArrayList<Article> articles) {
    articleSortedList =
        new SortedList<>(Article.class, new SortedListAdapterCallback<Article>(this) {
          @Override public int compare(Article o1, Article o2) {
            return o1.compare(o2);
          }

          @Override public boolean areContentsTheSame(Article oldItem, Article newItem) {
            return oldItem.areContentsTheSame(newItem);
          }

          @Override public boolean areItemsTheSame(Article item1, Article item2) {
            return item1.areContentsTheSame(item2);
          }
        });
    articleSortedList.addAll(articles);
  }

  @Override public SortedListShouldBeSortedGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return SortedListShouldBeSortedGridItemViewHolder.create(parent);
  }

  @Override public void onBindViewHolder(SortedListShouldBeSortedGridItemViewHolder holder, int position) {
    holder.bindTo(articleSortedList.get(position));
  }

  @Override public int getItemCount() {
    return articleSortedList.size();
  }
}
