package io.github.devbobos.cameradic.model.naver;

import java.util.ArrayList;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class Item
{
    private String title;
    private String link;
    private String description;
    private String thumbnail;

    public Item(String title, String link, String description, String thumbnail)
    {
        this.title = title;
        this.link = link;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString()
    {
        return "Item{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}