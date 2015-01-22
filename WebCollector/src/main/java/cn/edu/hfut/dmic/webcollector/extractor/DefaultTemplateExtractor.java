package cn.edu.hfut.dmic.webcollector.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2015/1/15.
 */
public class DefaultTemplateExtractor implements Extractor {
    //抽取正则
    protected HashMap<String,String> templateReg;
    protected static final String TitleStr = "title";
    protected static final String PublishDateStr = "publishDate";
    protected static final String AuthorStr = "author";
    protected static final String MainContentStr = "mainContent";
    protected static final String Seperator = "%%%";
    protected String filteredHtml = null;
    protected Validator validator;

    //html无用标签的过滤
    private final static String [][] filters = {
            {"(?is)<!DOCTYPE.*?>", ""},
            {"(?is)<script.*?>.*?</script>", ""},
            {"(?is)<style.*?>.*?</style>", ""},
            //{"(?is)<!--.*?-->", ""}, //不去掉，可通过它进行正文抽取
            {"&.{2,5};|&#.{2,5};", ""},
            {"&nbsp;", " "}
    };

    public DefaultTemplateExtractor(HashMap<String, String> templateReg){
        this.templateReg = templateReg;
        this.validator = new DefaultValidator();
    }

    public DefaultTemplateExtractor(HashMap<String, String> templateReg, Validator validator){
        this.templateReg = templateReg;
        this.validator = validator;
    }

    public void setValidator(Validator validator){
        this.validator = validator;
    }

    @Override
    public Object extractMainContent(Page page) {
        if(null == page || page.getHtml() == null){
            return null;
        }
        filteredHtml = null;
        String html = page.getHtml();
        //过滤样式，脚本等不相干标签
        for(String [] filter : filters){
            html = html.replaceAll(filter[0],filter[1]);
        }
        filteredHtml = html;

        Article article = new Article();
        article.setUrl(page.getUrl());

        //匹配作者
        article.setAuthor(extractField(AuthorStr, html,false));
        //时间抽取
        article.setPublishDate(extractField(PublishDateStr,html,false));
        //正文
        String content = extractField(MainContentStr,html,true);
        article.setMainContext(validator.validateHtml(content));
        //标题
        article.setTitle(extractField(TitleStr,html,false));

        return article;
    }

    protected String extractField(String fieldStr, String html, boolean isNeedHtml){
        String field = null;
        String fieldRegStr = templateReg.get(fieldStr);
        if(fieldRegStr == null || fieldRegStr.isEmpty()){
            return field;
        }
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
            if(isNeedHtml) {
                field = fieldHtml.replaceAll("\\s*\n\\s*", "\n").replaceAll("\\s*\r\\s*", "\n")
                        .trim();//.replaceAll("(?is)<!--.*?-->","")
            }else{
                field = fieldHtml.replaceAll("<.*?>", "").
                        replaceAll("\\s*\n\\s*", " ").replaceAll("\\s*\r\\s*", " ")
                        .replaceAll("(?is)<!--.*?-->", "").trim();
            }
        }
        return field;
    }
}
