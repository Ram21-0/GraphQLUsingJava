package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.User;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Component
@GraphQLApi
@Service
public class FriendsResolver {

    DataRepository repository;
    private static FriendsResolver friendsResolver;

    private FriendsResolver() {
        repository = DataRepository.getInstance();
    }

    public static FriendsResolver getInstance() {
        if(friendsResolver == null) {
            friendsResolver = new FriendsResolver();
        }
        return friendsResolver;
    }


    @GraphQLQuery(name = "friends")
    public CompletableFuture<List<User>> getFriends(@GraphQLContext User user,
                                                    @GraphQLArgument(name = "first",defaultValue = "-1") int first,
                                                    @GraphQLArgument(name = "offset",defaultValue = "0") int offset,
                                                    @GraphQLEnvironment ResolutionEnvironment env) {

        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
        offset = Math.max(offset, 0);
        Stream<Integer> friendIds = user.connections().stream().skip(offset);
        if(first >= 0) {
            friendIds = friendIds.limit(first);
        }
        return loader.loadMany(friendIds.toList());

//        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
//        CompletableFuture<List<User>> result = loader.loadMany(user.connections().stream().toList());
//        return result;
    }

//    // without data loaders
//    public List<User> getFriends(@GraphQLContext User user,
//                                 @GraphQLArgument(name = "first",defaultValue = "-1") int first,
//                                 @GraphQLArgument(name = "offset",defaultValue = "0") int offset,
//                                 @GraphQLEnvironment ResolutionEnvironment env) {
//
//        offset = Math.max(offset, 0);
//        Stream<Integer> friendIds = user.connections().stream().skip(offset);
//        if(first >= 0) {
//            friendIds = friendIds.limit(first);
//        }
//        return friendIds.map(id -> repository.getUser(id)).collect(Collectors.toList());
//    }
}
