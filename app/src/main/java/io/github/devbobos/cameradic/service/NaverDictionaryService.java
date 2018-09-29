package io.github.devbobos.cameradic.service;

import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.model.naver.NaverDictionaryResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class NaverDictionaryService
{
    private static NaverDictionaryService instance = new NaverDictionaryService();
    private static Retrofit retrofit;
    private static NaverDictionaryRequest request;
    private final int DEFAULT_DISPLAY = 1;
    private final int DEFAULT_START = 1;
    private final String DEFAULT_SORT = "sim";
    private NaverDictionaryService() { }

    public static NaverDictionaryService getInstance()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SystemConstant.URL_NAVER_DICTIONARY)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if(request == null)
        {
            request = retrofit.create(NaverDictionaryRequest.class);
        }
        return instance;
    }

    private interface NaverDictionaryRequest
    {
        @Headers({
                         "X-Naver-Client-Id: "+SystemConstant.CLIENT_ID_NAVER_DICTIONARY,
                         "X-Naver-Client-Secret: "+SystemConstant.SECRET_NAVER_DICTIONARY
                 })
        @GET("encyc.json")
        Call<NaverDictionaryResponse> getNaverDictionaryResponse(@Query("query") String query, @Query("integer") Integer display, @Query("start") Integer start, @Query("sort") String sort);
    }
    public Call<NaverDictionaryResponse> getNaverDictionaryResult(String word)
    {
        return request.getNaverDictionaryResponse(word, DEFAULT_DISPLAY, DEFAULT_START, DEFAULT_SORT);
    }
}
