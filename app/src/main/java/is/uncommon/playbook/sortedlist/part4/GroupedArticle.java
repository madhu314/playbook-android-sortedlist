package is.uncommon.playbook.sortedlist.part4;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import is.uncommon.playbook.sortedlist.Article;
import java.util.Comparator;
import org.joda.time.DateTime;
import org.joda.time.Days;

@AutoValue public abstract class GroupedArticle implements Parcelable {
  private static final int TYPE_TIMESTAMP = 1;
  private static final int TYPE_CONTENT = 2;
  private static final int TYPE_CATEGORY = 3;
  private static final int TYPE_AUTHOR = 4;

  @Nullable public abstract Article article();

  public abstract int type();

  public abstract String groupName();

  public abstract int groupKey();

  public static GroupedArticle[] byTimestamp(Article article) {
    int days = Days.daysBetween(DateTime.now().toLocalDate(),
        new DateTime(article.publishedTime()).toLocalDate()).getDays();
    String groupName = "Unknown";
    int groupKey = 0;
    if (days == 0) {
      groupName = "Today";
      groupKey = 0;
    } else if (days < 0) {
      if (days == -1) {
        groupName = "Yesterday";
        groupKey = -1;
      } else if (days == -2) {
        groupName = "Day Before";
        groupKey = -2;
      } else if (days > -7 && days < -2) {
        groupKey = -3;
        groupName = "In Past Week";
      } else if (days < -7) {
        groupKey = -4;
        groupName = "A Week Ago";
      } else {
        groupKey = -5;
        groupName = "Past";
      }
    } else {
      if (days == 1) {
        groupName = "Tomorrow";
        groupKey = 1;
      } else if (days == 2) {
        groupName = "Day After";
        groupKey = 2;
      } else if (days > 2 && days < 7) {
        groupKey = 3;
        groupName = "In Next Week";
      } else if (days > 7) {
        groupKey = 4;
        groupName = "After a Week";
      } else {
        groupKey = 5;
        groupName = "Future";
      }
    }
    GroupedArticle grouping =
        new AutoValue_GroupedArticle(null, GroupedArticle.TYPE_TIMESTAMP, groupName, groupKey);
    GroupedArticle wrappedArticle =
        new AutoValue_GroupedArticle(article, GroupedArticle.TYPE_TIMESTAMP, groupName, groupKey);
    return new GroupedArticle[] { grouping, wrappedArticle };
  }

  public static GroupedArticle[] byContent(Article article) {
    String groupName = article.content().substring(0, 1).toUpperCase();
    int groupKey = (int) groupName.charAt(0);
    GroupedArticle grouping =
        new AutoValue_GroupedArticle(null, GroupedArticle.TYPE_CONTENT, groupName, groupKey);
    GroupedArticle wrappedArticle =
        new AutoValue_GroupedArticle(article, GroupedArticle.TYPE_CONTENT, groupName, groupKey);
    return new GroupedArticle[] { grouping, wrappedArticle };
  }

  public static GroupedArticle[] byCategory(Article article) {
    String groupName = article.category();
    int groupKey = 0;
    for (int i = 0; i < Article.CATEGORIES.length; i++) {
      if (groupName.equalsIgnoreCase(Article.CATEGORIES[i])) {
        groupKey = i;
      }
    }
    GroupedArticle grouping =
        new AutoValue_GroupedArticle(null, GroupedArticle.TYPE_CATEGORY, groupName, groupKey);
    GroupedArticle wrappedArticle =
        new AutoValue_GroupedArticle(article, GroupedArticle.TYPE_CATEGORY, groupName, groupKey);
    return new GroupedArticle[] { grouping, wrappedArticle };
  }

  public static GroupedArticle[] byAuthor(Article article) {
    String groupName = article.author().substring(0, 1).toUpperCase();
    int groupKey = (int) groupName.charAt(0);
    GroupedArticle grouping =
        new AutoValue_GroupedArticle(null, GroupedArticle.TYPE_AUTHOR, groupName, groupKey);
    GroupedArticle wrappedArticle =
        new AutoValue_GroupedArticle(article, GroupedArticle.TYPE_AUTHOR, groupName, groupKey);
    return new GroupedArticle[] { grouping, wrappedArticle };
  }

  public boolean isGrouping() {
    return article() == null;
  }

  public boolean isArticle() {
    return article() != null;
  }

  public int compare(GroupedArticle that, Comparator<Article> articleComparator) {
    if (this.type() != that.type()) {
      //we support same type comparison
      throw new RuntimeException("Cannot compare different types");
    }

    if (this.isGrouping() && that.isGrouping()) {
      return Integer.compare(this.groupKey(), that.groupKey());
    } else if (this.isGrouping() && that.isArticle()) {
      if (this.groupName().equalsIgnoreCase(that.groupName())) {
        return -1;
      } else {
        return Integer.compare(this.groupKey(), that.groupKey());
      }
    } else if (this.isArticle() && that.isGrouping()) {
      if (this.groupName().equalsIgnoreCase(that.groupName())) {
        return 1;
      } else {
        return Integer.compare(this.groupKey(), that.groupKey());
      }
    } else {
      if (this.groupKey() == that.groupKey()) {
        return this.article().compare(that.article(), articleComparator);
      } else {
        return Integer.compare(this.groupKey(), that.groupKey());
      }
    }
  }

  public boolean areContentsTheSame(GroupedArticle that) {
    return this.equals(that);
  }

  public boolean areItemsTheSame(GroupedArticle that) {
    if (this.type() != that.type()) {
      //we support same type comparison
      throw new RuntimeException("Cannot compare different types");
    }

    if (this.isGrouping() && that.isGrouping()) {
      return this.groupKey() == that.groupKey();
    } else if (this.isArticle() && that.isArticle()) {
      return this.article().id() == that.article().id();
    }
    return false;
  }
}
