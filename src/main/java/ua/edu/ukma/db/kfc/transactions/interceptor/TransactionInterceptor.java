package ua.edu.ukma.db.kfc.transactions.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import lombok.RequiredArgsConstructor;
import ua.edu.ukma.db.kfc.transactions.TransactionManager;

@TransactionSpecification
@RequiredArgsConstructor(onConstructor_ = @jakarta.inject.Inject)
public class TransactionInterceptor {

    private final TransactionManager transactionManager;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (transactionManager.isTransactionActive()) return context.proceed();
        try {
            TransactionSpecification transactionSpecification = getTransactionSpecification(context);
            transactionManager.beginTransaction(transactionSpecification.readOnly(), transactionSpecification.isolation());
            Object result = context.proceed();
            transactionManager.commitCurrent();
            return result;
        } catch (Exception e) {
            transactionManager.rollbackCurrent();
            throw e;
        }
    }

    private TransactionSpecification getTransactionSpecification(InvocationContext context) {
        TransactionSpecification transactionSpecification = context.getMethod().getAnnotation(TransactionSpecification.class);
        if (transactionSpecification == null)
            transactionSpecification = context.getTarget().getClass().getAnnotation(TransactionSpecification.class);
        if (transactionSpecification == null)
            transactionSpecification = this.getClass().getAnnotation(TransactionSpecification.class);
        return transactionSpecification;
    }
}
