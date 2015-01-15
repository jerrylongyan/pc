package cn.edu.hfut.dmic.webcollector.extractor;

/**
 * Created by leilongyan on 2015/1/15.
 */
public class Article {
    private String title;
    private String mainContext;
    private String publishDate;
    private String url;
    private String author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainContext() {
        return mainContext;
    }

    public void setMainContext(String mainContext) {
        this.mainContext = mainContext;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
