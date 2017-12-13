package ht.msc.util;

import org.springframework.dao.DataAccessException;

public class ValidException extends DataAccessException{
	private static final long serialVersionUID = 1L;

    public ValidException(String msg) {
        super(msg);
    }

    public ValidException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
