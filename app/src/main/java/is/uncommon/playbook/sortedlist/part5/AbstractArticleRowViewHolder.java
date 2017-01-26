package is.uncommon.playbook.sortedlist.part5;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractArticleRowViewHolder extends RecyclerView.ViewHolder {
  public AbstractArticleRowViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void bindTo(ArticleRow articleRow);
}
