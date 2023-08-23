package com.techplanet.expertbridge.fusion.validation;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.PlainTextPayload;
import com.techplanet.expertbridge.fusion.security.AesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
public class ModelValidator {

//    private static final Gson gson = new Gson();
    @Autowired
    Gson gson;
    @Autowired
    AesService aesService;
    @Value("${aes.encryption.key}")
    private String aesEncryptionKey;

    /**
     * This method performs validation of the fields passed in model using the
     * javax.validation.constraints package.It is better used compared to the
     * verbose response provided by the spring framework.
     *
     * @param payload: Object
     * @param validationPayload
     * @return PlainTextPayload
     */
    public PlainTextPayload doModelValidation(Object payload, PlainTextPayload validationPayload) {
//        PlainTextPayload validationPayload = new PlainTextPayload();
//        validationPayload.setError(false);
//        validationPayload.setResponse(Strings.EMPTY);
//        validationPayload.setPlainTextPayload(Strings.EMPTY);

        List<Field> fields = List.of(payload.getClass().getDeclaredFields());
        List<String> errorMessageList = new ArrayList<>();

        fields.forEach(field -> {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(payload);
            } catch (IllegalAccessException e) {
                log.error("Error occurred while trying to get field value for model validation. Reason: {}", e.getMessage());
                throw new RuntimeException(e);
            }

            List<Annotation> annotations = List.of(field.getDeclaredAnnotations());
            for (Annotation annotation : annotations) {
                Class<?> annotationClass = annotation.annotationType();
                if (annotationClass.isAssignableFrom(AssertFalse.class)) {
                    if (fieldValue instanceof Boolean && Objects.equals((Boolean) fieldValue, Boolean.TRUE)) {
                        errorMessageList.add(field.getAnnotation(AssertFalse.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(AssertTrue.class)) {
                    if (fieldValue instanceof Boolean && Objects.equals((Boolean) fieldValue, Boolean.FALSE)) {
                        errorMessageList.add(field.getAnnotation(AssertTrue.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(Pattern.class)) {
                    Pattern pattern = field.getAnnotation(Pattern.class);
                    String regex = pattern.regexp();
                    if (fieldValue == null || !java.util.regex.Pattern.matches(regex, String.valueOf(fieldValue))) {
                        errorMessageList.add(field.getAnnotation(Pattern.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(NotNull.class)) {
                    if (fieldValue == null) {
                        errorMessageList.add(field.getAnnotation(NotNull.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(Null.class)) {
                    if (fieldValue != null) {
                        errorMessageList.add(field.getAnnotation(Null.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(NotEmpty.class)) {
                    if (fieldValue == null || String.valueOf(fieldValue).isEmpty()) {
                        errorMessageList.add(field.getAnnotation(NotEmpty.class).message());
                    }
                } else if (annotationClass.isAssignableFrom(NotBlank.class)) {
                    if (fieldValue == null || String.valueOf(fieldValue).isBlank()) {
                        errorMessageList.add(field.getAnnotation(NotBlank.class).message());
                    }
                }

            }
        });

        if (!errorMessageList.isEmpty()) {
            String completeErrorMessage = String.join(", ", errorMessageList);
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            exceptionResponse.setResponseMessage(completeErrorMessage);

            validationPayload.setError(true);
            GenericPayload responsePayload = new GenericPayload();
            responsePayload.setResponse(aesService.encrypt(gson.toJson(exceptionResponse), validationPayload.getAppUser()));
            validationPayload.setResponse(gson.toJson(responsePayload));
            validationPayload.setPlainTextPayload(gson.toJson(exceptionResponse));
            return validationPayload;
        }

        return validationPayload;
    }

}
