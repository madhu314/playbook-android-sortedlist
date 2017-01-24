package is.uncommon.playbook.sortedlist.part3;

public interface ActivityBus {
  String HORIZONTAL_ITEM_CLICKED = "horizontalItemClicked";
  String GRID_ITEM_CLICKED = "gridItemClicked";

  void onBusEvent(String eventName, Object data);
}
