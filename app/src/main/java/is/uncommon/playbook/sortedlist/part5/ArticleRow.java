package is.uncommon.playbook.sortedlist.part5;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import is.uncommon.playbook.sortedlist.Article;

@AutoValue public abstract class ArticleRow implements Parcelable {
  public static final int TYPE_FULLSCREEN_LOADER = 1;
  public static final int TYPE_PAGING_INDICATOR = 2;
  public static final int TYPE_ARTICLE = 3;

  public static ArticleRow fullscreenLoader() {
    return new AutoValue_ArticleRow(null, TYPE_FULLSCREEN_LOADER, Integer.MIN_VALUE);
  }

  public static ArticleRow pagingIndicator() {
    return new AutoValue_ArticleRow(null, TYPE_PAGING_INDICATOR, Integer.MAX_VALUE);
  }

  public static ArticleRow ofArticle(Article article, int atIndex) {
    return new AutoValue_ArticleRow(article, TYPE_ARTICLE, atIndex);
  }

  @Nullable public abstract Article article();

  public abstract int type();

  public abstract int index();

  public boolean isFullscreenLoader() {
    return type() == TYPE_FULLSCREEN_LOADER;
  }

  public boolean isPagingIndicator() {
    return type() == TYPE_PAGING_INDICATOR;
  }

  public boolean isArticle() {
    return type() == TYPE_ARTICLE;
  }

  public int compare(ArticleRow that) {
    if (this.type() == that.type()) {
      if (this.isArticle()) {
        return Integer.compare(this.index(), that.index());
      } else {
        return 0;
      }
    } else {
      if (this.isFullscreenLoader()) {
        return -1;
      } else if (this.isPagingIndicator()) {
        return 1;
      } else {
        if (this.isArticle() && that.isFullscreenLoader()) {
          return 1;
        } else if (this.isArticle() && that.isPagingIndicator()) {
          return -1;
        }
      }
    }
    return 0;
  }

  public boolean areContentsTheSame(ArticleRow newItem) {
    return this.equals(newItem);
  }

  public boolean areItemsTheSame(ArticleRow that) {
    if (this.type() == that.type()) {
      if (this.isArticle()) {
        return this.article().id() == that.article().id();
      }
      return true;
    }
    return false;
  }
}
