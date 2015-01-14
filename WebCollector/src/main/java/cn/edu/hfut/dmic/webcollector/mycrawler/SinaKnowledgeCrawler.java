package cn.edu.hfut.dmic.webcollector.mycrawler;

import cn.edu.hfut.dmic.webcollector.conf.ConfConstant;
import cn.edu.hfut.dmic.webcollector.conf.ConfLoader;
import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2015/1/14.
 */
public class SinaKnowledgeCrawler extends DeepCrawler {
    //包括主题url正则和分页正则
    private RegexRule regexRule = new RegexRule();
    //<url的hashcode, 年龄段>
    private HashMap<Integer,Integer> urlAge = new HashMap<Integer, Integer>();
    private HashMap<String,Integer> partAge = new HashMap<String, Integer>();
    //<url的hashcode, 类别>
    private HashMap<Integer,Integer> urlCategory = new HashMap<Integer, Integer>();
    private HashMap<String,Integer> partCategory = new HashMap<String, Integer>();

    public SinaKnowledgeCrawler(String crawlPath) {
        super(crawlPath);
        //主题
        for(String themeReg : ConfLoader.templatesMap.keySet()){
            regexRule.addPositive(themeReg);
        }
        //分页
        for(String pagingReg : ConfLoader.pagingRegexSet){
            regexRule.addPositive(pagingReg);
        }
        //初始化partAge和partCategory
        partAge.put("/zbhy/",10);//准备怀孕
        partAge.put("/hyq/",20);//怀孕期
        partAge.put("/fmq/",30);//分娩期
        partAge.put("/xse/",40);//新生儿
        partAge.put("/yeq/",50);//婴儿期
        partAge.put("/yeq1/",60);//幼儿期
        partAge.put("/xlq/",70);//学龄前
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
        partCategory.put("//",1);//
    }

    @Override
    public Links visitAndGetNextLinks(Page page) {
        String url = page.getUrl();
        //如果匹配种子或分页，获取主题页和分页
        if(isMatchSeedsAndPaging(url)){
            Links nextLinks=new Links();
            /*将所有搜索结果条目的超链接返回，爬虫会在下一层爬取中爬取这些链接*/
            nextLinks.addAllFromDocument(page.getDoc(), regexRule);
            Integer age = 80;//其他年龄
            for(String part : partAge.keySet()){
                if(url.contains(part)){
                    age = partAge.get(part);
                    break;
                }
            }
            Integer category = 0;//其他
            for(String part : partCategory.keySet()){
                if(url.contains(part)){
                    category = partCategory.get(part);
                    break;
                }
            }
            for(String link : nextLinks){
                if(isMatchTheme(link)){
                    urlAge.put(link.hashCode(),age);
                    urlCategory.put(link.hashCode(),category);
                }
            }
            return nextLinks;
        }
        else if(isMatchTheme(url)){//如果匹配主题
            //主题抽取,插入数据库
            Integer age = urlAge.get(url.hashCode());
            Integer category = urlCategory.get(url.hashCode());
        }
        return null;
    }

    //是否匹配种子和分页
    private boolean isMatchSeedsAndPaging(String url){
        boolean isMatch = false;
        if(url == null){
            return isMatch;
        }
        if(ConfLoader.seedsSet.contains(url)){
            isMatch = true;
        }
        else{
            for(String pagingReg : ConfLoader.pagingRegexSet){
                if(Pattern.matches(pagingReg,url)){
                    isMatch = true;
                    break;
                }
            }
        }
        return isMatch;
    }

    //是否是主题页
    private boolean isMatchTheme(String url){
        boolean isMatch = false;
        if(url == null){
            return isMatch;
        }
        for(String themeReg : ConfLoader.templatesMap.keySet()){
            if(Pattern.matches(themeReg,url)){
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }
}
