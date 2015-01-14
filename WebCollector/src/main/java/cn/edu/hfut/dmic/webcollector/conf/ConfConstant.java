package cn.edu.hfut.dmic.webcollector.conf;

/**
 * Created by leilongyan on 2015/1/14.
 */
public interface ConfConstant {
    static final String ThemeTemplateSplit = "%%%";//主题模板的分隔符
    static final String CommentFlag = "#";//dat文件的注释的标识

    //每个爬虫的通用配置文件
    static final String SeedFile = "seeds.dat";
    static final String PagingRegexFile = "pagingregex.dat";
    static final String TemplatesFile = "templates.dat";

    //crawl.properties文件配置
    static final String CrawlName = "crawlname";
    static final String CrawldbPath = "crawldbpath";
    static final String Depth = "depth";
    static final String Resumable = "resumable";
    static final String Threads = "threads";
}
