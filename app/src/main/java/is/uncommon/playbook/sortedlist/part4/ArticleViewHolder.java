package is.uncommon.playbook.sortedlist.part4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ArticleViewHolder extends GroupedArticleViewHolder {
  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormat.forPattern("MMM. d, HH:mm");

  private final TextView contentView;
  private final TextView dateView;
  private final TextView authorView;
  private final TextView categoryView;

  public ArticleViewHolder(View itemView) {
    super(itemView);
    contentView = (TextView) itemView.findViewById(R.id.content);
    dateView = (TextView) itemView.findViewById(R.id.date);
    authorView = (TextView) itemView.findViewById(R.id.author);
    categoryView = (TextView) itemView.findViewById(R.id.category);
  }

  public static ArticleViewHolder create(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_grid_section_article, parent, false);
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    layoutParams.height = parent.getWidth() / 2;
    layoutParams.width = layoutParams.height;
    view.setLayoutParams(layoutParams);
    return new ArticleViewHolder(view);
  }

  @Override public void bindTo(final GroupedArticle groupedArticle) {
    Article article = groupedArticle.article();
    contentView.setText(article.content());
    DateTime dateTime = new DateTime(article.publishedTime());
    dateView.setText(dateTime.toString(dateTimeFormatter));
    categoryView.setText(article.category());
    authorView.setText("By " + article.author());
  }
}
