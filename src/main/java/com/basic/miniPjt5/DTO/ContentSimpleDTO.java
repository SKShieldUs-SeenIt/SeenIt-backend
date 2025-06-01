package com.basic.miniPjt5.DTO;

import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentSimpleDTO {
    private Long id;
    private String title;
    private String posterPath;

    public static ContentSimpleDTO fromMovie(Movie movie) {
        return new ContentSimpleDTO(
            movie.getId(), movie.getTitle(), movie.getPosterPath()
        );
    }

    public static ContentSimpleDTO fromDrama(Drama drama) {
        return new ContentSimpleDTO(
            drama.getId(), drama.getTitle(), drama.getPosterPath()
        );
    }
}
