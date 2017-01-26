package is.uncommon.playbook.sortedlist.part4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.uncommon.playbook.sortedlist.R;

public class HeaderViewHolder extends GroupedArticleViewHolder {
  private final TextView headerView;

  public HeaderViewHolder(View itemView) {
    super(itemView);
    headerView = (TextView) itemView.findViewById(R.id.text);
  }

  public static HeaderViewHolder create(ViewGroup parent) {
    ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_header, parent, false);
    return new HeaderViewHolder(view);
  }

  @Override public void bindTo(final GroupedArticle article) {
    headerView.setText(article.groupName());
  }
}


