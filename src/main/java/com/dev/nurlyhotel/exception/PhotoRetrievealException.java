package com.dev.nurlyhotel.exception;

/*
    Собственные исключения могут быть созданы для отображения конкретных ошибок или ситуаций,
    которые могут возникнуть в приложении.
 */
public class PhotoRetrievealException extends RuntimeException {
    public PhotoRetrievealException(String message) {
        super(message);
    }
}
