package is.uncommon.playbook.sortedlist.part3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.Article;

public class DatasetHorizontalAdapter extends RecyclerView.Adapter<DatasetViewHolder> {
  private ArticleDataset dataset;

  @Override public DatasetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return DatasetViewHolder.squareWithMatchingHeight(parent);
  }

  @Override public void onBindViewHolder(final DatasetViewHolder holder, final int position) {
    final Article article = dataset.getArticle(position);
    holder.bindTo(article);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Context context = holder.itemView.getContext();
        if (context instanceof ActivityBus) {
          ((ActivityBus) context).onBusEvent(ActivityBus.HORIZONTAL_ITEM_CLICKED,
              article);
        }
      }
    });
  }

  @Override public int getItemCount() {
    return dataset == null ? 0 : dataset.size();
  }

  public void dataset(ArticleDataset dataset) {
    this.dataset = dataset;
  }
}
