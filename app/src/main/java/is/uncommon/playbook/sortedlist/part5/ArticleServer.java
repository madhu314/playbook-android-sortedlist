package is.uncommon.playbook.sortedlist.part5;

import android.net.Uri;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import is.uncommon.playbook.sortedlist.Article;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.joda.time.DateTime;
import timber.log.Timber;

public class ArticleServer extends Dispatcher {

  private MockWebServer mockWebServer;
  private Moshi moshi;
  private ArrayList<Article> articleList;

  public static ArticleServer create() {
    return new ArticleServer();
  }

  private ArticleServer() {
    init();
  }

  @Override public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    Thread.currentThread().sleep(3000);
    Uri uri = Uri.parse("spec://test" + request.getPath());
    String pageStr = uri.getQueryParameter("page");
    String pageSizeStr = uri.getQueryParameter("pageSize");
    List<Article> toSend = new ArrayList<>();
    try {
      int page = Integer.valueOf(pageStr).intValue();
      int pageSize = Integer.valueOf(pageSizeStr).intValue();
      int startIndex = page * pageSize;
      int endIndex = Math.min(startIndex + pageSize, articleList.size());
      toSend = articleList.subList(startIndex, endIndex);
    } catch (NumberFormatException nfe) {
      Timber.e(nfe, "Page or page size is not a number");
    }

    MockResponse response = new MockResponse();
    Type listOfArticles = Types.newParameterizedType(List.class, Article.class);
    JsonAdapter<List<Article>> jsonAdapter = moshi.adapter(listOfArticles);
    response.addHeader("Content-Type", "application/json; charset=utf-8")
        .addHeader("Cache-Control", "no-cache")
        .setBody(jsonAdapter.toJson(toSend));
    return response;
  }

  private void init() {
    mockWebServer = new MockWebServer();
    mockWebServer.setDispatcher(this);
    moshi = new Moshi.Builder().add(ArticleJsonFactory.create()).build();
    articleList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 2; j++) {
        Article article = Article.builder()
            .publishedTime(DateTime.now().minusDays(i).plusHours(j).getMillis())
            .build();
        articleList.add(article);
      }
    }

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 2; j++) {
        Article article = Article.builder()
            .publishedTime(DateTime.now().plusDays(i).plusHours(j).getMillis())
            .build();
        articleList.add(article);
      }
    }

    Collections.sort(articleList, Article.timestampComparator);
  }

  public void start() {
    try {
      mockWebServer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    try {
      mockWebServer.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public HttpUrl baseUrl() {
    return mockWebServer.url("");
  }
}
