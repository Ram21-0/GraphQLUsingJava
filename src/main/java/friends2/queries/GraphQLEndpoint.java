//package friends2.queries;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import friends.queries.data.DataRepository;
//import graphql.ExceptionWhileDataFetching;
//import graphql.GraphQLError;
//import graphql.schema.GraphQLSchema;
//import graphql.schema.idl.SchemaParser;
//import graphql.servlet.GraphQLContext;
//import graphql.servlet.SimpleGraphQLServlet;
//import io.leangen.graphql.GraphQLSchemaGenerator;
//
///**
// * The servlet acting as the GraphQL endpoint
// */
//@WebServlet(urlPatterns = "/graphql")
//public class GraphQLEndpoint extends SimpleGraphQLServlet {
//
//    private static final DataRepository repository;
//
//    static {
//        repository = DataRepository.getInstance();
//    }
//
//    public GraphQLEndpoint() {
//        super(buildSchema());
//    }
//
//    private static GraphQLSchema buildSchema() {
//        return new GraphQLSchemaGenerator()
//                .withBasePackages("friends.queries")
//                .withOperationsFromSingletons(UserQueryWithLoaders.getInstance(), FriendsQueryWithLoader.getInstance())
//                .generate();
//    }
//
//    @Override
//    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
//        User user = request
//                .map(req -> req.getHeader("Authorization"))
//                .filter(id -> !id.isEmpty())
//                .map(id -> id.replace("Bearer ", ""))
//                .map(userRepository::findById)
//                .orElse(null);
//        return new AuthContext(user, request, response);
//    }
//
//    @Override
//    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
//        return errors.stream()
//                .filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
//                .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
//                .collect(Collectors.toList());
//    }
//}