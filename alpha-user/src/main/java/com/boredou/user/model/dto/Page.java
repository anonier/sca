package com.boredou.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Page implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long current;

    private Long size;
}
