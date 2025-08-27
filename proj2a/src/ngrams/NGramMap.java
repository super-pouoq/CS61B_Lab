package ngrams;

import edu.princeton.cs.algs4.In;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;
import static utils.Utils.SHORT_WORDS_FILE;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    TreeMap<String, TimeSeries> words_tree=new TreeMap<>();
    TimeSeries total=new TimeSeries();

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        In in = new In(wordsFilename);

        while (!in.isEmpty()) {
            String nextLine = in.readLine();
            String[] splitLine = nextLine.split("\t");

            String word = splitLine[0];          // airport
            int year = Integer.parseInt(splitLine[1]);   // 2007
            double count = Double.parseDouble(splitLine[2]);  // 175702

            // 如果这个词还没出现过，就新建一个 TimeSeries
            if (!words_tree.containsKey(word)) {
                words_tree.put(word, new TimeSeries());
            }

            // 往对应的 TimeSeries 里加年份和次数
            words_tree.get(word).put(year, count);
        }
        In count_file = new In(countsFilename);

        while (!count_file.isEmpty()) {
            String nextLine = count_file.readLine();
            String[] splitLine = nextLine.split(",");
            int year = Integer.parseInt(splitLine[0]);   // 2007
            double count = Double.parseDouble(splitLine[1]);  // 175702
            total.put(year,count);
        }
        // TODO: Fill in this constructor. See the "NGramMap Tips" section of the spec for help.
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        TimeSeries return_series = new TimeSeries();

        if (!words_tree.containsKey(word)) {
            return return_series; // 空的
        }

        TimeSeries original = words_tree.get(word);

        for (Integer year : original.keySet()) {
            if (year >= startYear && year <= endYear) {
                return_series.put(year, original.get(year));
            }
        }
        return return_series;
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        if (!words_tree.containsKey(word)) {
            return new TimeSeries(); // 空的
        }

        TimeSeries original = words_tree.get(word);
        TimeSeries copy = new TimeSeries();

        for (Integer year : original.keySet()) {
            copy.put(year, original.get(year));
        }
        return copy;
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        TimeSeries copy = new TimeSeries();

        for (Integer year : total.keySet()) {
            copy.put(year, total.get(year));
        }
        return copy;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries data_series=countHistory(word,startYear,endYear);
        TimeSeries original = totalCountHistory();
        TimeSeries time_series = new TimeSeries();
        for (Integer year : original.keySet()) {
            if (year >= startYear && year <= endYear) {
                time_series.put(year, original.get(year));
            }
        }

        return data_series.dividedBy(time_series);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        return countHistory(word).dividedBy(totalCountHistory());
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries result = new TimeSeries();

        for (String word : words) {
            TimeSeries wordWeight = weightHistory(word, startYear, endYear);
            result = result.plus(wordWeight);
        }

        return result;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries result = new TimeSeries();

        for (String word : words) {
            TimeSeries wordWeight = weightHistory(word);
            result = result.plus(wordWeight);
        }

        return result;
    }

    // TODO: Add any private helper methods.
    // TODO: Remove all TODO comments before submitting.
}
