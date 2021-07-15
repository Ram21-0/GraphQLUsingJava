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
public class UserResolver {

    DataRepository repository;
    static UserResolver userResolver;

    private UserResolver() {
        repository = DataRepository.getInstance();
    }

    public static UserResolver getInstance() {
        if(userResolver == null) userResolver = new UserResolver();
        return userResolver;
    }

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

//        return loader.loadMany(repository.getAllUserIds());
    }

//    // without data loaders
//    public List<User> getAll(@GraphQLArgument(name = "first", defaultValue = "-1") int first,
//                             @GraphQLArgument(name = "offset",defaultValue = "-1") int offset,
//                             @GraphQLEnvironment ResolutionEnvironment env) {
//
//        var list = repository.getAllUserIds();
//        int size = list.size();
//
//        offset = Math.max(offset, 0);
//        offset = Math.min(offset, size);
//
//        if(first < 0) first = size;
//        int end = Math.min(size, first + offset);
//        return repository.getAllUsers().subList(offset,end);
//    }

    @GraphQLQuery(name = "getUserById")
    public CompletableFuture<User> getUser(@GraphQLArgument(name = "id") int id, @GraphQLEnvironment ResolutionEnvironment env) {
        DataLoader<Integer, User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
        return loader.load(id);
    }

//    // without data loaders
//    public User getUserById(@GraphQLArgument(name = "id") int id, @GraphQLEnvironment ResolutionEnvironment env) {
//        return repository.getUser(id);
//    }

    @GraphQLMutation(name = "createUser")
    public User addUser(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "name") String name,
                        @GraphQLArgument(name = "username") String username, @GraphQLArgument(name = "password") String password,
                        @GraphQLArgument(name = "friends") Set<Integer> friends,
                        @GraphQLArgument(name = "items") List<String> items,
                        @GraphQLEnvironment ResolutionEnvironment env) {

        env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER).clear(id);
        return repository.addUser(new User(id,name,username,password,friends,items));
    }

    @GraphQLMutation(name = "updateUser")
    public User update(@GraphQLArgument(name = "id") int id, @GraphQLArgument(name = "u") User u,
                       @GraphQLEnvironment ResolutionEnvironment env) {

        env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER).clear(id);
        return repository.updateUser(id,u);
    }

    @GraphQLMutation(name = "addFriends")
    public User updateUser(@GraphQLArgument(name = "id") int id,
                           @GraphQLArgument(name = "friends") Set<Integer> friends,
                           @GraphQLEnvironment ResolutionEnvironment env) {

        env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER).clear(id);
        return repository.addFriendsOfUser(id,friends);
    }

    @GraphQLMutation(name = "deleteUser")
    public User deleteUser(@GraphQLArgument(name = "id") int id, @GraphQLEnvironment ResolutionEnvironment env) {
        env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER).clear(id);
        return repository.deleteUser(id);
    }
}

