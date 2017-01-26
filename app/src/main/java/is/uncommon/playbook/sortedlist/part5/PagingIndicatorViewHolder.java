package is.uncommon.playbook.sortedlist.part5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.R;

public class PagingIndicatorViewHolder extends AbstractArticleRowViewHolder {
  public PagingIndicatorViewHolder(View itemView) {
    super(itemView);
  }

  @Override public void bindTo(ArticleRow articleRow) {
    //no-op
  }

  public static PagingIndicatorViewHolder create(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_paging_indicator, parent, false);
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    layoutParams.width = parent.getWidth();
    layoutParams.height = layoutParams.width / 4;
    return new PagingIndicatorViewHolder(view);
  }
}
