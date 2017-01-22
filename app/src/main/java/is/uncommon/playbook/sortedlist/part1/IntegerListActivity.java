package is.uncommon.playbook.sortedlist.part1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import is.uncommon.playbook.sortedlist.R;
import java.util.ArrayList;

public class IntegerListActivity extends AppCompatActivity {

  @BindView(R.id.recycler) RecyclerView recyclerView;
  @BindView(R.id.addButton) Button addButton;
  @BindView(R.id.removeButton) Button removeButton;

  private Unbinder unbinder;
  private IntegerListAdapter listAdapter;
  private ArrayList<Integer> integerAdditionList = new ArrayList<>();
  private ArrayList<Integer> integerRemovalList = new ArrayList<>();
  private Integer integerToAdd;
  private Integer integerToRemove;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_integer_list);
    unbinder = ButterKnife.bind(this);
    setupSeeds();
    setupRecycler();
    randomizeAddButton();
  }

  private void setupSeeds() {
    for (int i = 0; i < 5; i++) {
      integerAdditionList.add(Integer.valueOf(i));
    }
  }

  private void randomizeRemoveButton() {
    integerToRemove = randIntegerToRemove();
    if (integerToRemove >= 0) {
      removeButton.setText("Remove " + integerToRemove.intValue());
    } else {
      removeButton.setText("Remove");
    }
  }

  private void randomizeAddButton() {
    integerToAdd = randIntegerToAdd();
    if (integerToAdd >= 0) {
      addButton.setText("Add " + integerToAdd.intValue());
    } else {
      addButton.setText("Add");
    }
  }

  private void setupRecycler() {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    listAdapter = new ArrayListAdapter();
    recyclerView.setAdapter(listAdapter);
  }

  @OnClick(R.id.addButton) public void onAddClick() {
    if (integerToAdd >= 0) {
      listAdapter.addInteger(integerToAdd);
      integerRemovalList.add(integerToAdd);
      integerAdditionList.remove(integerToAdd);
      randomizeAddButton();
      randomizeRemoveButton();
    }
  }

  @OnClick(R.id.removeButton) public void onRemoveClick() {
    if (integerToRemove >= 0) {
      listAdapter.removeInteger(integerToRemove);
      integerAdditionList.add(integerToRemove);
      integerRemovalList.remove(integerToRemove);
      randomizeRemoveButton();
      randomizeAddButton();
    }
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }

  private Integer randIntegerToAdd() {
    if (integerAdditionList.size() > 0) {
      int position = (int) (Math.random() * 10) % integerAdditionList.size();
      return integerAdditionList.get(position);
    }
    return -1;
  }

  private Integer randIntegerToRemove() {
    if (integerRemovalList.size() > 0) {
      int position = (int) (Math.random() * 10) % integerRemovalList.size();
      return integerRemovalList.get(position);
    }
    return -1;
  }
}
