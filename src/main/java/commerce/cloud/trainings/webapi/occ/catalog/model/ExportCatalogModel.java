package commerce.cloud.trainings.webapi.occ.catalog.model;

import java.io.InputStream;


/**
 * @author Nick Mandrik
 */


public class ExportCatalogModel {

    private InputStream content;
    private Long contentLength;
    private String contentDisposition;

    public ExportCatalogModel(InputStream content, Long contentLength, String contentDisposition) {
        this.content = content;
        this.contentLength = contentLength;
        this.contentDisposition = contentDisposition;
    }

    public boolean isValid() {
        boolean isValid = false;
        if(contentLength != null && content != null && contentDisposition != null) {
            isValid = true;
        }
        return isValid;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportCatalogModel that = (ExportCatalogModel) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (contentLength != null ? !contentLength.equals(that.contentLength) : that.contentLength != null)
            return false;
        return contentDisposition != null ? contentDisposition.equals(that.contentDisposition) : that.contentDisposition == null;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (contentLength != null ? contentLength.hashCode() : 0);
        result = 31 * result + (contentDisposition != null ? contentDisposition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExportCatalogModel{" +
                "content=" + content +
                ", contentLength=" + contentLength +
                ", contentDisposition='" + contentDisposition + '\'' +
                '}';
    }
}
