package com.basic.miniPjt5.service;

import com.basic.miniPjt5.DTO.GenreDTO;
import com.basic.miniPjt5.entity.Genre;
import com.basic.miniPjt5.mapper.GenreMapper;
import com.basic.miniPjt5.repository.DramaRepository;
import com.basic.miniPjt5.repository.GenreRepository;
import com.basic.miniPjt5.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final DramaRepository dramaRepository;
    private final GenreMapper genreMapper;
    private final TMDBGenreService tmdbGenreService;

    public GenreService(GenreRepository genreRepository,
                        MovieRepository movieRepository,
                        DramaRepository dramaRepository,
                        GenreMapper genreMapper,
                        TMDBGenreService tmdbGenreService) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.dramaRepository = dramaRepository;
        this.genreMapper = genreMapper;
        this.tmdbGenreService = tmdbGenreService;
    }

    // 모든 장르 조회 (상세 정보 포함)
    public List<GenreDTO.Response> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper::toResponse)
                .collect(Collectors.toList());
    }

    // 장르 목록 조회 (간단한 정보)
    public List<GenreDTO.ListResponse> getGenreList() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper::toListResponse)
                .collect(Collectors.toList());
    }

    // 장르 상세 조회
    public GenreDTO.Response getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));

        return genreMapper.toResponse(genre);
    }

    // 장르 생성 (관리자용)
    @Transactional
    public GenreDTO.Response createGenre(GenreDTO.CreateRequest request) {
        if (genreRepository.existsById(request.getId())) {
            throw new DuplicateContentException("이미 존재하는 장르입니다: " + request.getId());
        }

        Genre genre = Genre.builder()
                .id(request.getId())
                .name(request.getName())
                .build();

        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toResponse(savedGenre);
    }

    // 장르 수정 (관리자용)
    @Transactional
    public GenreDTO.Response updateGenre(Long id, GenreDTO.UpdateRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));

        genre.setName(request.getName());

        Genre updatedGenre = genreRepository.save(genre);
        return genreMapper.toResponse(updatedGenre);
    }

    // 장르 삭제 (관리자용 - 주의: 연관된 컨텐츠가 있으면 삭제 불가)
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));

        // 연관된 영화나 드라마가 있는지 확인
        if (!genre.getMovies().isEmpty() || !genre.getDramas().isEmpty()) {
            throw new RuntimeException("연관된 컨텐츠가 있어 삭제할 수 없습니다");
        }

        genreRepository.delete(genre);
    }

    // 장르 통계 조회
    public GenreDTO.Statistics getGenreStatistics(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));

        int movieCount = genre.getMovies().size();
        int dramaCount = genre.getDramas().size();

        double avgMovieRating = genre.getMovies().stream()
                .mapToDouble(movie -> movie.getVoteAverage())
                .average()
                .orElse(0.0);

        double avgDramaRating = genre.getDramas().stream()
                .mapToDouble(drama -> drama.getVoteAverage())
                .average()
                .orElse(0.0);

        double overallAvgRating = 0.0;
        if (movieCount > 0 && dramaCount > 0) {
            overallAvgRating = (avgMovieRating + avgDramaRating) / 2;
        } else if (movieCount > 0) {
            overallAvgRating = avgMovieRating;
        } else if (dramaCount > 0) {
            overallAvgRating = avgDramaRating;
        }

        int totalReviews = genre.getMovies().stream()
                .mapToInt(movie -> movie.getReviews().size())
                .sum() +
                genre.getDramas().stream()
                        .mapToInt(drama -> drama.getReviews().size())
                        .sum();

        return GenreDTO.Statistics.builder()
                .id(genre.getId())
                .name(genre.getName())
                .movieCount(movieCount)
                .dramaCount(dramaCount)
                .totalCount(movieCount + dramaCount)
                .averageMovieRating(movieCount > 0 ? avgMovieRating : null)
                .averageDramaRating(dramaCount > 0 ? avgDramaRating : null)
                .overallAverageRating(overallAvgRating)
                .totalReviews(totalReviews)
                .build();
    }

    // 장르별 인기 컨텐츠 조회
    public GenreDTO.PopularContent getPopularContentByGenre(Long id, int limit) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(id));

        List<GenreDTO.MovieSummary> topMovies = genre.getMovies().stream()
                .sorted((m1, m2) -> Double.compare(m2.getVoteAverage(), m1.getVoteAverage()))
                .limit(limit)
                .map(movie -> GenreDTO.MovieSummary.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .posterPath(movie.getPosterPath())
                        .voteAverage(movie.getVoteAverage())
                        .build())
                .collect(Collectors.toList());

        List<GenreDTO.DramaSummary> topDramas = genre.getDramas().stream()
                .sorted((d1, d2) -> Double.compare(d2.getVoteAverage(), d1.getVoteAverage()))
                .limit(limit)
                .map(drama -> GenreDTO.DramaSummary.builder()
                        .id(drama.getId())
                        .title(drama.getTitle())
                        .posterPath(drama.getPosterPath())
                        .voteAverage(drama.getVoteAverage())
                        .numberOfSeasons(drama.getNumberOfSeasons())
                        .build())
                .collect(Collectors.toList());

        return GenreDTO.PopularContent.builder()
                .genreId(genre.getId())
                .genreName(genre.getName())
                .topMovies(topMovies)
                .topDramas(topDramas)
                .build();
    }

    // TMDB 장르 동기화
    @Transactional
    public List<GenreDTO.Response> syncGenresFromTMDB() {
        return tmdbGenreService.syncGenresFromTMDB();
    }

    // 장르명으로 검색
    public List<GenreDTO.ListResponse> searchGenresByName(String name) {
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(name);
        return genres.stream()
                .map(genreMapper::toListResponse)
                .collect(Collectors.toList());
    }
}