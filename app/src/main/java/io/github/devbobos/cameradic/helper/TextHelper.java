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
    public String getKoreanFromResultTitle(String input)
    {
        if(input.equals("person"))
        {
            return "사람";
        }
        else if(input.equals("bicycle"))
        {
            return "자전거";
        }
        else if(input.equals("car"))
        {
            return "자동차";
        }
        else if(input.equals("motorcycle"))
        {
            return "모터사이클";
        }
        else if(input.equals("airplane"))
        {
            return "비행기";
        }
        else if(input.equals("bus"))
        {
            return "버스";
        }
        else if(input.equals("train"))
        {
            return "기차";
        }
        else if(input.equals("truck"))
        {
            return "트럭";
        }
        else if(input.equals("boat"))
        {
            return "보트";
        }
        else if(input.equals("traffic light"))
        {
            return "신호등";
        }
        else if(input.equals("fire hydrant"))
        {
            return "소화전";
        }
        else if(input.equals("stop sign"))
        {
            return "차 정지표지";
        }
        else if(input.equals("parking meter"))
        {
            return "주차료징수기";
        }
        else if(input.equals("bench"))
        {
            return "벤치";
        }
        else if(input.equals("bird"))
        {
            return "새";
        }
        else if(input.equals("cat"))
        {
            return "고양이";
        }
        else if(input.equals("dog"))
        {
            return "개";
        }
        else if(input.equals("horse"))
        {
            return "말";
        }
        else if(input.equals("sheep"))
        {
            return "양";
        }
        else if(input.equals("cow"))
        {
            return "소";
        }
        else if(input.equals("elephant"))
        {
            return "코끼리";
        }
        else if(input.equals("bear"))
        {
            return "곰";
        }
        else if(input.equals("zebra"))
        {
            return "얼룩말";
        }
        else if(input.equals("giraffe"))
        {
            return "기린";
        }
        else if(input.equals("backpack"))
        {
            return "백팩";
        }
        else if(input.equals("umbrella"))
        {
            return "우산";
        }
        else if(input.equals("handbag"))
        {
            return "핸드백";
        }
        else if(input.equals("tie"))
        {
            return "넥타이";
        }
        else if(input.equals("suitcase"))
        {
            return "여행가방";
        }
        else if(input.equals("frisbee"))
        {
            return "프리스비";
        }
        else if(input.equals("skis"))
        {
            return "스키";
        }
        else if(input.equals("snowboard"))
        {
            return "스노우보드";
        }
        else if(input.equals("sports ball"))
        {
            return "공";
        }
        else if(input.equals("kite"))
        {
            return "연";
        }
        else if(input.equals("baseball bat"))
        {
            return "야구 방망이";
        }
        else if(input.equals("baseball glove"))
        {
            return "야구 글러브";
        }
        else if(input.equals("skateboard"))
        {
            return "스케이트보드";
        }
        else if(input.equals("surfboard"))
        {
            return "서핑보드";
        }
        else if(input.equals("tennis racket"))
        {
            return "테니스 라켓";
        }
        else if(input.equals("bottle"))
        {
            return "병";
        }
        else if(input.equals("wine glass"))
        {
            return "와인잔";
        }
        else if(input.equals("cup"))
        {
            return "컵";
        }
        else if(input.equals("fork"))
        {
            return "포크";
        }
        else if(input.equals("knife"))
        {
            return "칼";
        }
        else if(input.equals("spoon"))
        {
            return "숟가락";
        }
        else if(input.equals("bowl"))
        {
            return "사발";
        }
        else if(input.equals("banana"))
        {
            return "바나나";
        }
        else if(input.equals("apple"))
        {
            return "사과";
        }
        else if(input.equals("sandwich"))
        {
            return "샌드위치";
        }
        else if(input.equals("orange"))
        {
            return "오렌지";
        }
        else if(input.equals("broccoli"))
        {
            return "브로콜리";
        }
        else if(input.equals("carrot"))
        {
            return "당근";
        }
        else if(input.equals("hot dog"))
        {
            return "핫도그";
        }
        else if(input.equals("pizza"))
        {
            return "피자";
        }
        else if(input.equals("donut"))
        {
            return "도넛";
        }
        else if(input.equals("cake"))
        {
            return "케이크";
        }
        else if(input.equals("chair"))
        {
            return "의자";
        }
        else if(input.equals("couch"))
        {
            return "침상";
        }
        else if(input.equals("potted plant"))
        {
            return "화분";
        }
        else if(input.equals("bed"))
        {
            return "침대";
        }
        else if(input.equals("dining table"))
        {
            return "식탁";
        }
        else if(input.equals("toilet"))
        {
            return "화장실";
        }
        else if(input.equals("tv"))
        {
            return "텔레비전";
        }
        else if(input.equals("laptop"))
        {
            return "랩톱 컴퓨터";
        }
        else if(input.equals("mouse"))
        {
            return "마우스";
        }
        else if(input.equals("remote"))
        {
            return "리모컨";
        }
        else if(input.equals("keyboard"))
        {
            return "키보드";
        }
        else if(input.equals("cell phone"))
        {
            return "휴대전화";
        }
        else if(input.equals("microwave"))
        {
            return "전자레인지";
        }
        else if(input.equals("oven"))
        {
            return "오븐";
        }
        else if(input.equals("toaster"))
        {
            return "토스터";
        }
        else if(input.equals("sink"))
        {
            return "싱크대";
        }
        else if(input.equals("refrigerator"))
        {
            return "냉장고";
        }
        else if(input.equals("book"))
        {
            return "책";
        }
        else if(input.equals("clock"))
        {
            return "시계";
        }
        else if(input.equals("vase"))
        {
            return "꽃병";
        }
        else if(input.equals("scissors"))
        {
            return "가위";
        }
        else if(input.equals("teddy bear"))
        {
            return "곰인형";
        }
        else if(input.equals("hair drier"))
        {
            return "헤어드라이어";
        }
        else if(input.equals("toothbrush"))
        {
            return "칫솔";
        }
        else
        {
            return "";
        }
    }
}
