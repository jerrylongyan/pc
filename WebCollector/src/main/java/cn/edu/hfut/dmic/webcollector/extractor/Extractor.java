package cn.edu.hfut.dmic.webcollector.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.HashMap;

/**
 * Created by leilongyan on 2015/1/15.
 */
public abstract class Extractor {
    public abstract Object extractMainContent(Page page);
}
