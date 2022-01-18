package com.kevin.linebotdemo.model;

import com.sun.istack.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor()
public class QueryDto {

    @NonNull
    private String city;
    @NonNull
    private String routeName;
    private String select = "";
    private String filter = "";
}
