package is.uncommon.playbook.sortedlist.part1;

import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;

public class ArrayListAdapter extends IntegerListAdapter {
  private ArrayList<Integer> integers = new ArrayList<>();

  @Override protected void addInteger(Integer integer) {
    int itemPosition = Collections.binarySearch(integers, integer);
    if (itemPosition >= 0) {
      return;
    }

    integers.add(-itemPosition - 1, integer);
    notifyItemInserted(-itemPosition - 1);
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
