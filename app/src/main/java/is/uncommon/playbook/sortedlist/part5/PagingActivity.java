package is.uncommon.playbook.sortedlist.part5;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.squareup.moshi.Moshi;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import is.uncommon.playbook.sortedlist.Article;
import is.uncommon.playbook.sortedlist.R;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

public class PagingActivity extends AppCompatActivity {

  private Unbinder unbinder;
  private ArticleService articleService;
  private ArticleServer server;
  private PublishSubject<Page> pagedSubject = PublishSubject.create();
  private CompositeDisposable compositeDisposable = new CompositeDisposable();

  @BindView(R.id.loadingServerContainer) ViewGroup loadingServerContainer;
  @BindView(R.id.recycler) RecyclerView recycler;
  private ArticleRowAdapter adapter;
  private ArticleRowDataset dataset;
  private Page currPage;
  private boolean isPagingInProgress;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_paging);
    setupRetrofit();
    unbinder = ButterKnife.bind(this);
    recycler.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            recycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            setupRecycler();
          }
        });
  }

  private void setupRecycler() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycler.setLayoutManager(layoutManager);
    adapter = new ArticleRowAdapter();
    dataset = new ArticleRowDataset(recycler, adapter);
    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int position = layoutManager.findLastVisibleItemPosition();
        if (dataset.get(position).isPagingIndicator()) {
          if (!isPagingInProgress) {
            currPage = currPage.next();
            Timber.d("Kick off new page request for %s", currPage);
            isPagingInProgress = true;
            pagedSubject.onNext(currPage);
          } else {
            Timber.d("Paging in progress for page %s", currPage);
          }
        }
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_start_over, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem startOver = null;
    for (int i = 0; i < menu.size(); i++) {
      if (menu.getItem(i).getItemId() == R.id.menu_start_over) {
        startOver = menu.getItem(i);
        break;
      }
    }
    if (dataset.hasPagingIndicator()) {
      startOver.setEnabled(false);
    } else {
      startOver.setEnabled(true);
    }
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_start_over) {
      startFirstPage();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupRetrofit() {
    Single<ArticleService> single = Single.fromCallable(new Callable<ArticleService>() {
      @Override public ArticleService call() throws Exception {
        server = ArticleServer.create();
        server.start();
        Moshi moshi = new Moshi.Builder().add(ArticleJsonFactory.create()).build();
        Retrofit retrofit =
            new Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(server.baseUrl())
                .build();
        return retrofit.create(ArticleService.class);
      }
    });

    single.subscribeOn(Schedulers.io())
        .delay(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<ArticleService>() {
          @Override public void onSuccess(ArticleService service) {
            Timber.d("Article service is created");
            articleService = service;
            handleServerReady();
          }

          @Override public void onError(Throwable throwable) {
            Timber.d(throwable, "Article service creation failed");
          }
        });
  }

  private void handleServerReady() {
    pagedSubject.flatMapSingle(new Function<Page, SingleSource<Pair<Page, List<Article>>>>() {

      @Override public SingleSource<Pair<Page, List<Article>>> apply(final Page page)
          throws Exception {
        return articleService.getArticles(page.number(), page.size())
            .subscribeOn(Schedulers.io())
            .map(new Function<List<Article>, Pair<Page, List<Article>>>() {
              @Override public Pair<Page, List<Article>> apply(List<Article> articles)
                  throws Exception {
                return Pair.create(page, articles);
              }
            });
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Pair<Page, List<Article>>>() {
          @Override public void onSubscribe(Disposable disposable) {
            Timber.d("Subscribed");
            compositeDisposable.add(disposable);
          }

          @Override public void onNext(Pair<Page, List<Article>> pageListPair) {
            Timber.d("Received data for page %s", pageListPair.first);
            Timber.d("Article size -- %d", pageListPair.second.size());
            isPagingInProgress = false;
            dataset.addArticles(pageListPair.first, pageListPair.second);
            invalidateOptionsMenu();
          }

          @Override public void onError(Throwable throwable) {
            Timber.e(throwable, "Received error");
          }

          @Override public void onComplete() {
            Timber.e("On observable complete");
          }
        });
    loadingServerContainer.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        loadingServerContainer.setVisibility(View.GONE);
        startFirstPage();
      }
    });
  }

  private void startFirstPage() {
    Timber.d("Resetting data and starting again");
    dataset.reset();
    adapter.dataset(dataset);
    recycler.setAdapter(adapter);
    currPage = Page.start();
    invalidateOptionsMenu();
    pagedSubject.onNext(currPage);
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    if (server != null) {
      server.shutdown();
    }
    if (!compositeDisposable.isDisposed()) {
      compositeDisposable.dispose();
    }
    super.onDestroy();
  }
}
