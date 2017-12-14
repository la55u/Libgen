package scenehub.libgen;

import java.io.Serializable;

public class Book implements Serializable{
    
    private String id;
    private String title;
    private String author;
    private String year;
    private String publisher;
    private long filesize;
    private String extension;
    private String pages;
    private String coverurl;
    private String md5;
    private String edition;
    private String downloadUrl;

    public Book(String id, String title, String author, String year, String publisher, long fileSize, String extension,
                String pages, String coverUrl, String md5, String edition, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.filesize = fileSize;
        this.extension = extension;
        this.pages = pages;
        this.coverurl = coverUrl;
        this.md5 = md5;
        this.edition = edition;
        this.downloadUrl = downloadUrl;
    }



    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }

    public long getFilesize() {
        return filesize;
    }

    public String getExtension() {
        return extension;
    }

    public String getPages() {
        return pages;
    }

    public String getCoverurl() { return coverurl; }

    public String getMd5() { return md5; }

    public String getDownloadUrl() { return downloadUrl; }

    public String getEdition() {
        return edition;
    }

    public String getId() { return id; }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}