package io.github.devbobos.cameradic.model.glosbe;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class Phrase {

    private String text;
    private String language;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString()
    {
        return "Phrase{" +
                "text='" + text + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}