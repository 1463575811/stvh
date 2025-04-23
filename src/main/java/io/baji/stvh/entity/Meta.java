package io.baji.stvh.entity;

import lombok.Data;

import java.util.List;

@Data
public class Meta {

    private String title;
    private String icon;
    private List<String> roles;
    private Boolean affix;
}
