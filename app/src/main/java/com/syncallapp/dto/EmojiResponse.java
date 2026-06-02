package com.syncallapp.dto;

import java.util.List;

public class EmojiResponse {
    private String name;
    private String category;
    private String group;
    private List<String> htmlCode;
    private List<String> unicode;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getGroup() {
        return group;
    }

    public List<String> getHtmlCode() {
        return htmlCode;
    }

    public List<String> getUnicode() {
        return unicode;
    }
}