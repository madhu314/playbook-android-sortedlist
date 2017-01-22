package is.uncommon.playbook.sortedlist.part2;

import is.uncommon.playbook.sortedlist.Article;
import java.util.ArrayList;

public class SortedListSameAddHasNoEffectAdapter extends SortedListShouldBeSortedAdapter {
  public SortedListSameAddHasNoEffectAdapter(ArrayList<Article> articles) {
    super(articles);
  }

  public void addFirstItem() {
    articleSortedList.add(articleSortedList.get(0).dupe());
  }
}
