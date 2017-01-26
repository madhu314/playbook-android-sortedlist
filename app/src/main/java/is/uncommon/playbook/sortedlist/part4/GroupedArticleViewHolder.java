package is.uncommon.playbook.sortedlist.part4;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class GroupedArticleViewHolder extends RecyclerView.ViewHolder {
  public GroupedArticleViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void bindTo(GroupedArticle groupedArticle);
}
