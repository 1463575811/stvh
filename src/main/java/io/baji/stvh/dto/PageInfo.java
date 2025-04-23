package io.baji.stvh.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {

    private long currentPage;
    private long pageSize;
    private long total;

    private List<T> records;

}
