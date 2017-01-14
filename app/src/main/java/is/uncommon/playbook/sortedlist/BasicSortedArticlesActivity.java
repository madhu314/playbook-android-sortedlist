package is.uncommon.playbook.sortedlist;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class BasicSortedArticlesActivity extends AppCompatActivity {
  private static final String HORIZONTAL = "horizontal";
  private static final String GRID = "grid";
  @BindView(R.id.verticalContainer) ViewGroup gridContainer;
  @BindView(R.id.horizontalContainer) ViewGroup horizontalContainer;
  private CompositeSubscription subscription = new CompositeSubscription();
  private PublishSubject<Pair<String, Object>> eventSubject = PublishSubject.create();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_basic_sorted_articles);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ButterKnife.bind(this);
    doSubscriptions();

    if (getHorizontalArticlesFragment() == null) {
      getFragmentManager().beginTransaction()
          .add(horizontalContainer.getId(), HorizontalArticlesFragment.create(), HORIZONTAL)
          .commit();
    } else {
      getHorizontalArticlesFragment().eventSubject(eventSubject);
    }

    if (getArticleGridFragment() == null) {
      getFragmentManager().beginTransaction()
          .add(gridContainer.getId(), ArticleGridFragment.create(), GRID)
          .commit();
    } else {
      getArticleGridFragment().eventSubject(eventSubject);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } return super.onOptionsItemSelected(item);
  }

  @Override public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
    if (fragment instanceof ArticleRecyclerFragment) {
      ((ArticleRecyclerFragment) fragment).eventSubject(eventSubject);
    }
  }

  private void doSubscriptions() {
    Subscription sub = eventSubject.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Pair<String, Object>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            Timber.d(e, "Event subject error occured");
          }

          @Override public void onNext(Pair<String, Object> eventObject) {
            handleEvent(eventObject.first, eventObject.second);
          }
        });
    subscription.add(sub);
  }

  private void handleEvent(String eventName, Object data) {
    if (HorizontalArticlesFragment.ITEM_CLICKED.equals(eventName)) {
      Article article = (Article) data;
      getHorizontalArticlesFragment().removeArticle(article.dupe());
      getArticleGridFragment().addArticle(article.dupe());
    } else if (ArticleGridFragment.ITEM_CLICKED.equals(eventName)) {
      Article article = (Article) data;
      getArticleGridFragment().removeArticle(article.dupe());
      getHorizontalArticlesFragment().addArticle(article.dupe());
    }
  }

  @Override protected void onDestroy() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
    super.onDestroy();
  }

  public HorizontalArticlesFragment getHorizontalArticlesFragment() {
    return (HorizontalArticlesFragment) getFragmentManager().findFragmentByTag(HORIZONTAL);
  }

  public ArticleGridFragment getArticleGridFragment() {
    return (ArticleGridFragment) getFragmentManager().findFragmentByTag(GRID);
  }

  public static class HorizontalArticlesFragment extends ArticleRecyclerFragment {
    public static final String TAG = HorizontalArticlesFragment.class.getSimpleName();
    public static final String ITEM_CLICKED = TAG + ".itemClicked";

    public static final HorizontalArticlesFragment create() {
      return new HorizontalArticlesFragment();
    }

    @Override protected ArticlesAdapter createNewAdapter() {
      return new ArticlesAdapter() {
        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          ViewGroup viewgroup = ViewHolder.create(parent);
          int offset = parent.getResources()
              .getDimensionPixelSize(R.dimen.horizontal_recycler_divider_width);
          int width = parent.getHeight() - 2 * offset;
          int height = width;
          ViewGroup.LayoutParams params = viewgroup.getLayoutParams();
          params.width = width;
          params.height = height;
          viewgroup.setLayoutParams(params);
          return new ViewHolder(viewgroup);
        }

        @Override protected void handleItemClick(Article article) {
          if (eventSubject != null) {
            eventSubject.onNext(Pair.create(ITEM_CLICKED, (Object) article));
          }
        }
      };
    }

    @Override protected ArticleDataset createDataset(ArticlesAdapter adapter) {
      ArticleDataset dataset = new ArticleDataset(adapter);
      dataset.generateRandom();
      return dataset;
    }

    @Override protected void setupRecycler() {
      recyclerView.setLayoutManager(
          new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL,
              false));
      recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
          super.getItemOffsets(outRect, view, parent, state);
          float offsetFloat = parent.getResources()
              .getDimensionPixelSize(R.dimen.horizontal_recycler_divider_width);
          int halfOffset = Math.round(offsetFloat / 2f);
          int offset = Math.round(offsetFloat);
          int position = parent.getChildAdapterPosition(view);
          if (position == 0 && parent.getAdapter().getItemCount() == 1) {
            outRect.set(offset, offset, offset, offset);
          } else if (position == 0) {
            outRect.set(offset, offset, halfOffset, offset);
          } else if (position > 0 && position < parent.getAdapter().getItemCount() - 1) {
            outRect.set(halfOffset, offset, halfOffset, offset);
          } else if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.set(halfOffset, offset, offset, offset);
          }
        }
      });
    }
  }

  public static class ArticleGridFragment extends ArticleRecyclerFragment {
    public static final String TAG = ArticleGridFragment.class.getSimpleName();
    public static final String ITEM_CLICKED = TAG + ".itemClicked";
    int spanCount = 3;

    public static final ArticleGridFragment create() {
      return new ArticleGridFragment();
    }

    @Override protected ArticlesAdapter createNewAdapter() {
      return new ArticlesAdapter() {
        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          ViewGroup viewgroup = ViewHolder.create(parent);
          int offset =
              parent.getResources().getDimensionPixelSize(R.dimen.grid_recycler_divider_width);
          int width = Math.round((float) parent.getWidth() / (float) spanCount) - 2 * offset;
          int height = width;
          ViewGroup.LayoutParams params = viewgroup.getLayoutParams();
          params.width = width;
          params.height = height;
          viewgroup.setLayoutParams(params);
          return new ViewHolder(viewgroup);
        }

        @Override protected void handleItemClick(Article article) {
          if (eventSubject != null) {
            eventSubject.onNext(Pair.create(ITEM_CLICKED, (Object) article));
          }
        }
      };
    }

    @Override protected ArticleDataset createDataset(ArticlesAdapter adapter) {
      ArticleDataset dataset = new ArticleDataset(adapter);
      return dataset;
    }

    @Override protected void setupRecycler() {
      recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), spanCount));
      recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
          int offset =
              parent.getResources().getDimensionPixelSize(R.dimen.grid_recycler_divider_width);
          int position = parent.getChildAdapterPosition(view); // item position
          int column = position % spanCount; // item column
          int row = position / spanCount;
          int lastRow = parent.getChildAdapterPosition(view) / spanCount;
          if (parent.getChildAdapterPosition(view) % spanCount != 0) {
            lastRow = lastRow + 1;
          }

          outRect.top = offset;
          outRect.bottom = offset;
          if (row == 0) {
            outRect.top = 2 * offset;
          }

          if (column == 0) {
            outRect.left = 2 * offset;
            outRect.right = 0;
          } else if (column == (spanCount - 1)) {
            outRect.left = 0;
            outRect.right = 2 * offset;
          } else {
            outRect.left = offset;
            outRect.right = offset;
          }
        }
      });
    }
  }

  public static abstract class ArticleRecyclerFragment extends Fragment {
    @BindView(R.id.recycler) RecyclerView recyclerView;
    private Unbinder unbinder;
    ArticleDataset dataset;
    private ArticlesAdapter adapter;
    private View rootView;
    private PublishSubject<Pair<String, Object>> eventSubject;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
      rootView = inflater.inflate(R.layout.full_content_recycler, null, true);
      unbinder = ButterKnife.bind(this, rootView);
      return rootView;
    }

    @Override public void onViewCreated(final View view, final Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      view.getViewTreeObserver()
          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
              view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              setupRecycler();
              setupAdapter(savedInstanceState);
            }
          });
    }

    private void setupAdapter(Bundle savedInstanceState) {
      adapter = createNewAdapter();
      dataset = createDataset(adapter);
      adapter.articleDataset(dataset);
      recyclerView.setAdapter(adapter);
      if (eventSubject != null) {
        adapter.eventSubject(eventSubject);
      }
    }

    protected abstract ArticlesAdapter createNewAdapter();

    protected abstract ArticleDataset createDataset(ArticlesAdapter adapter);

    protected abstract void setupRecycler();

    @Override public void onDestroyView() {
      unbinder.unbind();
      super.onDestroyView();
    }

    public void eventSubject(PublishSubject<Pair<String, Object>> eventSubject) {
      this.eventSubject = eventSubject;
      if (adapter != null) {
        adapter.eventSubject(eventSubject);
      }
    }

    public void addArticle(Article article) {
      dataset.add(article);
    }

    public void removeArticle(Article article) {
      dataset.remove(article);
    }
  }

  public static abstract class ArticlesAdapter
      extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private ArticleDataset articleDataset;
    PublishSubject<Pair<String, Object>> eventSubject;

    public ArticlesAdapter() {

    }

    public void articleDataset(ArticleDataset articleDataset) {
      this.articleDataset = articleDataset;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      Article article = articleDataset.getArticle(position);
      holder.bindTo(article, this);
    }

    @Override public int getItemCount() {
      return articleDataset == null ? 0 : articleDataset.size();
    }

    public void eventSubject(PublishSubject<Pair<String, Object>> eventSubject) {
      this.eventSubject = eventSubject;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
      public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMM. d");

      private final TextView contentView;
      private final TextView dateView;
      private final TextView authorView;

      public ViewHolder(View itemView) {
        super(itemView);
        contentView = (TextView) itemView.findViewById(R.id.content);
        dateView = (TextView) itemView.findViewById(R.id.date);
        authorView = (TextView) itemView.findViewById(R.id.author);
      }

      public static ViewGroup create(ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_article, parent, false);
        return view;
      }

      public void bindTo(final Article article, final ArticlesAdapter adapter) {
        contentView.setText(article.content());
        DateTime dateTime = new DateTime(article.publishedTime());
        dateView.setText(dateTime.toString(dateTimeFormatter));
        authorView.setText("By " + article.author());
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            adapter.handleItemClick(article);
          }
        });
      }
    }

    protected abstract void handleItemClick(Article article);
  }

  public static class ArticleDataset {

    private static final int DAYS_PRIOR = 20;
    SortedList<Article> sortedList = null;

    public ArticleDataset(RecyclerView.Adapter adapter) {
      this.sortedList = new SortedList<>(Article.class,
          new SortedList.BatchedCallback<>(new SortedListAdapterCallback<Article>(adapter) {
            @Override public int compare(Article a1, Article a2) {
              return a1.compare(a2);
            }

            @Override public boolean areContentsTheSame(Article oldItem, Article newItem) {
              return oldItem.areContentsTheSame(newItem);
            }

            @Override public boolean areItemsTheSame(Article item1, Article item2) {
              return item1.areItemsTheSame(item2);
            }
          }));
    }

    public void generateRandom() {
      List<Article> articleList = new ArrayList<>();
      for (int i = 0; i < DAYS_PRIOR; i++) {
        articleList.add(
            Article.builder().publishedTime(DateTime.now().minusDays(i).getMillis()).build());
      }
      sortedList.beginBatchedUpdates();
      sortedList.addAll(articleList);
      sortedList.endBatchedUpdates();
    }

    public int size() {
      return sortedList.size();
    }

    public Article getArticle(int position) {
      return sortedList.get(position);
    }

    public void remove(Article article) {
      sortedList.beginBatchedUpdates();
      sortedList.remove(article);
      sortedList.endBatchedUpdates();
    }

    public void add(Article article) {
      sortedList.beginBatchedUpdates();
      sortedList.add(article);
      sortedList.endBatchedUpdates();
    }
  }
}
