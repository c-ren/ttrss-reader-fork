package org.ttrssreader.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.ttrssreader.controllers.Controller;
import android.net.Uri;
import android.text.TextUtils;

// contains code from the Apache Software foundation
public class StringSupport {
    
    /**
     * Turns a camel case string into an underscored one, e.g. "HelloWorld"
     * becomes "hello_world".
     * 
     * @param camelCaseString
     *            the string to underscore
     * @return the underscored string
     */
    protected static String underscore(String camelCaseString) {
        String[] words = splitByCharacterTypeCamelCase(camelCaseString);
        return TextUtils.join("_", words).toLowerCase();
    }
    
    /**
     * <p>
     * Splits a String by Character type as returned by <code>java.lang.Character.getType(char)</code>. Groups of
     * contiguous characters of the same type are returned as complete tokens, with the following exception: the
     * character of type <code>Character.UPPERCASE_LETTER</code>, if any, immediately preceding a token of type
     * <code>Character.LOWERCASE_LETTER</code> will belong to the following token rather than to the preceding, if any,
     * <code>Character.UPPERCASE_LETTER</code> token.
     * 
     * <pre>
     * StringUtils.splitByCharacterTypeCamelCase(null)         = null
     * StringUtils.splitByCharacterTypeCamelCase("")           = []
     * StringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * StringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     * 
     * @param str
     *            the String to split, may be <code>null</code>
     * @return an array of parsed Strings, <code>null</code> if null String
     *         input
     * @since 2.4
     */
    private static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }
    
    /**
     * <p>
     * Splits a String by Character type as returned by <code>java.lang.Character.getType(char)</code>. Groups of
     * contiguous characters of the same type are returned as complete tokens, with the following exception: if
     * <code>camelCase</code> is <code>true</code>, the character of type <code>Character.UPPERCASE_LETTER</code>, if
     * any, immediately preceding a token of type <code>Character.LOWERCASE_LETTER</code> will belong to the following
     * token rather than to the preceding, if any, <code>Character.UPPERCASE_LETTER</code> token.
     * 
     * @param str
     *            the String to split, may be <code>null</code>
     * @param camelCase
     *            whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, <code>null</code> if null String
     *         input
     * @since 2.4
     */
    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new String[0];
        }
        char[] c = str.toCharArray();
        ArrayList<String> list = new ArrayList<String>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * Splits the ids into Sets of Strings with 50 ids each if configured in preferences, else only splits on 500 to
     * avoid extremely large requests.
     * 
     * @param ids
     *            the set of ids to be split
     * @return a set of Strings with comma-separated ids
     */
    public static Set<String> convertListToString(Set<Integer> ids) {
        int maxCount = 500;
        
        if (Controller.getInstance().splitGetRequests())
            maxCount = 50;
        
        Set<String> ret = new HashSet<String>();
        int count = 0;
        
        Iterator<Integer> it = ids.iterator();
        StringBuilder idList = new StringBuilder();
        while (it.hasNext()) {
            idList.append(it.next());
            if (it.hasNext() && count < maxCount) {
                idList.append(",");
            }
            if (count >= maxCount) {
                ret.add(idList.toString());
                idList = new StringBuilder();
                count = 0;
            } else {
                count++;
            }
        }
        
        ret.add(idList.toString());
        
        return ret;
    }
    
    public static String[] setToArray(Set<String> set) {
        String[] ret = new String[set.size()];
        int i = 0;
        for (String s : set) {
            ret[i++] = s;
        }
        return ret;
    }
    
    public static String getBaseURL(String url) {
        Uri uri = Uri.parse(url);
        if (uri != null) {
            // TODO: Check if base-url is the right thing to return, perhaps we also need the rest of the path without
            // the last segment
            return uri.getScheme() + "://" + uri.getAuthority();
        }
        return null;
    }
    
}
