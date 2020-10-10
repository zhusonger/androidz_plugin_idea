package cn.com.lasong.plugin.idea.utils;

import java.io.File;
import java.util.Comparator;

/**
 * 树排序
 */
public class TreeComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        String s1 = o1 == null ? "" : o1.toString();
        String s2 = o2 == null ? "" : o2.toString();
        if (s1.length() == 0 && s2.length() > 0) {
            return 1;
        }
        if (s2.length() == 0 && s1.length() > 0) {
            return -1;
        }
        if (s2.length() == 0) {
            return 0;
        }
        // 到这里肯定不为空
        File s1File = new File(s1);
        File s2File = new File(s2);
        if (s1File.isDirectory() && s2File.isFile()) {
            return -1;
        }
        if (s1File.isFile() && s2File.isDirectory()) {
            return 1;
        }
        return compareString(s1File.getName(), s2File.getName());
    }

    private int compareString(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        int min = Math.min(n1, n2);
        for (int i = 0; i < min; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 != c2) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        // No overflow because of numeric promotion
                        return c1 - c2;
                    }
                }
            }
        }
        return n1 - n2;
    }
}
