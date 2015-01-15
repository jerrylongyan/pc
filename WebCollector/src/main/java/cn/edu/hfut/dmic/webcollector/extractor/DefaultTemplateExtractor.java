package cn.edu.hfut.dmic.webcollector.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2015/1/15.
 */
public class DefaultTemplateExtractor extends Extractor {
    //抽取正则
    protected HashMap<String,String> templateReg;
    protected static final String TitleStr = "title";
    protected static final String PublishDateStr = "publishDate";
    protected static final String AuthorStr = "author";
    protected static final String MainContentStr = "mainContent";
    protected static final String Seperator = "%%%";

    //html无用标签的过滤
    private final static String [][] filters = {
            {"(?is)<!DOCTYPE.*?>", ""},
            {"(?is)<script.*?>.*?</script>", ""},
            {"(?is)<style.*?>.*?</style>", ""},
            {"(?is)<!--.*?-->", ""},
            {"&.{2,5};|&#.{2,5};", ""},
            {"&nbsp;", " "}
    };

    public DefaultTemplateExtractor(HashMap<String, String> templateReg){
        this.templateReg = templateReg;
    }

    @Override
    public Object extractMainContent(Page page) {
        if(null == page){
            return null;
        }
        String html = page.getHtml();
        //过滤样式，脚本等不相干标签
        for(String [] filter : filters){
            html = html.replaceAll(filter[0],filter[1]);
        }

        Article article = new Article();
        article.setUrl(page.getUrl());

        //匹配作者
        article.setAuthor(extractField(AuthorStr, html));
        //时间抽取


        return null;
    }

    protected String extractField(String fieldStr, String html){
        String field = null;
        String fieldRegStr = templateReg.get(fieldStr);
        String fieldReg = fieldRegStr;
        int fieldGroup = 0;
        if (fieldRegStr.contains(Seperator)) {
            fieldReg = fieldRegStr.split(Seperator)[0];
            fieldGroup = Integer.parseInt(fieldRegStr.split(Seperator)[1]);
        }
        Pattern p = Pattern.compile(fieldReg);
        Matcher m = p.matcher(html);
        if(m.find()){
            String fieldHtml = m.group(fieldGroup);
            field = fieldHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        return field;
    }
}
