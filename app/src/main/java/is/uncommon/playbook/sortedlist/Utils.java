package is.uncommon.playbook.sortedlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by madhu on 30/11/16.
 */

public class Utils {

  public static int randomWithRange(int min, int max) {
    if (min == max) return min;
    if (max < min) throw new IllegalArgumentException("Max should be greater than min");

    int range = max - min;
    return (int) (Math.random() * range) + min;
  }

  public static String shuffleString(String string) {
    char[] characters = string.toCharArray();
    List<Character> bigChars = new ArrayList<>();
    for (int i = 0; i < characters.length; i++) {
      bigChars.add(characters[i]);
    }
    Collections.shuffle(bigChars);
    for (int i = 0; i < bigChars.size(); i++) {
      characters[i] = bigChars.get(i);
    }
    return String.valueOf(characters);
  }
}
