package com.syncallapp.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private int number;

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getNumber() {
        return number;
    }
}
