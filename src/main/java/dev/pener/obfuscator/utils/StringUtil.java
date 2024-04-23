package dev.pener.obfuscator.utils;

import java.util.ArrayList;

public class StringUtil {
    public static String[] split(String string, String delem) {
        ArrayList<String> list = new ArrayList<String>();
        char[] charArr = string.toCharArray();
        char[] delemArr = delem.toCharArray();
        int counter = 0;
        for (int i = 0; i < charArr.length; i++) {
            int k = 0;
            for (int j = 0; j < delemArr.length; j++) {
                if (charArr[i+j] == delemArr[j]) {
                    k++;
                } else {
                    break;
                }
            }
            if (k == delemArr.length) {
                String s = "";
                while (counter < i ) {
                    s += charArr[counter];
                    counter++;
                }
                counter = i = i + k;
                list.add(s);
            }
        }
        String s = "";
        if (counter < charArr.length) {
            while (counter < charArr.length) {
                s += charArr[counter];
                counter++;
            }
            list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }
}
