package com.campus.feedbacktool.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A small, stable wrapper around Spring Data's Page so the JSON shape the
 * frontend depends on never changes even if the underlying Page
 * implementation's serialization does.
 */
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse() {
    }

    public PageResponse(List<T> content, Page<?> source) {
        this.content = content;
        this.page = source.getNumber();
        this.size = source.getSize();
        this.totalElements = source.getTotalElements();
        this.totalPages = source.getTotalPages();
        this.last = source.isLast();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
