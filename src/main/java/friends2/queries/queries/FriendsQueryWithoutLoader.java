package friends2.queries.queries;

import friends2.queries.data.DataRepositoryWithoutDataLoaders;
import friends2.queries.model.User;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//@Component
@GraphQLApi
@Service
public class FriendsQueryWithoutLoader {
//    DataRepository repository = DataRepository.getInstance();

    DataRepositoryWithoutDataLoaders repository;
    private static FriendsQueryWithoutLoader friendsQuery;

    private FriendsQueryWithoutLoader() {
        repository = DataRepositoryWithoutDataLoaders.getInstance();
    }

    public static FriendsQueryWithoutLoader getInstance() {
        if(friendsQuery == null) friendsQuery = new FriendsQueryWithoutLoader();
        return friendsQuery;
    }


    @GraphQLQuery(name = "friends")
    public List<User> getFriends(@GraphQLContext User user) {
        return user.getFriends().stream().map(id -> repository.get(id)).collect(Collectors.toList());
    }

//    @GraphQLQuery(name = "friends")
//    public CompletableFuture<List<User>> getFriends(@GraphQLContext User user, @GraphQLEnvironment ResolutionEnvironment env) {
//        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
//        CompletableFuture<List<User>> result = loader.loadMany(user.getFriends().stream().toList());
//
////        Statistics statistics = loader.getStatistics();
////        System.out.println("*******8*888888888***************");
////        System.out.println("batch load count: " + statistics.getBatchLoadCount() +
////                "\n cache hit count: " + statistics.getCacheHitCount() +
////                "\n cache hit ratio: " + statistics.getCacheHitRatio());
//        return result;
//    }
}