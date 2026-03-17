package core.common.exception;

import core.common.Status;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException{
    private Status status;

    public CommonException(Status status) {
        super();
        this.status = status;
    }
}
