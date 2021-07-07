package friends.queries;

import com.google.common.io.Resources;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
//import friends.queries.execution.ExecutionStrategy;
import friends.queries.execution.ExecutionStrategy;
import friends.queries.instrumentation.Instrumentation;
import friends.queries.queries.FriendsQueryWithLoader;
import friends.queries.queries.ItemQueryWithLoaders;
import friends.queries.queries.UserQueryWithLoaders;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.dataloader.DataLoaderRegistry;
//import org.slf4j.ILoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import friends.queries.queries.FriendsQuery;
//import friends.queries.queries.UserQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class Controller {

//    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private static GraphQL graphQL;

    @Autowired
    private static DataLoaderRegistry registry = DataLoaderConfig.registerLoader();

    private static long startTime = System.nanoTime();
    private static long endTime = System.nanoTime();

    @Autowired
    public Controller(UserQueryWithLoaders userQuery, FriendsQueryWithLoader friendQuery, ItemQueryWithLoaders itemQuery) {

//        System.out.println(logger.isDebugEnabled());
        //Schema generated from query classes
//        GraphQLSchema schema = new GraphQLSchemaGenerator()
//                .withBasePackages("friends.queries")
//                .withOperationsFromSingletons(userQuery, friendQuery)
//                .generate();
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("friends.queries")
                .withOperationsFromSingletons(userQuery, friendQuery, itemQuery)
                .generate();
        graphQL = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new ExecutionStrategy())
//                .instrumentation(new MyInstrumentation())
                .build();

        registry = new DataLoaderRegistry();

//        logger.info("Generated GraphQL schema using SPQR");
    }

    public static void main(String[] args) {



        System.out.println("Dataset size - " + DataRepository.size());
//        System.out.println(DataRepository.getInstance().getAll());
//        System.out.println("***************hgsjhfhhkshdldfbjehfwef**************************");
        startTime = System.nanoTime();
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("friends.queries")
                .withOperationsFromSingletons(UserQueryWithLoaders.getInstance(), FriendsQueryWithLoader.getInstance())
//                .resolver
                .generate();
        endTime = System.nanoTime();
        printTime("generate schema");

//        System.out.println(new SchemaPrinter().print(schema));

        startTime = System.nanoTime();
//        graphQL = GraphQL.newGraphQL(schema).build();
        graphQL = GraphQL.newGraphQL(schema)
                .queryExecutionStrategy(new ExecutionStrategy())
                .instrumentation(new Instrumentation())
//                .instrumentation(new ChainedInstrumentation(Arrays.asList(
//                        new MaxQueryComplexityInstrumentation(200),
//                        new MaxQueryDepthInstrumentation(20)
//                )))
                .build();
        endTime = System.nanoTime();
        printTime("build graphql object");

        List<String> queries = Stream.of(1,2,3,4,5)
                .map(i -> getQuery("query" + i + ".txt")).collect(Collectors.toList());

        queries.forEach(query -> {
//            System.out.println(query);
            startTime = System.nanoTime();
//            System.out.println(registry);

            ExecutionInput input = ExecutionInput.newExecutionInput(query).dataLoaderRegistry(registry).build();
            endTime = System.nanoTime();
            printTime("get input");
            startTime = System.nanoTime();
            ExecutionResult result = graphQL.execute(input);
//            CompletableFuture<ExecutionResult> asyncResult = graphQL.executeAsync(input);
            endTime = System.nanoTime();
            printTime("query");

            System.out.println();
            System.out.println();
//            try {
//                System.out.println(asyncResult.get().toSpecification());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            System.out.println(result.toSpecification());
        });

    }

    public static void printTime(String op) {
        System.out.println("Time: " + op + " = " + (endTime - startTime) + " ns " + (endTime-startTime)/1e6 + " ms");
    }

    public static String getQuery(String file) {
        StringBuilder query = new StringBuilder();
        try {
            Scanner scn = new Scanner(Resources.getResource(file).openStream());
            while(scn.hasNextLine()) {
                query.append(scn.nextLine());
            }
            return query.toString();
        }
        catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

//    @PostMapping(value = "/qqq", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public Map<String, Object> indexFromAnnotated(@RequestBody Map<String, String> request, HttpServletRequest raw) {
//        ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput()
//                .query(request.get("query"))
//                .operationName(request.get("operationName"))
//                .context(raw)
//                .dataLoaderRegistry(registry)
//                .build());
//        return executionResult.toSpecification();
//    }
}
