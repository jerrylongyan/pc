package cn.edu.hfut.dmic.webcollector.mycrawler;

import cn.edu.hfut.dmic.webcollector.conf.ConfConstant;
import cn.edu.hfut.dmic.webcollector.conf.ConfLoader;
import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.extractor.Article;
import cn.edu.hfut.dmic.webcollector.extractor.DefaultTemplateExtractor;
import cn.edu.hfut.dmic.webcollector.extractor.Extractor;
import cn.edu.hfut.dmic.webcollector.extractor.SinaKnowledgeExtractor;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2015/1/14.
 */
public class SinaKnowledgeCrawler extends DefaultCrawler {
    public static final Logger LOG = LoggerFactory.getLogger(SinaKnowledgeCrawler.class);

    private static HashMap<String,Integer> partAge = new HashMap<String, Integer>();
    private static HashMap<String,Integer> partCategory = new HashMap<String, Integer>();

    static {
        //初始化partAge和partCategory
        partAge.put("/zbhy/",10);//准备怀孕
        partAge.put("/hyq/",20);//怀孕期
        partAge.put("/fmq/",30);//分娩期
        partAge.put("/xse/",40);//新生儿
        partAge.put("/yeq/",50);//婴儿期
        partAge.put("/yeq1/",60);//幼儿期
        partAge.put("/xlq/",70);//学龄前

        partCategory.put("/qt/",0);//其他
        partCategory.put("/hqzb1/",1);//孕前准备
        partCategory.put("/bylc/",2);//避孕流产
        partCategory.put("/hyjj/",3);//怀孕禁忌
        partCategory.put("/ycys/",4);//遗传优生
        partCategory.put("/snsn/",5);//生男生女
        partCategory.put("/byby/",6);//不孕不育
        partCategory.put("/yyys/",7);//营养饮食
        partCategory.put("/tefy/",8);//胎儿发育
        partCategory.put("/yqbj/",9);//孕期保健
        partCategory.put("/yqjc/",10);//孕期检查
        partCategory.put("/yqxl/",11);//孕期心理
        partCategory.put("/ssyf/",12);//时尚孕妇
        partCategory.put("/cjjb/",13);//常见疾病
        partCategory.put("/yqaq/",14);//孕期安全
        partCategory.put("/tj2/",15);//胎教
        partCategory.put("/dbt/",16);//多胞胎
        partCategory.put("/scfs/",17);//生产方式
        partCategory.put("/lpdc/",18);//临盆待产
        partCategory.put("/fmgc/",19);//分娩过程
        partCategory.put("/yjbb/",20);//迎接宝贝
        partCategory.put("/mrwy/",21);//母乳喂养
        partCategory.put("/chhf/",22);//产后恢复
        partCategory.put("/chyyz/",23);//产后抑郁症
        partCategory.put("/aqbj/",24);//安全保健
        partCategory.put("/cczb/",25);//成长指标
        partCategory.put("/rgwy/",26);//人工喂养
        partCategory.put("/hlbj/",27);//护理保健
        partCategory.put("/myjz/",28);//免疫接种
        partCategory.put("/qnkf/",29);//潜能开发
        partCategory.put("/zce/",30);//早产儿
        partCategory.put("/jtjy/",31);//家庭教育
        partCategory.put("/xlxwyy/",32);//心理行为语言
        partCategory.put("/yspy/",33);//艺术培养
        partCategory.put("/yy4/",34);//英语
        partCategory.put("/yd1/",35);//阅读
        partCategory.put("/wj3/",36);//玩具
        partCategory.put("/xxjy/",37);//学校教育
        partCategory.put("/yey/",38);//幼儿园
    }

    public SinaKnowledgeCrawler(String crawlPath) {
        super(crawlPath);
    }

    @Override
    protected void init(){
        //主题
        for(String themeReg : ConfLoader.templatesMap.keySet()){
            regexRule.addPositive(themeReg);
        }
        //分页
        for(String pagingReg : ConfLoader.pagingRegexList){
            regexRule.addPositive(pagingReg);
        }
        try {
            jdbcTemplate = JDBCHelper.createMysqlTemplate("sinaknowledge",
                    "jdbc:mysql://localhost/pro-baby?useUnicode=true&characterEncoding=utf8",
                    "root", "1234", 5, 30);
        } catch (Exception ex) {
            jdbcTemplate = null;
            LOG.error("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }

        //初始化抽取器
        for(String themeReg : ConfLoader.templatesMap.keySet()){
            extractors.put(themeReg,new SinaKnowledgeExtractor(ConfLoader.templatesMap.get(themeReg)));
        }
    }

    @Override
    protected void extractAndSave(String matchedTheme,Page page){
        Extractor extractor = extractors.get(matchedTheme);
        if(extractor != null){
            Object obj = extractor.extractMainContent(page);
            if(null == obj)
                return;
            Article article = (Article)obj;
            if(article.getMainContext() == null || article.getMainContext().isEmpty()
                    || article.getTitle() == null || article.getTitle().isEmpty()){
                return;
            }
            Integer age = 0;//其他年龄
            Integer category = 0;//其他
            String parentUrl = ((SinaKnowledgeExtractor)extractor).getCategoryAndAgeUrl();
            if(parentUrl != null) {
                for (String part : partAge.keySet()) {
                    if (parentUrl.contains(part)) {
                        age = partAge.get(part);
                        break;
                    }
                }
                for (String part : partCategory.keySet()) {
                    if (parentUrl.contains(part)) {
                        category = partCategory.get(part);
                        break;
                    }
                }
            }
                /*将数据插入mysql*/
            int updates=jdbcTemplate.update("insert into t_knowledge (age_sectionid,author,categoryid,crawl_date,create_date,html_content,pv,title,url) value(?,?,?,?,?,?,?,?,?)",
                    age, article.getAuthor(), category, dateFormat.format(new Date()),
                    article.getPublishDate(), article.getMainContext(), 0,
                    article.getTitle(), article.getUrl());
            if(updates==1){
                LOG.info("url:" + article.getUrl() + "解析并入库成功!");
            }
        }
    }

    public static void main(String []args){
        //System.out.println(dateFormat.format(new Date()));

        SinaKnowledgeCrawler crawler = new SinaKnowledgeCrawler(
                ConfLoader.getProperty(ConfConstant.CrawldbPath,"crawldb/sinaknowledge"));
        crawler.setForcedSeeds(ConfLoader.seedsList);
        crawler.setResumable(Boolean.valueOf(ConfLoader.getProperty(ConfConstant.Resumable,"true")));
        crawler.setThreads(Integer.valueOf(ConfLoader.getProperty(ConfConstant.Threads,"10")));
        try {
            crawler.start(Integer.valueOf(ConfLoader.getProperty(ConfConstant.Depth,"3")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
