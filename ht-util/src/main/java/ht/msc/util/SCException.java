package ht.msc.util;

import org.springframework.dao.DataAccessException;

public class SCException extends DataAccessException{

    private static final long serialVersionUID = 1L;

    public SCException(String msg) {
        super(msg);
    }

    public SCException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
