package is.uncommon.playbook.sortedlist;

import android.support.v4.util.Pair;
import android.support.v7.util.SortedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.UTCProvider;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by madhu on 30/11/16.
 */

public class SortedListTestSetup implements TestRule {

  private SortedList<Article> sortedList;
  private SortedListCallbackRecorder callbackRecorder;
  private ArrayList<Article> orderedArticleList;
  private ArrayList<Article> shuffledArticles;

  public SortedList<Article> sortedList() {
    return sortedList;
  }

  public SortedListCallbackRecorder callbackRecorder() {
    return callbackRecorder;
  }

  public ArrayList<Article> orderedArticleList() {
    return orderedArticleList;
  }

  public ArrayList<Article> shuffledArticles() {
    return shuffledArticles;
  }

  public static class SortedListCallbackRecorder extends SortedList.Callback<Article> {
    private List<Pair<Integer, Integer>> insertions = new ArrayList<>();
    private List<Pair<Integer, Integer>> deletions = new ArrayList<>();
    private List<Pair<Integer, Integer>> moves = new ArrayList<>();
    private List<Pair<Integer, Integer>> changes = new ArrayList<>();

    @Override public int compare(Article o1, Article o2) {
      return o1.compare(o2);
    }

    @Override public void onChanged(int position, int count) {
      changes.add(Pair.create(position, count));
    }

    @Override public boolean areContentsTheSame(Article oldItem, Article newItem) {
      return oldItem.areContentsTheSame(newItem);
    }

    @Override public boolean areItemsTheSame(Article item1, Article item2) {
      return item1.areItemsTheSame(item2);
    }

    @Override public void onInserted(int position, int count) {
      insertions.add(Pair.create(position, count));
    }

    @Override public void onRemoved(int position, int count) {
      deletions.add(Pair.create(position, count));
    }

    @Override public void onMoved(int fromPosition, int toPosition) {
      moves.add(Pair.create(fromPosition, toPosition));
    }

    public void clear() {
      insertions.clear();
      deletions.clear();
      moves.clear();
      changes.clear();
    }

    public List<Pair<Integer, Integer>> insertions() {
      return insertions;
    }

    public List<Pair<Integer, Integer>> deletions() {
      return deletions;
    }

    public List<Pair<Integer, Integer>> moves() {
      return moves;
    }

    public List<Pair<Integer, Integer>> changes() {
      return changes;
    }
  }

  public SortedListTestSetup() {
    DateTimeZone.setProvider(new UTCProvider());
    callbackRecorder = new SortedListCallbackRecorder();
    sortedList = new SortedList<>(Article.class, callbackRecorder);
    Article past = Article.past();
    Article prePast = Article.past();
    Article current = Article.current();
    Article future = Article.future();
    Article postFuture = Article.future();
    orderedArticleList = new ArrayList<>();
    orderedArticleList.add(prePast);
    orderedArticleList.add(past);
    orderedArticleList.add(current);
    orderedArticleList.add(future);
    orderedArticleList.add(postFuture);

    shuffledArticles = new ArrayList<>();
    for (Article article : orderedArticleList) {
      shuffledArticles.add(article.dupe());
    }
    Collections.shuffle(shuffledArticles);
  }

  @Override public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        base.evaluate();
      }
    };
  }
}
