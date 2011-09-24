package org.ephman.abra.validation;

/**
 * ValidatorCode class - base for error code classes with validators..
 * has LENGTH_FAILED, MANDATORY_FAILED
 *
 * @author: Paul Bethe
 * @version 0.1
 */


public interface ValidationCodes {

    public final static int LENGTH_FAILED = 42;
    public final static int MANDATORY_FAILED = 43;
    public final static int REGEXP_FAILED = 44;

}
