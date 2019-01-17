package botapi.types;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-30
 * Time: 00:31
 * <p>
 * **************************************************************
 */
public class Photo {
    @JSONField(name = "file_name")
    private String fileName;
    private int width;
    private int height;

    @JSONField(name = "file_size")
    private int fileSize = 0;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
