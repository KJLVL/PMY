package com.example.myaquarium.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Theme {
    private String id;
    private String title;
    private String author;
    private String date;
    private String city;
    private String categoryId;
    private String content;
    private String images;
    private String userPhone;
    private String sections;
    private String categoryTitle;
}
