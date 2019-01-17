package botapi.types;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2019-01-04
 * Time: 16:35
 * <p>
 * **************************************************************
 */
public class Document {
    private String title;
    private String desc;
    private Photo thumb;
    @JSONField(name = "file_name")
    private String fileName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Photo getThumb() {
        return thumb;
    }

    public void setThumb(Photo thumb) {
        this.thumb = thumb;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
