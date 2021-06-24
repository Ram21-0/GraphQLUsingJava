package friends2.queries.queries;

import friends2.queries.data.DataRepositoryWithoutDataLoaders;
import friends2.queries.model.User;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@GraphQLApi
public class UserQueryWithoutLoaders {

    DataRepositoryWithoutDataLoaders repository;
    static UserQueryWithoutLoaders userQuery;

    private UserQueryWithoutLoaders() {
        repository = DataRepositoryWithoutDataLoaders.getInstance();
    }

    public static UserQueryWithoutLoaders getInstance() {
        if(userQuery == null) userQuery = new UserQueryWithoutLoaders();
        return userQuery;
    }

//    WITHOUT DATA LOADERS : IMPLEMENTATION 1
    @GraphQLQuery(name = "users")
    public List<User> getAll() {
        return repository.getAll();
    }

//        WITHOUT DATA LOADERS : IMPLEMENTATION 1
    @GraphQLQuery(name = "getUserById")
    public User getUser(@GraphQLArgument(name = "id") int id) {
        return repository.get(id);
    }


//    public CompletableFuture<User> getAll(@GraphQLEnvironment ResolutionEnvironment env) {
//        DataLoader<Integer,User> dataLoader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_DATA_LOADER);
//        return dataLoader.loa
//    }




//  USING DATA LOADERS : IMPLEMENTATION 2

//    @GraphQLQuery(name = "users")
//    public CompletableFuture<List<User>> getAll(@GraphQLEnvironment ResolutionEnvironment env) {
//        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
////        Statistics statistics = loader.getStatistics();
////        System.out.println("*******8*888888888***************");
////        System.out.println("batch load count: " + statistics.getBatchLoadCount() +
////                "\n cache hit count: " + statistics.getCacheHitCount() +
////                "\n cache hit ratio: " + statistics.getCacheHitRatio());
//
//
//        return loader.loadMany(repository.getKeys());
//    }
//
//    @GraphQLQuery(name = "getUserById")
//    public CompletableFuture<User> getUser(@GraphQLArgument(name = "id") int id, @GraphQLEnvironment ResolutionEnvironment env) {
//        DataLoader<Integer, User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
//        CompletableFuture<User> result = loader.load(id);
//        return result;
//    }

    @GraphQLMutation(name = "createUser")
    public User addUser(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "name") String name,
                        @GraphQLArgument(name = "username") String username, @GraphQLArgument(name = "password") String password,
                        @GraphQLArgument(name = "friends") Set<Integer> friends) {
        return repository.add(new User(id,name,username,password,friends));
    }

    @GraphQLMutation(name = "updateUser")
    public User update(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "u") User u) {
        return repository.update(id,u);
    }
}

