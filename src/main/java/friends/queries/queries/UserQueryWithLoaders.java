package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.User;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@GraphQLApi
public class UserQueryWithLoaders {

    DataRepository repository;
    static UserQueryWithLoaders userQuery;

    private UserQueryWithLoaders() {
        repository = DataRepository.getInstance();
    }

    public static UserQueryWithLoaders getInstance() {
        if(userQuery == null) userQuery = new UserQueryWithLoaders();
        return userQuery;
    }

////    WITHOUT DATA LOADERS : IMPLEMENTATION 1
//    @GraphQLQuery(name = "users")
//    public List<User> getAll() {
//        return repository.getAll();
//    }
//
////        WITHOUT DATA LOADERS : IMPLEMENTATION 1
//    @GraphQLQuery(name = "getUserById")
//    public User getUser(@GraphQLArgument(name = "id") int id) {
//        return repository.get(id);
//    }


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
////        loader.
//        return loader.loadMany(repository.getAllUserIds());
//    }

    @GraphQLQuery(name = "users")
    public CompletableFuture<List<User>> getAll(@GraphQLArgument(name = "first", defaultValue = "-1") int first,
                                                @GraphQLArgument(name = "offset",defaultValue = "-1") int offset,
                                                @GraphQLEnvironment ResolutionEnvironment env) {

        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);

        var list = repository.getAllUserIds();
        int size = list.size();

        offset = Math.max(offset, 0);
        offset = Math.min(offset, size);

        if(first < 0) first = size;
        int end = Math.min(size, first + offset);

        return loader.loadMany(list.subList(offset, end));
//        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
//        return loader.loadMany(repository.getAllUserIds());
    }

    @GraphQLQuery(name = "getUserById")
    public CompletableFuture<User> getUser(@GraphQLArgument(name = "id") int id, @GraphQLEnvironment ResolutionEnvironment env) {
        DataLoader<Integer, User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
        CompletableFuture<User> result = loader.load(id);
        return result;
    }

    @GraphQLMutation(name = "createUser")
    public User addUser(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "name") String name,
                        @GraphQLArgument(name = "username") String username, @GraphQLArgument(name = "password") String password,
                        @GraphQLArgument(name = "friends") Set<Integer> friends,
                        @GraphQLArgument(name = "items") List<String> items) {
        return repository.addUser(new User(id,name,username,password,friends,items));
    }

    @GraphQLMutation(name = "updateUser")
    public User update(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "u") User u) {
        return repository.updateUser(id,u);
    }
}

