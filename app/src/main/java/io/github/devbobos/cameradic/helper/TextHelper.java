package io.github.devbobos.cameradic.helper;

import android.text.Html;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import io.github.devbobos.cameradic.constant.SystemConstant;

/**
 * Created by devbobos on 2018. 9. 1..
 */
public class TextHelper
{
    public static final int CHARACTER_KOREAN = 1;
    public static final int CHARACTER_ENGLISH = 2;

    private static TextHelper instance = new TextHelper();

    private TextHelper() { }

    public static TextHelper getInstance()
    {
        return instance;
    }
    public int getCharacterCode(String text)
    {
        int characterCode = 0;
        for(int i = 0; i<text.length(); i++)
        {
            if(Character.getType(text.charAt(i)) == Character.OTHER_LETTER)
            {
                characterCode = CHARACTER_KOREAN;
            }
            else
            {
                characterCode = CHARACTER_ENGLISH;
            }
        }
        return characterCode;
    }
    public String removeHtmlEntityOnResponse(String text)
    {
        text = text.replace("&#60;","<");
        text = text.replace("&#62;",">");
        text = text.replace("&#38;","&");
        text = text.replace("&#34;","\"");
        text = text.replace("&#39;","'");
        text = text.replace("&#160;","");
        text = text.replace("&#162;","¢");
        text = text.replace("&#163;","£");
        text = text.replace("&#165;","¥");
        text = text.replace("&#8364;","€");
        text = text.replace("&#169;","©");
        text = text.replace("&#174;","®");
        text = text.replace("&quot;","");
        text = text.replace("&lt;","<");
        text = text.replace("&gt;",">");
        text = text.replace("<b>","");
        text = text.replace("</b>","");
        text = text.replace("<i>","");
        text = text.replace("</i>","");
        text = text.replace("<span>","");
        text = text.replace("</span>","");
        text = text.replace("<sup>","");
        text = text.replace("</sup>","");
        return text;
    }
    public String getHistoryDateString(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 M월 d일에 검색하였습니다.");
        return formatter.format(date);
    }
    public String getDictionaryType(int value)
    {
        switch (value)
        {
            case SystemConstant.LANGUAGE_TYPE_KOREAN:
                return SystemConstant.LANGUAGE_STRING_KOREAN;
            case SystemConstant.LANGUAGE_TYPE_ENGLISH:
                return SystemConstant.LANGUAGE_STRING_ENGLISH;
            case SystemConstant.LANGUAGE_TYPE_JAPANESE:
                return SystemConstant.LANGUAGE_STRING_JAPANESE;
            case SystemConstant.LANGUAGE_TYPE_CHINESE:
                return SystemConstant.LANGUAGE_STRING_CHINESE;
            case SystemConstant.LANGUAGE_TYPE_RUSSIAN:
                return SystemConstant.LANGUAGE_STRING_RUSSIAN;
            default:
                return SystemConstant.LANGUAGE_STRING_KOREAN;
        }
    }
}
