package cn.edu.hfut.dmic.webcollector.conf;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jerry on 2015/1/13.
 */
public class ConfLoader {
    public static final Logger LOG = LoggerFactory.getLogger(ConfLoader.class);
    public static PropertiesConfiguration prop = null;

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

    }
}
