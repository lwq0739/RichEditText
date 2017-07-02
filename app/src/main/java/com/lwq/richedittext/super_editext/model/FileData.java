package com.lwq.richedittext.super_editext.model;

/**
 * User:lwq
 * Date:2017-06-30
 * Time:18:33
 * introduction:
 */
public class FileData {
    private String file_path;
    private String file_title;

    public FileData(String file_path, String file_title) {
        this.file_path = file_path;
        this.file_title = file_title;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_title() {
        return file_title;
    }

    public void setFile_title(String file_title) {
        this.file_title = file_title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileData)) return false;

        FileData fileData = (FileData) o;

        return getFile_path().equals(fileData.getFile_path());

    }

}
