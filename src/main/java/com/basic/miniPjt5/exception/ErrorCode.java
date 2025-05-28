//ErrorCode enum 상수정의
package com.basic.miniPjt5.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증/인가 관련 (40X)
    AUTH_INVALID_CREDENTIALS("AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_002", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED("AUTH_004", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_LOCKED("AUTH_005", "계정이 잠겨있습니다", HttpStatus.FORBIDDEN),

    // 회원 관련 (40X)
    MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
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
    POST_CODE_DUBPLICATE("POST_002","이미 사용 중인 게시글 코드입니다.", HttpStatus.CONFLICT),

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
    EXTERNAL_API_ERROR("SERVER_003", "외부 API 호출 중 오류가 발생했습니다", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}