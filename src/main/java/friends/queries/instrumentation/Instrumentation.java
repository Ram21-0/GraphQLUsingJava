package friends.queries.instrumentation;

import friends.queries.data.DataRepository;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.ExecutionStrategyInstrumentationContext;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
//import graphql.execution.instrumentation.nextgen.S;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.*;
import graphql.language.Document;
import graphql.schema.DataFetcher;
import graphql.validation.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Component
public class Instrumentation extends SimpleInstrumentation {
    public static final String CORRELATION_ID = "correlation_id";

    static int count = 0;

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        var start = System.nanoTime();

        MDC.put(CORRELATION_ID, parameters.getExecutionInput().getExecutionId().toString());

        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
            var duration = System.nanoTime() - start;
            if (throwable == null) {
                log.info("{} Completed successfully in: {} ns  {} ms", parameters.getOperation(), duration, duration/1e6);
            } else {
                log.warn("Failed in: {}", duration, throwable);
            }
//            MDC.clear();
        });
    }

    @Override
    public InstrumentationContext<Document> beginParse(InstrumentationExecutionParameters parameters) {
//        return super.beginParse(parameters);

        long start = System.nanoTime();
        return SimpleInstrumentationContext.whenCompleted((document, throwable) -> {
            var duration = System.nanoTime() - start;
            if (throwable == null) {
                log.info("{} : Parse in {} ns .. {} ms",parameters.getOperation(),duration,duration/1e6);
            }
            else {
                log.warn("Failed in: {}", duration, throwable);
            }
        });
    }
//
    @Override
    public InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
//        return super.beginValidation(parameters);

        long start = System.nanoTime();

        return SimpleInstrumentationContext.whenCompleted((validationErrors, throwable) -> {
            var duration = System.nanoTime() - start;
            if (throwable == null) {
                log.info("{} : validation in {} ns {} ms", parameters.getOperation(), duration, duration/1e6);
            } else {
                log.warn("Failed in: {}", duration, throwable);
            }
        });
    }
////
////    @Override
////    public ExecutionStrategyInstrumentationContext beginExecutionStrategy(InstrumentationExecutionStrategyParameters parameters) {
////        return super.beginExecutionStrategy(parameters);
//////        long start = System.nanoTime();
//////
////    }
//
//    @Override
//    public InstrumentationContext<ExecutionResult> beginExecuteOperation(InstrumentationExecuteOperationParameters parameters) {
////        return super.beginExecuteOperation(parameters);
//        long start = System.nanoTime();
//        System.out.print("beginExecutionOperation : ");
//        System.out.println(count++);
//
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
////
////    @Override
////    public InstrumentationContext<ExecutionResult> beginField(InstrumentationFieldParameters parameters) {
//////        return super.beginField(parameters);
////        var start = System.nanoTime();
////        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
////            var duration = System.nanoTime() - start;
////            if (throwable == null) {
////                log.info("{} field in {} ns .. {} ms",parameters.getField().getName(),duration,duration/1e6);
////            }
////            else {
////                log.warn("Failed in: {}", duration, throwable);
////            }
////        });
////    }
////
////    @Override
////    public InstrumentationContext<Object> beginFieldFetch(InstrumentationFieldFetchParameters parameters) {
//////        return super.beginFieldFetch(parameters);
////        long start = System.nanoTime();
////        return SimpleInstrumentationContext.whenCompleted((o, throwable) -> {
////            long duration = System.nanoTime() - start;
////            if (throwable == null) {
////                log.info("{} field fetch in {} ns .. {} ms",parameters.getField().getName(),duration,duration/1e6);
////            }
////            else {
////                log.warn("Failed in: {}", duration, throwable);
////            }
////        });
////    }
////
////
////    @Override
////    public InstrumentationContext<ExecutionResult> beginFieldComplete(InstrumentationFieldCompleteParameters parameters) {
//////        return super.beginFieldComplete(parameters);
////        long start = System.nanoTime();
////        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
////            long duration = System.nanoTime() - start;
////            if (throwable == null) {
////                log.info("{} beginFieldComplete in {} ns .. {} ms",parameters.getField().getName(),duration,duration/1e6);
////            }
////            else {
////                log.warn("Failed in: {}", duration, throwable);
////            }
////        });
////    }
////
////
////    @Override
////    public DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
////
////        return super.instrumentDataFetcher(dataFetcher, parameters);
////    }
}