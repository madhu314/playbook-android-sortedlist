package is.uncommon.playbook.sortedlist.part5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import is.uncommon.playbook.sortedlist.R;

public class FullscreenLoadingViewHolder extends AbstractArticleRowViewHolder {
  public FullscreenLoadingViewHolder(View itemView) {
    super(itemView);
  }

  @Override public void bindTo(ArticleRow articleRow) {
    //no-op
  }

  public static FullscreenLoadingViewHolder create(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_paging_fullscreen_loader, parent, false);
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    layoutParams.width = parent.getWidth();
    layoutParams.height = parent.getHeight();
    return new FullscreenLoadingViewHolder(view);
  }
}
