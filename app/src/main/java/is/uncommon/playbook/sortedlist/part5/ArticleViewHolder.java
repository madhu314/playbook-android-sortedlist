package is.uncommon.playbook.sortedlist.part5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ArticleViewHolder extends AbstractArticleRowViewHolder {
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

  @Override public void bindTo(ArticleRow articleRow) {
    Article article = articleRow.article();
    contentView.setText(article.content());
    DateTime dateTime = new DateTime(article.publishedTime());
    dateView.setText(dateTime.toString(dateTimeFormatter));
    categoryView.setText(article.category());
    authorView.setText("By " + article.author());
  }

  public static ArticleViewHolder create(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_article, parent, false);
    return new ArticleViewHolder(view);
  }
}
