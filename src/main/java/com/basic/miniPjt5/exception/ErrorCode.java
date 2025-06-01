//ErrorCode enum 상수정의
package com.basic.miniPjt5.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE("C002", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("C003", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND("C004", "엔티티를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TYPE_VALUE("C005", "잘못된 타입 값입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("C006", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

    // 사용자 관련 에러
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    USER_ACCESS_DENIED("U003", "사용자 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD("U004", "잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),

    // 영화 관련 에러
    MOVIE_NOT_FOUND("M001", "영화를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MOVIE_ALREADY_EXISTS("M002", "이미 존재하는 영화입니다.", HttpStatus.CONFLICT),
    MOVIE_ACCESS_DENIED("M003", "영화 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

    // 드라마 관련 에러
    DRAMA_NOT_FOUND("D001", "드라마를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DRAMA_ALREADY_EXISTS("D002", "이미 존재하는 드라마입니다.", HttpStatus.CONFLICT),
    DRAMA_ACCESS_DENIED("D003", "드라마 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),

    // 장르 관련 에러
    GENRE_NOT_FOUND("G001", "장르를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    GENRE_ALREADY_EXISTS("G002", "이미 존재하는 장르입니다.", HttpStatus.CONFLICT),
    GENRE_HAS_CONTENT("G003", "연관된 컨텐츠가 있어 삭제할 수 없습니다.", HttpStatus.CONFLICT),

    // 리뷰 관련 에러
    REVIEW_NOT_FOUND("R001", "리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS("R002", "이미 리뷰를 작성하셨습니다.", HttpStatus.CONFLICT),
    REVIEW_ACCESS_DENIED("R003", "리뷰 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    REVIEW_CONTENT_INVALID("R004", "리뷰 내용이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 별점 관련 에러
    RATING_NOT_FOUND("RT001", "별점을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RATING_ALREADY_EXISTS("RT002", "이미 별점을 등록하셨습니다.", HttpStatus.CONFLICT),
    RATING_ACCESS_DENIED("RT003", "별점 접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    RATING_SCORE_INVALID("RT004", "별점은 1~10 사이여야 합니다.", HttpStatus.BAD_REQUEST),

    // 컨텐츠 관련 에러
    CONTENT_TYPE_INVALID("CT001", "컨텐츠 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    CONTENT_NOT_FOUND("CT002", "컨텐츠를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // TMDB API 관련 에러
    TMDB_API_ERROR("T001", "TMDB API 호출에 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    TMDB_API_RATE_LIMIT("T002", "TMDB API 호출 한도를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    TMDB_DATA_NOT_FOUND("T003", "TMDB에서 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 인증/인가 관련 에러
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("A002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("A003", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSION("A004", "권한이 부족합니다.", HttpStatus.FORBIDDEN),

    // 파일 관련 에러
    FILE_UPLOAD_ERROR("F001", "파일 업로드에 실패했습니다.", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("F002", "파일 크기가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("F003", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),

    USER_NOT_AUTHENTICATED("USER_401", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),

    // 인증/인가 관련 (40X)
    AUTH_INVALID_CREDENTIALS("AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_002", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED("AUTH_004", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_LOCKED("AUTH_005", "계정이 잠겨있습니다", HttpStatus.FORBIDDEN),

    // 회원 관련 (40X)
    MEMBER_EMAIL_DUPLICATE("MEMBER_002", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    MEMBER_SUSPENDED("MEMBER_003", "정지된 회원입니다", HttpStatus.FORBIDDEN),
    MEMBER_WITHDRAWN("MEMBER_004", "탈퇴한 회원입니다", HttpStatus.GONE),

    // 도서 관련 (40X)
    BOOK_NOT_FOUND("BOOK_001", "도서를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    BOOK_ISBN_DUPLICATE("BOOK_002", "이미 등록된 ISBN입니다", HttpStatus.CONFLICT),
    BOOK_NOT_AVAILABLE("BOOK_003", "대출할 수 없는 도서입니다", HttpStatus.BAD_REQUEST),
    BOOK_OUT_OF_STOCK("BOOK_004", "재고가 없습니다", HttpStatus.BAD_REQUEST),

    //Post 관련(40X)
    POST_NOT_FOUND("POST_001", "게시글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    POST_CODE_DUPLICATE("POST_002","이미 사용 중인 게시글 코드입니다", HttpStatus.CONFLICT),
    POST_ACCESS_DENIED("POST_003", "작성자가 아닙니다", HttpStatus.FORBIDDEN),

    //댓글 관련(40X)
    COMMENT_NOT_FOUND("COMMENT_001", "댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED ("COMMENT_002", "작성자가 아닙니다", HttpStatus.FORBIDDEN),

    // 검증 관련 (40X)
    VALIDATION_ERROR("VALID_001", "입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING("VALID_002", "필수 항목이 누락되었습니다", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("VALID_003", "형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),

    // 비즈니스 로직 관련 (40X)
    BUSINESS_RULE_VIOLATION("BIZ_001", "비즈니스 규칙 위반입니다", HttpStatus.BAD_REQUEST),
    OPERATION_NOT_ALLOWED("BIZ_002", "허용되지 않은 작업입니다", HttpStatus.BAD_REQUEST),

    // 서버 오류 (50X)
    INTERNAL_SERVER_ERROR("SERVER_001", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("SERVER_002", "데이터베이스 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_ERROR("SERVER_003", "외부 API 호출 중 오류가 발생했습니다", HttpStatus.BAD_GATEWAY),

    //이미지 업로드 오류
    FILE_UPLOAD_FAILED("IMAGE_001", "이미지 저장에 실패했습니다",  HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("IMAGE_002", "이미지 삭제에 실패했습니다",  HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}