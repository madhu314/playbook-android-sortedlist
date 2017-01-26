package is.uncommon.playbook.sortedlist.part5;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class ArticleRowAdapter extends RecyclerView.Adapter<AbstractArticleRowViewHolder> {
  private ArticleRowDataset dataset;

  @Override public AbstractArticleRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case ArticleRow.TYPE_FULLSCREEN_LOADER:
        return FullscreenLoadingViewHolder.create(parent);
      case ArticleRow.TYPE_PAGING_INDICATOR:
        return PagingIndicatorViewHolder.create(parent);
      case ArticleRow.TYPE_ARTICLE:
        return ArticleViewHolder.create(parent);
    }
    return null;
  }

  @Override public void onBindViewHolder(AbstractArticleRowViewHolder holder, int position) {
    holder.bindTo(dataset.get(position));
  }

  @Override public int getItemCount() {
    return dataset == null ? 0 : dataset.size();
  }

  public void dataset(ArticleRowDataset dataset) {
    this.dataset = dataset;
  }

  @Override public int getItemViewType(int position) {
    return dataset.get(position).type();
  }
}
