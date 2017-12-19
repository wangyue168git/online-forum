package com.bolo.util;

/**
 * @Author wangyue
 * @Date 16:51
 */
public class MatchUtils {

    public static String filterNumber(String number){
        return number.replaceAll("[^(0-9)]","");
    }

    public static String filterAlphabet(String alph){
        return alph.replaceAll("[^(A-Za-z)]","");
    }

    public static String filterChinese(String chin){
        return chin.replaceAll("[^(\\u4e00-\\u9fa5)]","");
    }

    public static String filter(String character){
        return character.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5]","");
    }
}
