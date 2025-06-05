package com.basic.miniPjt5.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.basic.miniPjt5.entity.Drama;
import com.basic.miniPjt5.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "콘텐츠 간단 정보 DTO")
@Data
@AllArgsConstructor
public class ContentSimpleDTO {

    @Schema(description = "콘텐츠 ID", example = "1")
    private Long id;

    @Schema(description = "콘텐츠 제목", example = "아바타: 물의 길")
    private String title;

    @Schema(description = "포스터 URL", example = "https://image.tmdb.org/t/p/w500/poster.jpg")
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