package com.lwq.richedittext.super_editext.model;

/**
 * Created by lwq on 2017/6/26.
 */

public class JsonData {
    //数据类型 1文本 2图片 3回车
    private int type = 1;
    private TextData textData = null;
    private ImgData imgData = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TextData getTextData() {
        return textData;
    }

    public void setTextData(TextData textData) {
        this.textData = textData;
    }

    public ImgData getImgData() {
        return imgData;
    }

    public void setImgData(ImgData imgData) {
        this.imgData = imgData;
    }


    @Override
    public String toString() {
        return "JsonData{" +
                "type=" + type +
                ", textData=" + textData +
                ", imgData=" + imgData +
                '}';
    }

    public static class TextData{
        private String text;
//        private int textSize = 18;
//        //字体样式 0正常 1加粗 2斜体 3加粗加斜体
//        private int typeface = 0;
//        private int textColor = 0xff404040;
        private String url = "";
        private PaintBrush paintBrush=null;

        public PaintBrush getPaintBrush() {
            return paintBrush;
        }

        public void setPaintBrush(PaintBrush paintBrush) {
            this.paintBrush = paintBrush;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "TextData{" +
                    "text='" + text + '\'' +
                    ", url='" + url + '\'' +
                    ", paintBrush=" + paintBrush +
                    '}';
        }

        //        public int getTextColor() {
//            return textColor;
//        }
//
//        public void setTextColor(int textColor) {
//            this.textColor = textColor;
//        }
//
//        public int getTextSize() {
//            return textSize;
//        }
//
//        public void setTextSize(int textSize) {
//            this.textSize = textSize;
//        }
//
//        public int getTypeface() {
//            return typeface;
//        }
//
//        public void setTypeface(int typeface) {
//            this.typeface = typeface;
//        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ImgData{
        private String loclPath;
        private String urlPath;

        public String getLoclPath() {
            return loclPath;
        }

        public void setLoclPath(String loclPath) {
            this.loclPath = loclPath;
        }

        public String getUrlPath() {
            return urlPath;
        }

        public void setUrlPath(String urlPath) {
            this.urlPath = urlPath;
        }
    }

}
