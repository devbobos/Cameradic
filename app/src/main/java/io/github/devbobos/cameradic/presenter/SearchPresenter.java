package io.github.devbobos.cameradic.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.orhanobut.logger.Logger;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.devbobos.cameradic.R;
import io.github.devbobos.cameradic.constant.SystemConstant;
import io.github.devbobos.cameradic.helper.PreferenceHelper;
import io.github.devbobos.cameradic.helper.TextHelper;
import io.github.devbobos.cameradic.model.History;
import io.github.devbobos.cameradic.model.glosbe.GlosbeResponse;
import io.github.devbobos.cameradic.model.glosbe.Meaning;
import io.github.devbobos.cameradic.model.glosbe.Tuc;
import io.github.devbobos.cameradic.model.naver.Item;
import io.github.devbobos.cameradic.model.naver.NaverDictionaryResponse;
import io.github.devbobos.cameradic.service.GlosbeService;
import io.github.devbobos.cameradic.service.NaverDictionaryService;
import io.github.devbobos.cameradic.view.SearchActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by devbobos on 2018. 9. 1..
 */
public class SearchPresenter
{
    private SearchActivity activity;

    public SearchPresenter(SearchActivity activity)
    {
        this.activity = activity;
    }
    public void setActionBar()
    {
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(activity.getResources().getString(R.string.search_title));
        }
        else
        {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(activity.getResources().getString(R.string.search_title));
        }
    }
    public void setBottomSpace(final View bottomSpace)
    {
        KeyboardVisibilityEvent.setEventListener(activity, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen)
            {
                if(isOpen)
                {
                    bottomSpace.setVisibility(View.GONE);
                }
                else
                {
                    bottomSpace.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void getResultList(final String inputText)
    {
        int characterCode = TextHelper.getInstance().getCharacterCode(inputText);
        Call<GlosbeResponse> glosbeResponseCall;
        switch (characterCode)
        {
            case TextHelper.CHARACTER_KOREAN:
                switch (PreferenceHelper.getInstance().getDictionaryType(activity))
                {
                    case SystemConstant.LANGUAGE_TYPE_KOREAN:
                        Call<NaverDictionaryResponse> naverDictionaryResponseCall = NaverDictionaryService.getInstance().getNaverDictionaryResult(inputText);
                        naverDictionaryResponseCall.enqueue(new Callback<NaverDictionaryResponse>() {
                            @Override
                            public void onResponse(Call<NaverDictionaryResponse> call, Response<NaverDictionaryResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                if(response.body()!=null)
                                {
                                    Logger.d("request naver dictionary success : "+response.body().toString());
                                    NaverDictionaryResponse naverDictionaryResponse = response.body();
                                    for(Item index : naverDictionaryResponse.getItems())
                                    {
                                        if(index.getDescription().length()>0)
                                        {
                                            list.add(new History(
                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(index.getTitle()),
                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(index.getDescription()),
                                                    index.getLink(),
                                                    index.getThumbnail(),
                                                    SystemConstant.LANGUAGE_TYPE_KOREAN));
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<NaverDictionaryResponse> call, Throwable t)
                            {
                                Logger.d("request naver dictionary failed : "+t.getMessage());
                                Call<GlosbeResponse> retryGlosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                                retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                    @Override
                                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                    {
                                        List<History> list = new ArrayList<>();
                                        GlosbeResponse glosbeResponse = response.body();
                                        if(glosbeResponse!=null)
                                        {
                                            Logger.d("request glosbe dictionary success : "+response.body().toString());
                                            for(Tuc tuc : glosbeResponse.getTuc())
                                            {
                                                for(Meaning meaning : tuc.getMeanings())
                                                {
                                                    if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                                    {
                                                        StringBuilder titleBuilder = new StringBuilder();
                                                        titleBuilder.append(tuc.getPhrase().getText());
                                                        titleBuilder.append(" (");
                                                        titleBuilder.append(inputText);
                                                        titleBuilder.append(")");
                                                        list.add(new History(
                                                                titleBuilder.toString(),
                                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                SystemConstant.URL_GLOSBE_KO_KO+inputText,
                                                                null,
                                                                SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                    }
                                                    else
                                                    {
                                                        list.add(new History(
                                                                glosbeResponse.getPhrase(),
                                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                SystemConstant.URL_GLOSBE_KO_KO+inputText,
                                                                null,
                                                                SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                    }
                                                }
                                            }
                                        }
                                        activity.setResultView(list);
                                    }

                                    @Override
                                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                    {
                                        Logger.d("request glosbe dictionary failed : "+t.getMessage());
                                    }
                                });
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_ENGLISH:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_EN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_ENGLISH));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_EN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_ENGLISH));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_JAPANESE:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_JAPANESE);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_JP+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_JAPANESE));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_JP+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_JAPANESE));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_CHINESE:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_CHINESE);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_CN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_CHINESE));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_CN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_CHINESE));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_RUSSIAN:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_RUSSIAN);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_RN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_RUSSIAN));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_KO_RN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_RUSSIAN));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                }
                break;
            case TextHelper.CHARACTER_ENGLISH:
                switch (PreferenceHelper.getInstance().getDictionaryType(activity))
                {
                    case SystemConstant.LANGUAGE_TYPE_KOREAN:
                        Call<NaverDictionaryResponse> naverDictionaryResponseCall = NaverDictionaryService.getInstance().getNaverDictionaryResult(inputText);
                        naverDictionaryResponseCall.enqueue(new Callback<NaverDictionaryResponse>() {
                            @Override
                            public void onResponse(Call<NaverDictionaryResponse> call, Response<NaverDictionaryResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                if(response.body()!=null)
                                {
                                    Logger.d("request naver dictionary success : "+response.body().toString());
                                    NaverDictionaryResponse naverDictionaryResponse = response.body();
                                    for(Item index : naverDictionaryResponse.getItems())
                                    {
                                        if(index.getDescription().length()>0)
                                        {
                                            list.add(new History(
                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(index.getTitle()),
                                                    TextHelper.getInstance().removeHtmlEntityOnResponse(index.getDescription()),
                                                    index.getLink(),
                                                    index.getThumbnail(),
                                                    SystemConstant.LANGUAGE_TYPE_KOREAN));
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<NaverDictionaryResponse> call, Throwable t)
                            {
                                Logger.d("request naver dictionary failed : "+t.getMessage());
                                Call<GlosbeResponse> retryGlosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_KOREAN, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                                retryGlosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                                    @Override
                                    public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                                    {
                                        List<History> list = new ArrayList<>();
                                        GlosbeResponse glosbeResponse = response.body();
                                        if(glosbeResponse!=null)
                                        {
                                            Logger.d("request glosbe dictionary success : "+response.body().toString());
                                            for(Tuc tuc : glosbeResponse.getTuc())
                                            {
                                                for(Meaning meaning : tuc.getMeanings())
                                                {
                                                    if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                                    {
                                                        StringBuilder titleBuilder = new StringBuilder();
                                                        titleBuilder.append(tuc.getPhrase().getText());
                                                        titleBuilder.append(" (");
                                                        titleBuilder.append(inputText);
                                                        titleBuilder.append(")");
                                                        list.add(new History(
                                                                titleBuilder.toString(),
                                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                SystemConstant.URL_GLOSBE_EN_KO+inputText,
                                                                null,
                                                                SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                    }
                                                    else
                                                    {
                                                        list.add(new History(
                                                                glosbeResponse.getPhrase(),
                                                                TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                                SystemConstant.URL_GLOSBE_EN_KO+inputText,
                                                                null,
                                                                SystemConstant.LANGUAGE_TYPE_KOREAN));
                                                    }
                                                }
                                            }
                                        }
                                        activity.setResultView(list);
                                    }

                                    @Override
                                    public void onFailure(Call<GlosbeResponse> call, Throwable t)
                                    {
                                        Logger.d("request glosbe dictionary failed : "+t.getMessage());
                                    }
                                });
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_ENGLISH:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_ENGLISH);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_EN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_ENGLISH));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_EN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_ENGLISH));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_JAPANESE:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_JAPANESE);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_JP+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_JAPANESE));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_JP+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_JAPANESE));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_CHINESE:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_CHINESE);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_CN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_CHINESE));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_CN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_CHINESE));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                    case SystemConstant.LANGUAGE_TYPE_RUSSIAN:
                        glosbeResponseCall = GlosbeService.getInstance().getGlosbeResponse(inputText, GlosbeService.getInstance().LANGUAGE_ENGLISH, GlosbeService.getInstance().LANGUAGE_RUSSIAN);
                        glosbeResponseCall.enqueue(new Callback<GlosbeResponse>() {
                            @Override
                            public void onResponse(Call<GlosbeResponse> call, Response<GlosbeResponse> response)
                            {
                                List<History> list = new ArrayList<>();
                                GlosbeResponse glosbeResponse = response.body();
                                if(glosbeResponse!=null)
                                {
                                    Logger.d("request glosbe dictionary success : "+response.body().toString());
                                    for(Tuc tuc : glosbeResponse.getTuc())
                                    {
                                        for(Meaning meaning : tuc.getMeanings())
                                        {
                                            if(tuc.getPhrase()!=null && tuc.getPhrase().getText()!=null)
                                            {
                                                StringBuilder titleBuilder = new StringBuilder();
                                                titleBuilder.append(tuc.getPhrase().getText());
                                                titleBuilder.append(" (");
                                                titleBuilder.append(inputText);
                                                titleBuilder.append(")");
                                                list.add(new History(
                                                        titleBuilder.toString(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_RN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_RUSSIAN));
                                            }
                                            else
                                            {
                                                list.add(new History(
                                                        glosbeResponse.getPhrase(),
                                                        TextHelper.getInstance().removeHtmlEntityOnResponse(meaning.getText()),
                                                        SystemConstant.URL_GLOSBE_EN_RN+inputText,
                                                        null,
                                                        SystemConstant.LANGUAGE_TYPE_RUSSIAN));
                                            }
                                        }
                                    }
                                }
                                activity.setResultView(list);
                            }

                            @Override
                            public void onFailure(Call<GlosbeResponse> call, Throwable t)
                            {
                                Logger.d("request glosbe dictionary failed : "+t.getMessage());
                            }
                        });
                        break;
                }
                break;
        }
    }
    public void hideKeyboard(View container) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
    }
    public void showDictionaryTypeSelector(int recentValue)
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        CharSequence items[] = new CharSequence[]{SystemConstant.LANGUAGE_STRING_KOREAN,
                SystemConstant.LANGUAGE_STRING_ENGLISH,
                SystemConstant.LANGUAGE_STRING_JAPANESE,
                SystemConstant.LANGUAGE_STRING_CHINESE,
                SystemConstant.LANGUAGE_STRING_RUSSIAN};
        builder.setTitle(activity.getResources().getString(R.string.setting_popup_dictionary_title));
        builder.setSingleChoiceItems(items, recentValue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Logger.d("dictionary type : "+which);
                PreferenceHelper.getInstance().setDictionaryType(activity.getApplicationContext(), which);
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.setting_popup_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.setting_popup_decline), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                return;
            }
        });
        builder.create();
        builder.show();
    }
}