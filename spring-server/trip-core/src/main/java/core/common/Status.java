package core.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Status {
    SUCCESS("SU", "Success", HttpStatus.OK),
    NOT_FOUND_MEMBER("NF", "Not Found Member", HttpStatus.NOT_FOUND),
    UN_CORRECT_PASSWORD("UC", "Un correct Password", HttpStatus.UNAUTHORIZED),
    VALIDATION_FAILED("VF", "Validation Failed", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED("MN", "Method not supported", HttpStatus.METHOD_NOT_ALLOWED),
    UPLOAD_FAILED("UF", "Upload Failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_ERROR("DE", "Database Error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_AUTHORIZED("NA", "Not Authorized", HttpStatus.FORBIDDEN),
    MEMBER_EXISTS("ME", "Member already exists", HttpStatus.CONFLICT),
    NOT_FOUND_CERTIFICATION("CR", "Not Found Certification", HttpStatus.NOT_FOUND),
    NOT_VALID_EMAIL("NE", "Not Valid Email", HttpStatus.BAD_REQUEST),
    NOT_CORRECT_CERTIFICATION("NC", "Not correct Certification", HttpStatus.BAD_REQUEST),
    INVALID_JWT_TOKEN("IV", "Invalid JWT Token", HttpStatus.UNAUTHORIZED),
    ALREADY_PAYMENT_REQUEST("AP", "Already Payment Request", HttpStatus.CONFLICT),
    ALREADY_CANCEL_REQUEST("AC", "Already CANCEL Request", HttpStatus.CONFLICT),
    PAYMENT_SERVER_ERROR("PS", "Payment Server Error", HttpStatus.BAD_GATEWAY),
    NOT_FOUND_TEMP_PAYMENT("NT", "Not Found Temp Payment", HttpStatus.NOT_FOUND),
    SSE_SEND_ERROR("SS", "Sse Send Error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND_DESTINATION("NF", "Not Found Destination", HttpStatus.NOT_FOUND),
    NOT_FOUND_IMAGE("NI", "Not Found Image", HttpStatus.NOT_FOUND),
    NOT_FOUND_GROUP("NG", "Not Found Group", HttpStatus.NOT_FOUND),
    ALREADY_PARTICIPATE_GROUP("GG", "Already Participate Group", HttpStatus.CONFLICT),
    NOT_PARTICIPATE_GROUP("GF", "Not Participate Group", HttpStatus.BAD_REQUEST),
    NOT_FOUND_KEEP("NF", "Not Fount Keep", HttpStatus.NOT_FOUND),
    AUTHENTICATION_FAILED("AF", "Authentication Failed", HttpStatus.UNAUTHORIZED),
    REDIS_SERVER_ERROR("RS", "Redis Server Error", HttpStatus.BAD_GATEWAY),
    NOT_FOUND_COURSE("NF", "Not Found Course", HttpStatus.NOT_FOUND),
    NOT_FOUND_COURSE_LIKE("NF", "Course Like Not Found", HttpStatus.NOT_FOUND),
    ALREADY_LIKED_COURSE("AL", "Already Liked Course", HttpStatus.CONFLICT),
    ALREADY_EXISTS_REVIEW("AR", "Already Exists Review", HttpStatus.CONFLICT),
    NOT_FOUND_COMMENT("NC", "Not Found Comment", HttpStatus.NOT_FOUND),
    NOT_FOUND_POINT("NP", "Not Found Point", HttpStatus.NOT_FOUND),
    NOT_FOUND_COMMENT_LIKE("NL", "Not Found Comment Like", HttpStatus.NOT_FOUND),
    ALREADY_COMMENT_LIKE("AC", "Already Comment Like", HttpStatus.CONFLICT),
    NOT_FOUND_RECEIPT_REVIEW("NR", "Not Found Receipt Review", HttpStatus.NOT_FOUND),
    NOT_FOUND_GROUP_COMMENT("GC", "Not Found Group Comment", HttpStatus.NOT_FOUND),
    NOT_FOUND_ENROLL("NE", "Not Found Enroll", HttpStatus.NOT_FOUND),
    ALREADY_GROUP_LIKE("AG", "Already Group Like", HttpStatus.CONFLICT),
    NOT_FOUND_GROUP_LIKE("GL", "Not Found Group Like", HttpStatus.NOT_FOUND),
    NOT_FOUND_PAYMENT("NP", "Not Found Payment", HttpStatus.NOT_FOUND),
    NOT_VALID_DEPTH("VD", "Not Valid Depth", HttpStatus.BAD_REQUEST),
    NULL_PATH("NP", "Path Cannot Be Null", HttpStatus.BAD_REQUEST),
    GET_COURSES_LIKED("SUCCESS", "Liked Courses Retrieved Successfully", HttpStatus.OK),
    GET_DESTINATIONS_BY_CONTENT_ID("SUCCESS", "Destinations Retrieved By Content Id Successfully", HttpStatus.OK),
    ALREADY_EXIST_PAYMENT("PP","Already Process Payment",HttpStatus.BAD_REQUEST);
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
