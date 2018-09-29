package io.github.devbobos.cameradic.model.glosbe;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class Meaning {

    private String language;
    private String text;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return "Meaning{" +
                "language='" + language + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}