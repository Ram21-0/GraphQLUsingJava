package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.User;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.dataloader.stats.Statistics;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

//@Component
@GraphQLApi
@Service
public class FriendsQueryWithLoader {
//    DataRepository repository = DataRepository.getInstance();

    DataRepository repository;
    private static FriendsQueryWithLoader friendsQuery;

    private FriendsQueryWithLoader() {
        repository = DataRepository.getInstance();
    }

    public static FriendsQueryWithLoader getInstance() {
        if(friendsQuery == null) friendsQuery = new FriendsQueryWithLoader();
        return friendsQuery;
    }

//
//    @GraphQLQuery(name = "friends")
//    public List<User> getFriends(@GraphQLContext User user) {
//        return user.getFriends().stream().map(id -> repository.get(id)).collect(Collectors.toList());
//    }

    @GraphQLQuery(name = "friends")
    public CompletableFuture<List<User>> getFriends(@GraphQLContext User user, @GraphQLEnvironment ResolutionEnvironment env) {
        DataLoader<Integer,User> loader = env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.USER_FETCHER);
        CompletableFuture<List<User>> result = loader.loadMany(user.connections().stream().toList());

//        Statistics statistics = loader.getStatistics();
//        System.out.println("*******8*888888888***************");
//        System.out.println("batch load count: " + statistics.getBatchLoadCount() +
//                "\n cache hit count: " + statistics.getCacheHitCount() +
//                "\n cache hit ratio: " + statistics.getCacheHitRatio());
        return result;
    }
}