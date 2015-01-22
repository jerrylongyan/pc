package cn.edu.hfut.dmic.webcollector.extractor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2015/1/16.
 */
public class SinaKnowledgeExtractor extends DefaultTemplateExtractor {
    //<a href=http://roll.baby.sina.com.cn/babynewslist/yeq1/yeq1-2s/cczb/index.shtml>
    public static String Regx = "(?is)href=http://roll.baby.sina.com.cn/babynewslist/.*?/index.shtml>";
    public SinaKnowledgeExtractor(HashMap<String, String> templateReg, Validator validator) {
        super(templateReg,validator);
    }

    public String getCategoryAndAgeUrl(){
        String url = null;
        if(filteredHtml != null){
            Pattern p = Pattern.compile(Regx);
            Matcher m = p.matcher(filteredHtml);
            if(m.find()) {
                url = m.group(0);
            }
        }
        return url;
    }

    public static void main(String []args){
    }
}
