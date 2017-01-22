package is.uncommon.playbook.sortedlist.part1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.uncommon.playbook.sortedlist.R;

public class IntegerListItemViewHolder extends RecyclerView.ViewHolder {
  public IntegerListItemViewHolder(View itemView) {
    super(itemView);
  }

  public void bindTo(Integer integer) {
    TextView tv = (TextView) itemView;
    tv.setText(String.valueOf(integer.intValue()));
  }

  public static final IntegerListItemViewHolder create(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_integer, parent, false);
    return new IntegerListItemViewHolder(view);
  }
}
