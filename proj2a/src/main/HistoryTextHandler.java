package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.Collections;
import java.util.List;

import static utils.Utils.*;

public class HistoryTextHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    public HistoryTextHandler(NGramMap ngm) {
        super();
        this.ngm = ngm;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        StringBuilder returnString=new StringBuilder();
        for (String word : words) {
            TimeSeries History = ngm.weightHistory(word, startYear, endYear);
            returnString.append(word);
            returnString.append(": ");
            returnString.append(History.toString());
            returnString.append("\n");
        }
        return returnString.toString();
    }
}
