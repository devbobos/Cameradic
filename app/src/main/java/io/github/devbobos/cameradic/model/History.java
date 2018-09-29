package io.github.devbobos.cameradic.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by devbobos on 2018. 8. 26..
 */
@Entity
public class History
{
    @Id(autoincrement = true)
    private Long id;
    private Date createdDate;
    private Date updatedDate;
    private boolean isFavorite;
    private String title;
    private String description;
    private String link;
    private String thumbnail;
    private int languageCode;

    public History(String title, String description, String link, String thumbnail, int languageCode)
    {
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail = thumbnail;
        this.languageCode = languageCode;
    }

    public History(Date createdDate, Date updatedDate, boolean isFavorite, String title, String description, String link, String thumbnail, int languageCode)
    {
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isFavorite = isFavorite;
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail = thumbnail;
        this.languageCode = languageCode;
    }

    @Generated(hash = 351804744)
    public History(Long id, Date createdDate, Date updatedDate, boolean isFavorite, String title,
            String description, String link, String thumbnail, int languageCode) {
        this.id = id;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.isFavorite = isFavorite;
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail = thumbnail;
        this.languageCode = languageCode;
    }

    @Generated(hash = 869423138)
    public History() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(int languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String toString()
    {
        return "History{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", isFavorite=" + isFavorite +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", languageCode=" + languageCode +
                '}';
    }
}
