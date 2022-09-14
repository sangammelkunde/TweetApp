package com.tweetapp.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadFileVO {
    private String filename;
    private String fileType;
    private String fileSize;
    private byte[] file;
}

