package is.uncommon.playbook.sortedlist.part5;

import io.reactivex.Single;
import is.uncommon.playbook.sortedlist.Article;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArticleService {
  @GET("/articles") Single<List<Article>> getArticles(@Query("page") int pageNum,
      @Query("pageSize") int pageSize);
}
