package io.github.devbobos.cameradic.service;

import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.model.glosbe.GlosbeResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class GlosbeService
{
    private static GlosbeService instance = new GlosbeService();
    private static Retrofit retrofit;
    private static GlosbeRequest request;
    private final String DEFAULT_PRETTY_VALUE = "true";
    private final String FORMAT_JSON = "json";
    public final String LANGUAGE_ENGLISH = "eng";
    public final String LANGUAGE_KOREAN = "kor";
    public final String LANGUAGE_JAPANESE = "jpn";
    public final String LANGUAGE_RUSSIAN = "rus";
    public final String LANGUAGE_CHINESE = "cmn";

    public static GlosbeService getInstance()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SystemConstant.URL_GLOSBE_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if(request == null)
        {
            request = retrofit.create(GlosbeRequest.class);
        }
        return instance;
    }

    private interface GlosbeRequest
    {
        @GET("translate")
        Call<GlosbeResponse> getGlosbe(@Query("from") String from, @Query("dest") String dest, @Query("phrase") String phrase, @Query("format") String format, @Query("pretty") String pretty);
    }
    public Call<GlosbeResponse> getGlosbeResponse(String word, String from, String to)
    {
        return request.getGlosbe(from, to, word, FORMAT_JSON, DEFAULT_PRETTY_VALUE);
    }
}
