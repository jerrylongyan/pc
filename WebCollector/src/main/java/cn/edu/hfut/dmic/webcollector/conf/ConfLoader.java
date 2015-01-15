package cn.edu.hfut.dmic.webcollector.conf;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jerry on 2015/1/13.
 */
public class ConfLoader implements ConfConstant {
    public static final Logger LOG = LoggerFactory.getLogger(ConfLoader.class);
    public static PropertiesConfiguration prop = null;
    public static ArrayList<String> seedsList = new ArrayList<String>();
    public static ArrayList<String> pagingRegexList = new ArrayList<String>();
    public static HashMap<String,HashMap<String,String>> templatesMap = new HashMap<String, HashMap<String,String>>();

    static {
        loadConf();
    }

    public static void loadConf(){
        LOG.info("loading crawler conf...");
        try {
            prop = new PropertiesConfiguration("crawl.properties");
            FileChangedReloadingStrategy strategy  =new FileChangedReloadingStrategy(){
                public void reloadingPerformed(){
                    super.reloadingPerformed();
                    reloadConf();
                    LOG.info("properties file reloading...");
                }
            };
            prop.setReloadingStrategy(strategy);

            reloadConf();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadConf(){
        String confPath = getProperty(CrawlName,"");
        analyseConf(confPath + "/" + SeedFile, seedsList);//种子
        analyseConf(confPath + "/" + PagingRegexFile, pagingRegexList);//分页

        //模板
        templatesMap.clear();
        ArrayList<String> templatesList = new ArrayList<String>();
        templatesList = analyseConf(confPath + "/" + TemplatesFile, templatesList);//模板
        for(String regexTps : templatesList){
            String []tpsFiles = regexTps.split(ThemeTemplateSplit);
            if(tpsFiles.length <= 1){
                continue;
            }
            try {
                File file = new File(ConfLoader.class.getClassLoader().getResource(confPath + "/" + tpsFiles[1]).toURI());
                if(!file.exists()){
                    LOG.error(confPath + "/" + tpsFiles[1] + " not exists!");
                    continue;
                }
                HashMap<String,String> tpsRegexs = new HashMap<String, String>();
                List<String> regexs = FileUtils.readLines(file);
                for(String regex : regexs){
                    int index = regex.indexOf('=');
                    tpsRegexs.put(regex.substring(0,index).trim(),regex.substring(index + 1).trim());
                }
                templatesMap.put(tpsFiles[0],tpsRegexs);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> analyseConf(String filePath, ArrayList<String> propSet){
        propSet.clear();
        try {
            File file = new File(ConfLoader.class.getClassLoader().getResource(filePath).toURI());
            if(!file.exists()){
                LOG.error(filePath + " not exists!");
                return null;
            }
            List<String> confs = FileUtils.readLines(file);
            for(String conf : confs){
                if(conf.startsWith(ConfConstant.CommentFlag)){//去掉注释
                    continue;
                }
                if(!conf.trim().equals("")) {
                    propSet.add(conf.trim());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propSet;
    }

    public static String getProperty(String key, String defaultValue){
        if(prop == null){
            return defaultValue;
        }
        Object proValue = prop.getProperty(key);
        if(null != proValue){
            return proValue.toString();
        }
        return defaultValue;
    }

    public static void main(String []args){
        System.out.println(ConfLoader.seedsList);
        System.out.println(ConfLoader.pagingRegexList);
        System.out.println(ConfLoader.templatesMap);
        //System.out.println(ConfLoader.seedsSet);
    }
}
