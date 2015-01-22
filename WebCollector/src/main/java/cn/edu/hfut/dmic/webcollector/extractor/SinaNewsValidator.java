package cn.edu.hfut.dmic.webcollector.extractor;

import org.apache.commons.lang.StringUtils;

/**
 * Created by jerry on 2015/1/21.
 */
public class SinaNewsValidator implements Validator {
    private static String imgClearRegx = "(?is)<!-- 标清图 begin -->.*?<!-- 标清图 end -->";
    @Override
    public String validateHtml(String content) {
        if(content == null)
            return null;
        String validatedStr = content.trim();
        validatedStr = validatedStr.replaceAll(imgClearRegx," ");

        String divStart = "<div";
        String divEnd = "</div>";
        int divStartNum = StringUtils.countMatches(validatedStr, divStart);
        int divEndNum = StringUtils.countMatches(validatedStr,divEnd);
        //中间的div如果不匹配的话，统一改成<p>
        if(divStartNum != divEndNum){
            validatedStr = validatedStr.replaceAll("(?is)<div.*?>","<p>").replaceAll(divEnd,"</p>");
        }
        return validatedStr.replaceAll("(?is)<!--.*?-->", " ").trim();
    }
}
