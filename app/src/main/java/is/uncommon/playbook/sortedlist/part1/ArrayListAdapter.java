package is.uncommon.playbook.sortedlist.part1;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;

public class ArrayListAdapter extends IntegerListAdapter {
  private ArrayList<Integer> integers = new ArrayList<>();

  @Override protected void addInteger(Integer integer) {
    int itemExistence = Collections.binarySearch(integers, integer);
    if (itemExistence >= 0) {
      return;
    }
    if (integers.isEmpty()) {
      integers.add(integer);
      notifyItemInserted(0);
    } else if (integer >= integers.get(integers.size() - 1)) {
      integers.add(integer);
      notifyItemInserted(integers.size() - 1);
    } else {
      int positionToAdd = -1;
      for (int i = 0; i < integers.size(); i++) {
        if (integers.get(i).intValue() >= integer.intValue()) {
          positionToAdd = i;
          break;
        }
      }
      if (positionToAdd >= 0) {
        integers.add(positionToAdd, integer);
        notifyItemInserted(positionToAdd);
      }
    }
  }

  @Override protected void removeInteger(Integer integer) {
    int positionRemove = Collections.binarySearch(integers, integer);
    if (positionRemove >= 0) {
      integers.remove(positionRemove);
      notifyItemRemoved(positionRemove);
    }
  }

  @Override public IntegerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return IntegerListItemViewHolder.create(parent);
  }

  @Override public void onBindViewHolder(IntegerListItemViewHolder holder, int position) {
    holder.bindTo(integers.get(position));
  }

  @Override public int getItemCount() {
    return integers.size();
  }
}
