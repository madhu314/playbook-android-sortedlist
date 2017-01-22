package is.uncommon.playbook.sortedlist;

import android.support.v4.util.Pair;
import android.support.v7.util.SortedList;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SortedListCallbackUnitTest {

  @Rule public SortedListTestSetup fixture = new SortedListTestSetup();

  @Test public void testAddShouldSortListIrrespectiveOfOrder() throws Exception {

    addArticles();

    for (int i = 0; i < fixture.orderedArticleList().size(); i++) {
      assertThat(fixture.sortedList().get(i)).isEqualTo(fixture.orderedArticleList().get(i));
    }
  }

  private void addArticles() {
    assertThat(fixture.sortedList().size()).isEqualTo(0);
    for (Article article : fixture.shuffledArticles()) {
      fixture.sortedList().add(article.dupe());
    }
    assertThat(fixture.sortedList().size()).isEqualTo(fixture.orderedArticleList().size());
  }

  @Test public void testAdditionOfSameObjectShouldNotChangeSortedList() throws Exception {
    assertThat(fixture.sortedList().size()).isEqualTo(0);

    Article article = fixture.orderedArticleList()
        .get(Article.Utils.randomWithRange(0, fixture.orderedArticleList().size() - 1));

    fixture.sortedList().add(article);
    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(1);

    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(0);

    fixture.callbackRecorder().clear();

    assertThat(fixture.sortedList().size()).isEqualTo(1);
    for (int i = 0; i < Article.Utils.randomWithRange(1, 10); i++) {
      fixture.sortedList().add(article.dupe());
    }
    assertThat(fixture.sortedList().size()).isEqualTo(1);

    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(0);
  }

  @Test public void testInsertions() throws Exception {
    assertThat(fixture.sortedList().size()).isEqualTo(0);
    for (int i = 0; i < fixture.shuffledArticles().size(); i++) {
      Article articleToAdd = fixture.shuffledArticles().get(i).dupe();
      int positionTobeInserted = -1;
      for (int j = 0; j < fixture.sortedList().size(); j++) {
        Article jth = fixture.sortedList().get(j);
        if (jth.compare(articleToAdd) >= 0) {
          positionTobeInserted = j;
          break;
        }
      }
      if (positionTobeInserted == -1) {
        positionTobeInserted = fixture.sortedList().size();
      }

      fixture.sortedList().add(articleToAdd);
      assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(i + 1);
      Pair<Integer, Integer> insertionPair = fixture.callbackRecorder().insertions().get(i);
      assertThat(insertionPair.first).isEqualTo(positionTobeInserted);
    }

    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(0);
  }

  @Test public void testChanges() throws Exception {
    assertThat(fixture.sortedList().size()).isEqualTo(0);
    addArticles();
    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(
        fixture.shuffledArticles().size());
    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(0);

    fixture.callbackRecorder().clear();

    int index = Article.Utils.randomWithRange(0, fixture.sortedList().size() - 1);
    Article article = fixture.sortedList().get(index);
    Article contentChanged =
        article.toBuilder().content(Article.Utils.shuffleString(article.content())).build();
    fixture.sortedList().add(contentChanged);
    Article changed = fixture.sortedList().get(index);
    assertThat(changed.content()).isEqualTo(contentChanged.content());
    assertThat(changed.content()).isNotEqualTo(article.content());

    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);

    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(1);
    assertThat(fixture.callbackRecorder().changes().get(0).first).isEqualTo(index);
    assertThat(fixture.callbackRecorder().changes().get(0).second).isEqualTo(1);
  }

  @Test public void testDeletions() throws Exception {
    assertThat(fixture.sortedList().size()).isEqualTo(0);
    addArticles();

    fixture.callbackRecorder().clear();

    int index = Article.Utils.randomWithRange(0, fixture.sortedList().size() - 1);
    Article articleToDelete = fixture.sortedList().get(index).dupe();

    boolean deleted = fixture.sortedList().remove(articleToDelete);
    assertThat(deleted).isTrue();

    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().changes().size()).isEqualTo(0);

    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(1);

    assertThat(fixture.callbackRecorder().deletions().get(0).first).isEqualTo(index);
    assertThat(fixture.callbackRecorder().deletions().get(0).second).isEqualTo(1);
  }

  @Test public void testMoves() throws Exception {
    assertThat(fixture.sortedList().size()).isEqualTo(0);
    addArticles();

    fixture.callbackRecorder().clear();

    int index1 = 2;
    Article articleOne = fixture.sortedList().get(index1);
    assertThat(fixture.sortedList().indexOf(articleOne)).isEqualTo(index1);

    fixture.sortedList().updateItemAt(index1, articleOne.toBuilder().publishedTime(1).build());

    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(0);
    assertThat(fixture.callbackRecorder().deletions().size()).isEqualTo(0);

    assertThat(fixture.callbackRecorder().moves().size()).isEqualTo(1);

    assertThat(fixture.callbackRecorder().moves().get(0).first).isEqualTo(2);
    assertThat(fixture.callbackRecorder().moves().get(0).second).isEqualTo(0);

    assertThat(fixture.callbackRecorder().changes().get(0).first).isEqualTo(2);
  }

  @Test public void testBatchedCallbacks() throws Exception {
    SortedList<Article> articleSortedList = new SortedList<>(Article.class,
        new SortedList.BatchedCallback<>(fixture.callbackRecorder()));
    articleSortedList.beginBatchedUpdates();
    for (int i = 0; i < fixture.shuffledArticles().size(); i++) {
      Article articleToAdd = fixture.shuffledArticles().get(i).dupe();
      articleSortedList.add(articleToAdd);
      assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(0);
    }
    articleSortedList.endBatchedUpdates();
    assertThat(fixture.callbackRecorder().insertions().size()).isEqualTo(1);
    assertThat(fixture.callbackRecorder().insertions().get(0).first).isEqualTo(0);
    assertThat(fixture.callbackRecorder().insertions().get(0).second).isEqualTo(
        fixture.shuffledArticles().size());
  }
}

