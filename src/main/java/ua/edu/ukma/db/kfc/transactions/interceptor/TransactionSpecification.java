package ua.edu.ukma.db.kfc.transactions.interceptor;

import ua.edu.ukma.db.kfc.transactions.TransactionIsolation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionSpecification {

    boolean readOnly() default false;

    TransactionIsolation isolation() default TransactionIsolation.READ_COMMITTED;
}
