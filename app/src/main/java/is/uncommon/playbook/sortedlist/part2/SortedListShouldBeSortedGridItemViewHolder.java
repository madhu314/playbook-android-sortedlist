package is.uncommon.playbook.sortedlist.part2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SortedListShouldBeSortedGridItemViewHolder extends RecyclerView.ViewHolder {
  public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMM. d");

  private final TextView contentView;
  private final TextView dateView;
  private final TextView authorView;
  private final TextView categoryView;

  public SortedListShouldBeSortedGridItemViewHolder(View itemView) {
    super(itemView);
    contentView = (TextView) itemView.findViewById(R.id.content);
    dateView = (TextView) itemView.findViewById(R.id.date);
    authorView = (TextView) itemView.findViewById(R.id.author);
    categoryView = (TextView) itemView.findViewById(R.id.category);
  }

  public static SortedListShouldBeSortedGridItemViewHolder create(ViewGroup parent) {
    ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_grid_article, parent, false);
    ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
    layoutParams.width = parent.getWidth() / 2;
    layoutParams.height = layoutParams.width;
    itemView.setLayoutParams(layoutParams);
    return new SortedListShouldBeSortedGridItemViewHolder(itemView);
  }

  public void bindTo(final Article article) {
    contentView.setText(article.content());
    DateTime dateTime = new DateTime(article.publishedTime());
    dateView.setText(dateTime.toString(dateTimeFormatter));
    categoryView.setText(article.category());
    authorView.setText("By " + article.author());
  }
}
