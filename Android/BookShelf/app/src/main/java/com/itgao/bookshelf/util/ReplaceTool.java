package com.itgao.bookshelf.util;

/**
 * Created by cheng on 2016/12/24.
 */
public class ReplaceTool {
    private static final String nbsp = "&nbsp;";
    private static final String br = "<br.*?></br>";
    private static final String brbr = "<br.*?/>";
    private static final String em = "<em>";
    private static final String me = "</em>";

    public  static String replaceAll(String text){
        String text1 = text.replaceAll(nbsp,"");
        text1 = text1.replaceAll(br,"\n");
        text1 = text1.replaceAll(em," ");
        text1 = text1.replaceAll(me," ");
        return text1.replaceAll(brbr,"\n");
    }
}
