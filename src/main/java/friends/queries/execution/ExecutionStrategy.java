package friends.queries.execution;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.PublicApi;
import graphql.execution.*;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationFieldCompleteParameters;
import graphql.util.FpKit;
import org.springframework.boot.SpringBootConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// TODO: 27/06/21 ADD THIS PROPERLY!!!
@PublicApi
@SpringBootConfiguration
public class ExecutionStrategy extends AsyncExecutionStrategy {

    @Override
    protected FieldValueInfo completeValueForList(ExecutionContext executionContext, ExecutionStrategyParameters parameters, Iterable<Object> iterableValues) {
//        System.out.println("hello");
        OptionalInt size = FpKit.toSize(iterableValues);
        ExecutionStepInfo executionStepInfo = parameters.getExecutionStepInfo();
        InstrumentationFieldCompleteParameters instrumentationParams = new InstrumentationFieldCompleteParameters(executionContext, parameters, () -> executionStepInfo, iterableValues);
        Instrumentation instrumentation = executionContext.getInstrumentation();
        InstrumentationContext<ExecutionResult> completeListCtx = instrumentation.beginFieldListComplete(instrumentationParams);

        List<Object> objectList = new ArrayList<>((Collection<?>) iterableValues);
        Stream<Pair> stream = IntStream.range(0, objectList.size()).parallel().mapToObj(i -> new Pair(i,objectList.get(i)));

        List<FieldValueInfo> fieldValueInfos = stream.parallel().map(item -> {
            ResultPath indexedPath = parameters.getPath().segment(item.index);
            ExecutionStepInfo stepInfoForListElement = this.executionStepInfoFactory.newExecutionStepInfoForListElement(executionStepInfo, item.index);
            NonNullableFieldValidator nonNullableFieldValidator = new NonNullableFieldValidator(executionContext, stepInfoForListElement);
            FetchedValue value = this.unboxPossibleDataFetcherResult(executionContext, parameters, item.object);
            ExecutionStrategyParameters newParameters = parameters.transform(builder -> builder.executionStepInfo(stepInfoForListElement).nonNullFieldValidator(nonNullableFieldValidator).listSize(size.orElse(-1)).localContext(value.getLocalContext()).currentListIndex(item.index).path(indexedPath).source(value.getFetchedValue()));
            return this.completeValue(executionContext, newParameters);
        }).collect(Collectors.toList());

        CompletableFuture<List<ExecutionResult>> resultsFuture = Async.each(fieldValueInfos, (itemx, i) -> itemx.getFieldValue());
        CompletableFuture<ExecutionResult> overallResult = new CompletableFuture();
        completeListCtx.onDispatched(overallResult);
        resultsFuture.whenComplete((results, exception) -> {
            if (exception != null) {
                ExecutionResult executionResult = this.handleNonNullException(executionContext, overallResult, exception);
                completeListCtx.onCompleted(executionResult, exception);
            } else {
                List<Object> completedResults = results.parallelStream().map(ExecutionResult::getData).collect(Collectors.toList());

                ExecutionResultImpl executionResultx = new ExecutionResultImpl(completedResults, null);
                overallResult.complete(executionResultx);
            }
        });
        overallResult.whenComplete(completeListCtx::onCompleted);
        return FieldValueInfo.newFieldValueInfo(FieldValueInfo.CompleteValueType.LIST).fieldValue(overallResult).fieldValueInfos(fieldValueInfos).build();
    }

    private static class Pair {
        int index;
        Object object;
        public Pair(int index, Object object) {
            this.index = index;
            this.object = object;
        }
    }
}
