package friends.queries;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.execution.ExecutionStrategy;
import friends.queries.instrumentation.Instrumentation;
import friends.queries.queries.FriendsResolver;
import friends.queries.queries.ItemResolver;
import friends.queries.queries.UserResolver;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphQLGenerator {

    private static final GraphQL graphQL;
    private static final DataLoaderRegistry registry;
    private static final Cache<String, PreparsedDocumentEntry> queryCache;


    static {

        queryCache = Caffeine.newBuilder().maximumSize(10000).build();

        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("friends.queries")
                .withOperationsFromSingletons(
                        UserResolver.getInstance(),
                        FriendsResolver.getInstance(),
                        ItemResolver.getInstance()
                 )
                .generate();

        PreparsedDocumentProvider preparsedDocumentProvider =
                (executionInput, function) -> queryCache.get(executionInput.getQuery(), key -> function.apply(executionInput));

        graphQL = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new ExecutionStrategy())
                .preparsedDocumentProvider(preparsedDocumentProvider)
//                .queryExecutionStrategy(new BatchedExecutionStrategy())
                .instrumentation(new Instrumentation())
                .build();

        registry = DataLoaderConfig.registerLoader();

        System.out.println("graphql ready");
    }

    public static void init() {

    }

    public static Map<String,Object> execute(String query) {
        ExecutionInput input = ExecutionInput.newExecutionInput(query).dataLoaderRegistry(registry).build();
        ExecutionResult result = graphQL.execute(input);
        return result.toSpecification();
    }

    public static Map<String,Object> execute(String operationName, String query, Map<String,Object> variables) {

        ExecutionInput input = ExecutionInput.newExecutionInput(query)
                                            .operationName(operationName)
                                            .variables(variables)
                                            .dataLoaderRegistry(registry)
                                            .build();

        ExecutionResult result = graphQL.execute(input);
//        System.out.println(queryCache.asMap());
//        System.out.println(queryCache.stats());
        return result.toSpecification();
    }
}
