/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

package Utils;

import java.util.concurrent.atomic.AtomicInteger;

public class UtilsMsg {
    public static final int VERBOSE_OFF = 0;
    public static final int VERBOSE_ON_LOW = 1;
    public static final int VERBOSE_ON_HIGH = 2;
    public static final String WORD_REGEX = "^[^\\s]*$"; // Makes sure the word does not contain any whitespace
    public static final String ERROR_WORD_ALREADY_EXISTS = "Word already exists in the dictionary.";
    public static final String ERROR_WORD_NOT_FOUND = "Word is not found in the dictionary.";
    public static final String ERROR_MEANINGS_EMPTY = "Attempting to add or update a word must have at least one meaning.";
    public static final String SERVER_OVERLOAD_REJECT_MSG = "Server is overloaded. Please try again later.";
}


