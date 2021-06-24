package friends.queries.instrumentation;

import friends.queries.data.DataRepository;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Instrumentation extends SimpleInstrumentation {

    public static final String CORRELATION_ID = "correlation_id";

//    private final Clock clock;

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        var start = System.nanoTime();

        MDC.put(CORRELATION_ID, parameters.getExecutionInput().getExecutionId().toString());

//        log.info("Query: {} with variables: {}", parameters.getQuery(), parameters.getVariables());

        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
            var duration = System.nanoTime() - start;
            if (throwable == null) {
                log.info("{} Completed successfully in: {} ns  {} ms", parameters.getOperation(), duration, duration/1e6);
                log.info("{} calls for {}", DataRepository.getCalls(), parameters.getOperation());
                DataRepository.resetCalls();
            } else {
                log.warn("Failed in: {}", duration, throwable);
            }
            MDC.clear();
        });
    }
//
//    @Override
//    public InstrumentationContext<Document> beginParse(InstrumentationExecutionParameters parameters) {
////        return super.beginParse(parameters);
//        long start = System.nanoTime();
////        System.out.println((parameters.getInstrumentationState().toString()));
//        return SimpleInstrumentationContext.whenCompleted((document, throwable) -> {
//            var duration = System.nanoTime() - start;
//            if (throwable == null) {
//                log.info("{} : Parse in {} ns .. {} ms",parameters.getOperation(),duration,duration/1e6);
//            }
//            else {
//                log.warn("Failed in: {}", duration, throwable);
//            }
//        });
//    }
//
//    @Override
//    public InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
////        return super.beginValidation(parameters);
//        long start = System.nanoTime();
//        return SimpleInstrumentationContext.whenCompleted((validationErrors, throwable) -> {
//            var duration = System.nanoTime() - start;
//            if (throwable == null) {
//                log.info("{} : validation in {} ns {} ms", parameters.getOperation(), duration, duration/1e6);
//            } else {
//                log.warn("Failed in: {}", duration, throwable);
//            }
//        });
//    }
//
//    @Override
//    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
//        return super.beginExecutionStrategy(parameters);
////        long start = System.nanoTime();
////
//    }
//
//    @Override
//    public InstrumentationContext<ExecutionResult> beginExecuteOperation(InstrumentationExecuteOperationParameters parameters) {
////        return super.beginExecuteOperation(parameters);
//        long start = System.nanoTime();
//        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
//            var duration = System.nanoTime() - start;
//            if (throwable == null) {
//                log.info("execute operation in {} ns .. {} ms",duration,duration/1e6);
//            }
//            else {
//                log.warn("Failed in: {}", duration, throwable);
//            }
//        });
//    }
//
//    @Override
//    public InstrumentationContext<ExecutionResult> beginField(InstrumentationFieldParameters parameters) {
////        return super.beginField(parameters);
//        var start = System.nanoTime();
//        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
//            var duration = System.nanoTime() - start;
//            if (throwable == null) {
//                log.info("{} field in {} ns .. {} ms",parameters.getField().getName(),duration,duration/1e6);
//            }
//            else {
//                log.warn("Failed in: {}", duration, throwable);
//            }
//        });
//    }
//
//    @Override
//    public InstrumentationContext<Object> beginFieldFetch(InstrumentationFieldFetchParameters parameters) {
////        return super.beginFieldFetch(parameters);
//        long start = System.nanoTime();
//        return SimpleInstrumentationContext.whenCompleted((o, throwable) -> {
//            var duration = System.nanoTime() - start;
//            if (throwable == null) {
//                log.info("{} field fetch in {} ns .. {} ms",parameters.getField().getName(),duration,duration/1e6);
//            }
//            else {
//                log.warn("Failed in: {}", duration, throwable);
//            }
//        });
//    }

}