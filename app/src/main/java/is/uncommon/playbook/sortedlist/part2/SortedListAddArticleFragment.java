package is.uncommon.playbook.sortedlist.part2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;
import java.util.ArrayList;
import java.util.Collections;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SortedListAddArticleFragment extends Fragment {
  public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMM. d");
  private static final int DAYS_PRIOR = 20;

  @BindView(R.id.linearLayout) LinearLayout linearLayoutContainer;
  @BindView(R.id.recycler) RecyclerView recyclerView;

  private View rootView;
  private Unbinder unbinder;
  private ArrayList<Article> articleList = new ArrayList<>();
  private SortedListAddArticleAdapter adapter;

  public static SortedListAddArticleFragment create() {
    return new SortedListAddArticleFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_sortedlist_add_article, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    setupSeeds();
    linearLayoutContainer.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            linearLayoutContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            setupShuffleContainer();
          }
        });
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    final ArrayList<Article> emptyList = new ArrayList<>();
    adapter = new SortedListAddArticleAdapter(emptyList);
    recyclerView.setAdapter(adapter);
    return rootView;
  }

  private void setupShuffleContainer() {
    linearLayoutContainer.removeAllViews();
    Collections.shuffle(articleList);
    for (final Article article : articleList) {
      View itemView = LayoutInflater.from(getActivity())
          .inflate(R.layout.item_grid_article, linearLayoutContainer, false);
      ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
      layoutParams.width = linearLayoutContainer.getHeight();
      layoutParams.height = linearLayoutContainer.getHeight();
      itemView.setLayoutParams(layoutParams);
      linearLayoutContainer.addView(itemView);
      TextView contentView = (TextView) itemView.findViewById(R.id.content);
      TextView dateView = (TextView) itemView.findViewById(R.id.date);
      TextView authorView = (TextView) itemView.findViewById(R.id.author);
      TextView categoryView = (TextView) itemView.findViewById(R.id.category);

      contentView.setText(article.content());
      DateTime dateTime = new DateTime(article.publishedTime());
      dateView.setText(dateTime.toString(dateTimeFormatter));
      categoryView.setText(article.category());
      authorView.setText("By " + article.author());
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          adapter.addArticle(article);
        }
      });
    }
  }

  private void setupSeeds() {
    for (int i = 0; i < DAYS_PRIOR; i++) {
      articleList.add(
          Article.builder().publishedTime(DateTime.now().minusDays(i).getMillis()).build());
    }
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }
}
