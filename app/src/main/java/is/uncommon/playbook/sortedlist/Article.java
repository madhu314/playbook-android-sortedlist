package is.uncommon.playbook.sortedlist;

import android.os.Parcelable;
import com.github.javafaker.Faker;
import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;

/**
 * Created by madhu on 29/11/16.
 */
@AutoValue public abstract class Article implements Parcelable {
  public static final String[] CATEGORIES = new String[] {
      "National", "International", "Business", "Technology", "Entertainment"
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
  public static final ArticleAuthorComparator authorComparator = new ArticleAuthorComparator();
  public static final ArticleContentComparator contentComparator = new ArticleContentComparator();
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

  public static Builder builder() {
    int id = IdMaker.next();
    return new AutoValue_Article.Builder().id(id)
        .publishedTime(TimestampMaker.current())
        .content(faker.lorem().paragraph(Utils.randomWithRange(3, 8)))
        .author(faker.name().firstName().trim())
        .category(CATEGORIES[(int) Math.round(Math.random() * 10) % CATEGORIES.length]);
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(int id);

    public abstract Builder publishedTime(long epochTime);

    public abstract Builder content(String content);

    public abstract Builder author(String author);

    public abstract Builder category(String category);

    public abstract Article build();
  }

  public static class IdMaker {
    static final AtomicInteger atomicInteger = new AtomicInteger();

    static int next() {
      return atomicInteger.incrementAndGet();
    }
  }

  public static class TimestampMaker {
    static final AtomicInteger future = new AtomicInteger();
    static final AtomicInteger past = new AtomicInteger();

    static long future() {
      return DateTime.now().plusDays(future.incrementAndGet()).getMillis();
    }

    static long past() {
      return DateTime.now().plusDays(past.decrementAndGet()).getMillis();
    }

    static long current() {
      return DateTime.now().getMillis();
    }
  }

  public static class Utils {

    public static int randomWithRange(int min, int max) {
      if (min == max) return min;
      if (max < min) throw new IllegalArgumentException("Max should be greater than min");

      int range = max - min;
      return (int) (Math.random() * range) + min;
    }

    public static String shuffleString(String string) {
      char[] characters = string.toCharArray();
      List<Character> bigChars = new ArrayList<>();
      for (int i = 0; i < characters.length; i++) {
        bigChars.add(characters[i]);
      }
      Collections.shuffle(bigChars);
      for (int i = 0; i < bigChars.size(); i++) {
        characters[i] = bigChars.get(i);
      }
      return String.valueOf(characters);
    }
  }
}
