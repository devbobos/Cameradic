package io.github.devbobos.cameradic.model.glosbe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devbobos on 2018. 8. 15..
 */
public class Tuc {

    private List<Long> authors = new ArrayList<>();
    private List<Meaning> meanings = new ArrayList<>();
    private Long meaningId;
    private Phrase phrase;

    public List<Long> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Long> authors) {
        this.authors = authors;
    }

    public Long getMeaningId() {
        return meaningId;
    }

    public void setMeaningId(Long meaningId) {
        this.meaningId = meaningId;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.phrase = phrase;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    @Override
    public String toString()
    {
        return "Tuc{" +
                "authors=" + authors +
                ", meanings=" + meanings +
                ", meaningId=" + meaningId +
                ", phrase=" + phrase +
                '}';
    }
}