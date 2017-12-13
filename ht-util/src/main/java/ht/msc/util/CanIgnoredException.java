package ht.msc.util;

import org.springframework.dao.DataAccessException;

public class CanIgnoredException extends DataAccessException{
	private static final long serialVersionUID = 1L;

    public CanIgnoredException(String msg) {
        super(msg);
    }

    public CanIgnoredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
