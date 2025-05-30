//ErrorCode enum 상수정의
package com.basic.miniPjt5.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 리뷰 관련 에러코드
    REVIEW_NOT_FOUND("REVIEW_404", "리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS("REVIEW_409", "이미 해당 작품에 리뷰를 작성했습니다.", HttpStatus.CONFLICT),
    REVIEW_ACCESS_DENIED("REVIEW_403", "리뷰를 수정/삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    REVIEW_CONTENT_INVALID("REVIEW_400", "리뷰 내용이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 별점 관련 에러코드
    RATING_NOT_FOUND("RATING_404", "별점을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RATING_ALREADY_EXISTS("RATING_409", "이미 해당 작품에 별점을 주었습니다.", HttpStatus.CONFLICT),
    RATING_ACCESS_DENIED("RATING_403", "별점을 수정/삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RATING_SCORE_INVALID("RATING_400", "별점은 1~10 사이의 값이어야 합니다.", HttpStatus.BAD_REQUEST),

    // 작품 관련 에러코드
    MOVIE_NOT_FOUND("MOVIE_404", "영화를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DRAMA_NOT_FOUND("DRAMA_404", "드라마를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CONTENT_TYPE_INVALID("CONTENT_400", "영화 또는 드라마 중 하나만 선택해야 합니다.", HttpStatus.BAD_REQUEST),

    // 사용자 관련 에러코드
    USER_NOT_FOUND("USER_404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
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

    // 대출 관련 (40X)
    LOAN_NOT_FOUND("LOAN_001", "대출 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    LOAN_EXCEED_LIMIT("LOAN_002", "최대 대출 권수를 초과했습니다", HttpStatus.BAD_REQUEST),
    LOAN_MEMBER_OVERDUE("LOAN_003", "연체 중인 도서가 있어 대출할 수 없습니다", HttpStatus.BAD_REQUEST),
    LOAN_ALREADY_BORROWED("LOAN_004", "이미 대출 중인 도서입니다", HttpStatus.CONFLICT),
    LOAN_INVALID_STATUS("LOAN_005", "잘못된 대출 상태입니다", HttpStatus.BAD_REQUEST),
    LOAN_CANNOT_RETURN("LOAN_006", "반납할 수 없는 상태입니다", HttpStatus.BAD_REQUEST),

    // 예약 관련 (40X)
    RESERVATION_NOT_FOUND("RESERVATION_001", "예약 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    RESERVATION_DUPLICATE("RESERVATION_002", "이미 예약한 도서입니다", HttpStatus.CONFLICT),
    RESERVATION_LIMIT_EXCEEDED("RESERVATION_003", "예약 한도를 초과했습니다", HttpStatus.BAD_REQUEST),

    // 카테고리 관련 (40X)
    CATEGORY_NOT_FOUND("CATEGORY_001", "카테고리를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    CATEGORY_HAS_BOOKS("CATEGORY_002", "도서가 등록된 카테고리는 삭제할 수 없습니다", HttpStatus.CONFLICT),

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