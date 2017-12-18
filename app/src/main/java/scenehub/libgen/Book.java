package scenehub.libgen;

import java.io.Serializable;

public class Book implements Serializable{
    
    private String ID;
    private String Title;
    private String Author;
    private String Year;
    private String Publisher;
    private long Filesize;
    private String Extension;
    private String Pages;
    private String Coverurl;
    private String MD5;
    private String Edition;
    private String Language;
    private String Scanned;
    private String downloadUrl;

    public Book(String id, String title, String author, String year, String publisher, long fileSize, String extension,
                String pages, String coverUrl, String md5, String edition, String language, String scanned, String downloadUrl) {
        this.ID = id;
        this.Title = title;
        this.Author = author;
        this.Year = year;
        this.Publisher = publisher;
        this.Filesize = fileSize;
        this.Extension = extension;
        this.Pages = pages;
        this.Coverurl = coverUrl;
        this.MD5 = md5;
        this.Edition = edition;
        this.Language = language;
        this.Scanned = scanned;
        this.downloadUrl = downloadUrl;
    }



    public String getTitle() {
        return Title;
    }

    public String getAuthor() {
        return Author;
    }

    public String getYear() {
        return Year;
    }

    public String getPublisher() {
        return Publisher;
    }

    public long getFilesize() {
        return Filesize;
    }

    public String getExtension() {
        return Extension;
    }

    public String getPages() {
        return Pages;
    }

    public String getCoverurl() { return Coverurl; }

    public String getMD5() { return MD5; }

    public String getDownloadUrl() { return downloadUrl; }

    public String getEdition() {
        return Edition;
    }

    public String getID() { return ID; }

    public String getLanguage() { return Language; }

    public String getScanned() { return Scanned; }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}