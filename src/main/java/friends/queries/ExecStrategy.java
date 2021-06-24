package friends.queries;

import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ExecStrategy extends AsyncExecutionStrategy {
    @Override
    protected CompletableFuture<ExecutionResult> resolveField(ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
        System.out.println(parameters.getExecutionStepInfo().simplePrint());
        System.out.println("hhh");
        return super.resolveField(executionContext, parameters);
    }
}
