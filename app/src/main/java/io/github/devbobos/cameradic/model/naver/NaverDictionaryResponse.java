package io.github.devbobos.cameradic.model.naver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class NaverDictionaryResponse
{
    private String lastBuildDate;
    private float total;
    private float start;
    private float display;
    ArrayList< Item > items;

    public NaverDictionaryResponse(String lastBuildDate, float total, float start, float display, ArrayList<Item> items)
    {
        this.lastBuildDate = lastBuildDate;
        this.total = total;
        this.start = start;
        this.display = display;
        this.items = items;
    }

    public String getLastBuildDate()
    {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate)
    {
        this.lastBuildDate = lastBuildDate;
    }

    public float getTotal()
    {
        return total;
    }

    public void setTotal(float total)
    {
        this.total = total;
    }

    public float getStart()
    {
        return start;
    }

    public void setStart(float start)
    {
        this.start = start;
    }

    public float getDisplay()
    {
        return display;
    }

    public void setDisplay(float display)
    {
        this.display = display;
    }

    public ArrayList<Item> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<Item> items)
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "NaverDictionaryResponse{" +
                "lastBuildDate='" + lastBuildDate + '\'' +
                ", total=" + total +
                ", start=" + start +
                ", display=" + display +
                ", items=" + items +
                '}';
    }
}