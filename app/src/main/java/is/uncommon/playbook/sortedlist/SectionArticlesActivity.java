package is.uncommon.playbook.sortedlist;

import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.auto.value.AutoValue;
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

public class SectionArticlesActivity extends AppCompatActivity {
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
          .add(horizontalContainer.getId(),
              BasicSortedArticlesActivity.HorizontalArticlesFragment.create(), HORIZONTAL)
          .commit();
    } else {
      getHorizontalArticlesFragment().eventSubject(eventSubject);
    }

    if (getArticleGridFragment() == null) {
      getFragmentManager().beginTransaction()
          .add(gridContainer.getId(), SectionArticlesFragment.create(), GRID)
          .commit();
    } else {
      getArticleGridFragment().eventSubject(eventSubject);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onAttachFragment(Fragment fragment) {
    super.onAttachFragment(fragment);
    if (fragment instanceof BasicSortedArticlesActivity.ArticleRecyclerFragment) {
      ((BasicSortedArticlesActivity.ArticleRecyclerFragment) fragment).eventSubject(eventSubject);
    } else if (fragment instanceof SectionArticlesFragment) {
      ((SectionArticlesFragment) fragment).eventSubject(eventSubject);
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
    if (BasicSortedArticlesActivity.HorizontalArticlesFragment.ITEM_CLICKED.equals(eventName)) {
      Article article = (Article) data;
      getHorizontalArticlesFragment().removeArticle(article.dupe());
      getArticleGridFragment().addArticle(article.dupe());
    } else if (SectionArticlesFragment.ITEM_CLICKED.equals(eventName)) {
      SectionArticle sectionArticle = (SectionArticle) data;
      if (((SectionArticle) data).isArticle()) {
        getArticleGridFragment().removeArticle(((SectionArticle) data).article());
        if (sectionArticle.isArticle()) {
          getHorizontalArticlesFragment().addArticle(sectionArticle.article().dupe());
        }
      }
    }
  }

  @Override protected void onDestroy() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
    super.onDestroy();
  }

  public BasicSortedArticlesActivity.HorizontalArticlesFragment getHorizontalArticlesFragment() {
    return (BasicSortedArticlesActivity.HorizontalArticlesFragment) getFragmentManager().findFragmentByTag(
        HORIZONTAL);
  }

  public SectionArticlesFragment getArticleGridFragment() {
    return (SectionArticlesFragment) getFragmentManager().findFragmentByTag(GRID);
  }

  public static class SectionArticlesFragment extends Fragment {
    private static String TAG = SectionArticlesFragment.class.getSimpleName();
    public static String ITEM_CLICKED = TAG + ".itemClicked";
    int spanCount = 3;
    private View rootView;
    private Unbinder unbinder;
    private SectionArticlesAdapter adapter;
    private SectionArticleDataset dataset;
    @BindView(R.id.recycler) RecyclerView recyclerView;
    private PublishSubject<Pair<String, Object>> eventSubject;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
      rootView = inflater.inflate(R.layout.full_content_recycler, null, true);
      unbinder = ButterKnife.bind(this, rootView);
      return rootView;
    }

    public void addArticle(Article article) {
      dataset.add(article);
    }

    public void removeArticle(Article article) {
      dataset.remove(article);
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
      adapter.sectionArticleDataset(dataset);
      recyclerView.setAdapter(adapter);
      if (eventSubject != null) {
        adapter.eventSubject(eventSubject);
      }
    }

    protected SectionArticlesAdapter createNewAdapter() {
      return new SectionArticlesAdapter(spanCount);
    }

    protected SectionArticleDataset createDataset(SectionArticlesAdapter adapter) {
      return new SectionArticleDataset(recyclerView, adapter);
    }

    protected void setupRecycler() {
      GridLayoutManager gridManager = new GridLayoutManager(recyclerView.getContext(), spanCount);
      gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override public int getSpanSize(int position) {
          return dataset.itemAt(position).isCategory() ? spanCount : 1;
        }
      });
      recyclerView.setLayoutManager(gridManager);
      recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
          int position = parent.getChildAdapterPosition(view);
          int offset =
              parent.getResources().getDimensionPixelSize(R.dimen.grid_recycler_divider_width);
          if (position > 0 && dataset.itemAt(position).isArticle()) {
            int categoryIndex =
                dataset.indexOfCategoryForArticle(dataset.itemAt(position).article());
            int relativePosition = position - categoryIndex - 1;
            int column = relativePosition % spanCount; // item column
            int row = relativePosition / spanCount;

            outRect.top = offset;
            if (row != 0) {
              outRect.bottom = offset;
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
          } else if (position > 0 && dataset.itemAt(position).isCategory()) {
            if (position != 0) {
              outRect.top = offset;
            }
          }
        }
      });
    }

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

    public static SectionArticlesFragment create() {
      return new SectionArticlesFragment();
    }
  }

  public static class SectionArticlesAdapter
      extends RecyclerView.Adapter<SectionArticlesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 01;
    private static final int VIEW_TYPE_ARTICLE = 02;
    private final int spanCount;

    private SectionArticleDataset sectionArticleDataset;
    PublishSubject<Pair<String, Object>> eventSubject;

    public SectionArticlesAdapter(int spanCount) {
      this.spanCount = spanCount;
    }

    public void sectionArticleDataset(SectionArticleDataset sectionArticleDataset) {
      this.sectionArticleDataset = sectionArticleDataset;
    }

    @Override public int getItemViewType(int position) {
      SectionArticle sectionArticle = sectionArticleDataset.itemAt(position);
      if (sectionArticle.isCategory()) {
        return VIEW_TYPE_HEADER;
      } else if (sectionArticle.isArticle()) {
        return VIEW_TYPE_ARTICLE;
      }
      return -1;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      switch (viewType) {
        case VIEW_TYPE_HEADER:
          return HeaderViewHolder.create(parent);
        case VIEW_TYPE_ARTICLE:
          return ArticleViewHolder.create(parent, spanCount);
      }
      return null;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.bindTo(sectionArticleDataset.itemAt(position), this);
    }

    @Override public int getItemCount() {
      return sectionArticleDataset == null ? 0 : sectionArticleDataset.size();
    }

    public void eventSubject(PublishSubject<Pair<String, Object>> eventSubject) {
      this.eventSubject = eventSubject;
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

      public ViewHolder(View itemView) {
        super(itemView);
      }

      public abstract void bindTo(final SectionArticle article,
          final SectionArticlesAdapter adapter);
    }

    public static class ArticleViewHolder extends ViewHolder {
      public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMM. d");

      private final TextView contentView;
      private final TextView dateView;
      private final TextView authorView;
      private final TextView categoryView;

      public ArticleViewHolder(View itemView) {
        super(itemView);
        contentView = (TextView) itemView.findViewById(R.id.content);
        dateView = (TextView) itemView.findViewById(R.id.date);
        authorView = (TextView) itemView.findViewById(R.id.author);
        categoryView = (TextView) itemView.findViewById(R.id.category);
      }

      public static ArticleViewHolder create(ViewGroup parent, int spanCount) {
        ViewGroup viewgroup = (ViewGroup) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_article, parent, false);
        int offset =
            parent.getResources().getDimensionPixelSize(R.dimen.grid_recycler_divider_width);
        int width = Math.round((float) parent.getWidth() / (float) spanCount) - 2 * offset;
        int height = width;
        ViewGroup.LayoutParams params = viewgroup.getLayoutParams();
        params.width = width;
        params.height = height;
        viewgroup.setLayoutParams(params);
        return new ArticleViewHolder(viewgroup);
      }

      @Override public void bindTo(final SectionArticle sectionArticle,
          final SectionArticlesAdapter adapter) {
        Article article = sectionArticle.article();
        contentView.setText(article.content());
        DateTime dateTime = new DateTime(article.publishedTime());
        dateView.setText(dateTime.toString(dateTimeFormatter));
        authorView.setText("By " + article.author());
        categoryView.setText(article.category());
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            adapter.handleItemClick(sectionArticle);
          }
        });
      }
    }

    public static class HeaderViewHolder extends ViewHolder {
      private final TextView headerView;

      public HeaderViewHolder(View itemView) {
        super(itemView);
        headerView = (TextView) itemView.findViewById(R.id.text);
      }

      public static HeaderViewHolder create(ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_header, parent, false);
        return new HeaderViewHolder(view);
      }

      @Override
      public void bindTo(final SectionArticle article, final SectionArticlesAdapter adapter) {
        headerView.setText(article.category());
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            adapter.handleItemClick(article);
          }
        });
      }
    }

    protected void handleItemClick(SectionArticle sectionArticle) {
      if (eventSubject != null && sectionArticle.isArticle()) {
        eventSubject.onNext(
            Pair.create(SectionArticlesFragment.ITEM_CLICKED, (Object) sectionArticle));
      }
    }
  }

  @AutoValue public static abstract class SectionArticle {
    @Nullable public abstract Article article();

    @Nullable public abstract String category();

    public static SectionArticle createCategory(Article article) {
      return new AutoValue_SectionArticlesActivity_SectionArticle(null, article.category());
    }

    public static SectionArticle createArticle(Article article) {
      return new AutoValue_SectionArticlesActivity_SectionArticle(article, null);
    }

    public boolean isCategory() {
      return article() == null;
    }

    public boolean isArticle() {
      return category() == null;
    }

    public int compare(SectionArticle that) {
      if (this.isCategory() && that.isCategory()) {
        return this.category().compareToIgnoreCase(that.category());
      } else if (this.isCategory() && that.isArticle()) {
        if (this.category().equalsIgnoreCase(that.article().category())) {
          return -1;
        } else {
          return this.category().compareToIgnoreCase(that.article().category());
        }
      } else if (this.isArticle() && that.isCategory()) {
        if (this.article().category().equalsIgnoreCase(that.category())) {
          return 1;
        } else {
          return this.article().category().compareToIgnoreCase(that.category());
        }
      } else {
        if (this.article().category().equalsIgnoreCase(that.article().category())) {
          return this.article().compare(that.article());
        } else {
          return this.article().category().compareToIgnoreCase(that.article().category());
        }
      }
    }

    public boolean areContentsTheSame(SectionArticle that) {
      return this.equals(that);
    }

    public boolean areItemsTheSame(SectionArticle that) {
      if (this.isCategory() && that.isCategory()) {
        return this.category().equalsIgnoreCase(that.category());
      } else if (this.isArticle() && that.isArticle()) {
        return this.article().id() == that.article().id();
      }
      return false;
    }
  }

  public static class SectionArticleDataset {

    SortedList<SectionArticle> sortedList = null;

    public SectionArticleDataset(final RecyclerView recyclerView,
        final RecyclerView.Adapter adapter) {
      this.sortedList = new SortedList<>(SectionArticle.class,
          new SortedList.BatchedCallback<>(new SortedListAdapterCallback<SectionArticle>(adapter) {

            @Override public int compare(SectionArticle item1, SectionArticle item2) {
              return item1.compare(item2);
            }

            @Override
            public boolean areContentsTheSame(SectionArticle oldItem, SectionArticle newItem) {
              return oldItem.areContentsTheSame(newItem);
            }

            @Override public boolean areItemsTheSame(SectionArticle item1, SectionArticle item2) {
              return item1.areItemsTheSame(item2);
            }

            @Override public void onInserted(int position, int count) {
              super.onInserted(position, count);
              recyclerView.scrollToPosition(position);
            }
          }));
    }

    public int size() {
      return sortedList.size();
    }

    public SectionArticle itemAt(int position) {
      return sortedList.get(position);
    }

    public void remove(Article article) {
      SectionArticle sectionArticle = SectionArticle.createArticle(article);
      sortedList.beginBatchedUpdates();
      sortedList.remove(sectionArticle);
      int categoryCount = 0;
      for (int i = 0; i < sortedList.size(); i++) {
        SectionArticle sectionArticleTemp = itemAt(i);
        if (sectionArticleTemp.isArticle() && sectionArticleTemp.article()
            .category()
            .equalsIgnoreCase(article.category())) {
          categoryCount++;
        }
      }
      if (categoryCount < 1) {
        sortedList.remove(SectionArticle.createCategory(sectionArticle.article()));
      }
      sortedList.endBatchedUpdates();
    }

    public void add(SectionArticle article) {
      sortedList.beginBatchedUpdates();
      sortedList.add(article);
      sortedList.endBatchedUpdates();
    }

    public void add(Article article) {
      sortedList.beginBatchedUpdates();
      sortedList.add(SectionArticle.createCategory(article));
      sortedList.add(SectionArticle.createArticle(article));
      sortedList.endBatchedUpdates();
    }

    public int indexOfCategoryForArticle(Article article) {
      return sortedList.indexOf(SectionArticle.createCategory(article));
    }
  }
}
