package com.project2.domain.post.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageFile {
	String message() default "Only image files are allowed (jpg, jpeg, png, gif, webp)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
