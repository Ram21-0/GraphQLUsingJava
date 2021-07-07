package friends2.queries.instrumentation;

import friends2.queries.data.DataRepositoryWithoutDataLoaders;
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
public class InstrumentationWithoutDataLoaders extends SimpleInstrumentation {

    public static final String CORRELATION_ID = "correlation_id";

    DataRepositoryWithoutDataLoaders repository = DataRepositoryWithoutDataLoaders.getInstance();

    //    ExecutionStrategy

//    private final Clock clock;

//    @Autowired
//    public MyInstrumentation(Clock clock) {
//        this.clock = clock;
//    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters parameters) {
//        var start = Instant.now(clock);
        var start = System.nanoTime();
        // Add the correlation ID to the NIO thread
        MDC.put(CORRELATION_ID, parameters.getExecutionInput().getExecutionId().toString());


//        log.info("Query: {} with variables: {}", parameters.getQuery(), parameters.getVariables());
        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
//            var duration = Duration.between(start, System.nanoTime());
            var duration = System.nanoTime() - start;
            if (throwable == null) {
                log.info("{} Completed successfully in: {} ns  {} ms", parameters.getOperation(), duration, duration/1e6);
            } else {
                log.warn("Failed in: {}", duration, throwable);
            }
            // If we have async resolvers, this callback can occur in the thread-pool and not the NIO thread.
            // In that case, the LoggingListener will be used as a fallback to clear the NIO thread.
            MDC.clear();
        });
    }

}