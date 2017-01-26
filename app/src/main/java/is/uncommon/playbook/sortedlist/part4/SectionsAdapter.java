package is.uncommon.playbook.sortedlist.part4;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class SectionsAdapter extends RecyclerView.Adapter<GroupedArticleViewHolder> {
  private static final int VIEW_TYPE_HEADER = 1;
  private static final int VIEW_TYPE_ARTICLE = 2;

  private GroupedArticleDataset dataset;

  @Override public GroupedArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch(viewType) {
      case VIEW_TYPE_HEADER:
        return HeaderViewHolder.create(parent);
      case VIEW_TYPE_ARTICLE:
        return ArticleViewHolder.create(parent);
      default:
        return null;
    }
  }

  @Override public void onBindViewHolder(GroupedArticleViewHolder holder, int position) {
    holder.bindTo(dataset.getArticle(position));
  }

  @Override public int getItemCount() {
    return dataset == null ? 0 : dataset.size();
  }

  @Override public int getItemViewType(int position) {
    if(dataset.getArticle(position).isGrouping()) {
      return VIEW_TYPE_HEADER;
    } else if(dataset.getArticle(position).isArticle()) {
      return VIEW_TYPE_ARTICLE;
    }
    return -1;
  }

  public void dataset(GroupedArticleDataset dataset) {
    this.dataset = dataset;
  }
}
