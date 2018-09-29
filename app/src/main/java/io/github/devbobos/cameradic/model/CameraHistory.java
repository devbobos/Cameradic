package io.github.devbobos.cameradic.model;

/**
 * Created by devbobos on 2018. 9. 4..
 */
public class CameraHistory
{
    private float recognitionValue;
    private boolean isFavorite;
    private String title;
    private String description;
    private String link;
    private String thumbnail;
    private int languageCode;

    public CameraHistory(float recognitionValue, String title, String description, String link, String thumbnail, int languageCode)
    {
        this.recognitionValue = recognitionValue;
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail = thumbnail;
        this.languageCode = languageCode;
    }

    public CameraHistory(float recognitionValue, boolean isFavorite, String title, String description, String link, String thumbnail, int languageCode)
    {
        this.recognitionValue = recognitionValue;
        this.isFavorite = isFavorite;
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail = thumbnail;
        this.languageCode = languageCode;
    }

    public float getRecognitionValue()
    {
        return recognitionValue;
    }

    public void setRecognitionValue(float recognitionValue)
    {
        this.recognitionValue = recognitionValue;
    }

    public boolean isFavorite()
    {
        return isFavorite;
    }

    public void setFavorite(boolean favorite)
    {
        isFavorite = favorite;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public int getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(int languageCode)
    {
        this.languageCode = languageCode;
    }

    @Override
    public String toString()
    {
        return "CameraHistory{" +
                "recognitionValue=" + recognitionValue +
                ", isFavorite=" + isFavorite +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", languageCode=" + languageCode +
                '}';
    }
}
