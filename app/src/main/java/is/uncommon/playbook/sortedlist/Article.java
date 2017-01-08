package is.uncommon.playbook.sortedlist;

import android.os.Parcelable;
import com.github.javafaker.Faker;
import com.google.auto.value.AutoValue;
import java.util.Comparator;

/**
 * Created by madhu on 29/11/16.
 */
@AutoValue public abstract class Article implements Parcelable {
  public static final String[] CATEGORIES = new String[] {
      "Business", "Entertainment", "Technology", "National", "International"
  };

  public static class ArticleTimestampComparator implements Comparator<Article> {
    @Override public int compare(Article first, Article second) {
      return Long.compare(first.publishedTime(), second.publishedTime());
    }
  }

  public static class ArticleAuthorComparator implements Comparator<Article> {
    @Override public int compare(Article first, Article second) {
      return first.author().compareToIgnoreCase(second.author());
    }
  }

  public static class ArticleContentComparator implements Comparator<Article> {
    @Override public int compare(Article first, Article second) {
      return first.content().compareToIgnoreCase(second.content());
    }
  }

  public static class ArticleCategoryComparator implements Comparator<Article> {
    @Override public int compare(Article first, Article second) {
      return first.category().compareToIgnoreCase(second.category());
    }
  }

  public static final Faker faker = new Faker();

  public static final ArticleTimestampComparator timestampComparator =
      new ArticleTimestampComparator();
  public static final ArticleAuthorComparator authorComparator =
      new ArticleAuthorComparator();
  public static final ArticleContentComparator contentComparator =
      new ArticleContentComparator();
  public static final ArticleCategoryComparator categoryComparator =
      new ArticleCategoryComparator();

  public static Article past() {
    return builder().publishedTime(TimestampMaker.past()).build();
  }

  public static Article future() {
    return builder().publishedTime(TimestampMaker.future()).build();
  }

  public static Article current() {
    return builder().publishedTime(TimestampMaker.current()).build();
  }

  public int compare(Article article, Comparator<Article> articleComparator) {
    return articleComparator.compare(this, article);
  }

  public int compare(Article article) {
    return timestampComparator.compare(this, article);
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

  public abstract String category();

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
        .author(faker.name().firstName().trim())
        .category(CATEGORIES[(int) Math.round(Math.random() * 10) % CATEGORIES.length]);
  }

  @AutoValue.Builder abstract static class Builder {
    public abstract Builder id(int id);

    public abstract Builder publishedTime(long epochTime);

    public abstract Builder content(String content);

    public abstract Builder author(String author);

    public abstract Builder category(String category);

    abstract Article build();
  }
}
