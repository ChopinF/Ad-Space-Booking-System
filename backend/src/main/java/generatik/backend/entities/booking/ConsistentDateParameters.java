package generatik.backend.entities.booking;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = ConsistentDateParameterValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface ConsistentDateParameters {

  String message() default "End date must be after begin date and both must be in the future";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
