package friends.queries;

import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.execution.ExecutionStrategy;
import friends.queries.instrumentation.Instrumentation;
import friends.queries.queries.FriendsQueryWithLoader;
import friends.queries.queries.ItemQueryWithLoaders;
import friends.queries.queries.UserQueryWithLoaders;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Configuration
public class GraphQLGenerator {

    private static final GraphQL graphQL;
    private static final DataLoaderRegistry registry;

    static {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("friends.queries")
                .withOperationsFromSingletons(
                        UserQueryWithLoaders.getInstance(),
                        FriendsQueryWithLoader.getInstance(),
                        ItemQueryWithLoaders.getInstance()
                 )
                .generate();

        graphQL = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new ExecutionStrategy())
                .instrumentation(new Instrumentation())
                .build();

//        GraphQLDirective

        registry = DataLoaderConfig.registerLoader();
    }

//    public static GraphQLGenerator getInstance() {
//        if (generator == null) {
//            generator = new GraphQLGenerator();
//        }
//        return generator;
//    }

    public static void init() {

    }

    public static Map<String,Object> execute(String query) {
        ExecutionInput input = ExecutionInput.newExecutionInput(query).dataLoaderRegistry(registry).build();
        ExecutionResult result = graphQL.execute(input);
        return result.toSpecification();
//        CompletableFuture<ExecutionResult> asyncResult = graphQL.executeAsync(input);
//        try {
//            return asyncResult.get().toSpecification();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            return e.
//            e.printStackTrace();
//        }
//        return
    }

    public static Map<String,Object> execute(String operationName, String query, Map<String,Object> variables) {
        ExecutionInput input = ExecutionInput.newExecutionInput(query)
                .operationName(operationName)
                .variables(variables)
                .dataLoaderRegistry(registry)
                .build();
        ExecutionResult result = graphQL.execute(input);
        return result.toSpecification();
    }
}