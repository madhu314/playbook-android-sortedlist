package is.uncommon.playbook.sortedlist;

import android.os.Parcelable;
import com.github.javafaker.Faker;
import com.google.auto.value.AutoValue;
import java.util.Comparator;

/**
 * Created by madhu on 29/11/16.
 */
@AutoValue public abstract class Article implements Parcelable {

  public static class ArticleTimestampComparator implements Comparator<Article> {

    @Override public int compare(Article first, Article second) {
      return Long.compare(first.publishedTime(), second.publishedTime());
    }
  }

  public static final Faker faker = new Faker();

  public static final ArticleTimestampComparator articleTimestampComparator =
      new ArticleTimestampComparator();

  public static Article past() {
    return builder().publishedTime(TimestampMaker.past()).build();
  }

  public static Article future() {
    return builder().publishedTime(TimestampMaker.future()).build();
  }

  public static Article current() {
    return builder().publishedTime(TimestampMaker.current()).build();
  }

  public int compare(Article article) {
    return articleTimestampComparator.compare(this, article);
  }

  public boolean areContentsTheSame(Article article) {
    return this.equals(article);
  }

  public boolean areItemsTheSame(Article article) {
    return this.id() == article.id();
  }

  public abstract int id();

  public abstract long publishedTime();

  public abstract String content();

  public abstract String author();

  public Article dupe() {
    return toBuilder().build();
  }

  public Builder toBuilder() {
    return new AutoValue_Article.Builder(this);
  }

  static Builder builder() {
    int id = IdMaker.next();
    return new AutoValue_Article.Builder().id(id)
        .publishedTime(TimestampMaker.current())
        .content(faker.lorem().paragraph(Utils.randomWithRange(3, 8)))
        .author(faker.name().fullName());
  }

  @AutoValue.Builder abstract static class Builder {
    public abstract Builder id(int id);

    public abstract Builder publishedTime(long epochTime);

    public abstract Builder content(String content);

    public abstract Builder author(String author);

    abstract Article build();
  }
}
