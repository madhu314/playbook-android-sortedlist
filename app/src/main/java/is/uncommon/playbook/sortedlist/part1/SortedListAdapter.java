package is.uncommon.playbook.sortedlist.part1;

import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.ViewGroup;

public class SortedListAdapter extends IntegerListAdapter {
  private SortedList<Integer> sortedList;

  public SortedListAdapter() {
    this.sortedList = new SortedList<>(Integer.class, new SortedListAdapterCallback<Integer>(this) {
      @Override public int compare(Integer item1, Integer item2) {
        return item1.compareTo(item2);
      }

      @Override public boolean areContentsTheSame(Integer oldItem, Integer newItem) {
        return oldItem.equals(newItem);
      }

      @Override public boolean areItemsTheSame(Integer item1, Integer item2) {
        return item1.intValue() == item2.intValue();
      }
    });
    ;
  }

  @Override protected void addInteger(Integer integer) {
    sortedList.add(integer);
  }

  @Override protected void removeInteger(Integer integer) {
    sortedList.remove(integer);
  }

  @Override public IntegerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return IntegerListItemViewHolder.create(parent);
  }

  @Override public void onBindViewHolder(IntegerListItemViewHolder holder, int position) {
    holder.bindTo(sortedList.get(position));
  }

  @Override public int getItemCount() {
    return sortedList.size();
  }
}
