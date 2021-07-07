package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.Item;
import friends.queries.model.User;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@GraphQLApi
public class ItemQueryWithLoaders {

    DataRepository repository;
    private static ItemQueryWithLoaders itemQueryWithLoaders;

    private ItemQueryWithLoaders() {
        repository = DataRepository.getInstance();
    }

    public static ItemQueryWithLoaders getInstance() {
        if (itemQueryWithLoaders == null) {
            itemQueryWithLoaders = new ItemQueryWithLoaders();
        }
        return itemQueryWithLoaders;
    }

    @GraphQLQuery(name = "items")
    public CompletableFuture<List<Item>> getItems(@GraphQLContext User user,
                                                  @GraphQLArgument(name = "first",defaultValue = "-1") int first,
                                                  @GraphQLArgument(name = "offset",defaultValue = "0") int offset,
                                                  @GraphQLEnvironment ResolutionEnvironment environment) {

        DataLoader<String,Item> loader = environment.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.ITEM_FETCHER);

        var list = user.itemList();
        int size = list.size();

        offset = Math.max(offset, 0);
        offset = Math.min(offset, size);

        if(first < 0) first = size;
        int end = Math.min(size, first + offset);
        return loader.loadMany(user.itemList().subList(offset,end));

//        DataLoader<String,Item> loader = environment.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.ITEM_FETCHER);
//        return loader.loadMany(user.itemList());
    }

//    public Map<Integer,CompletableFuture<List<Item>>> getManyItems(List<User> users, @GraphQLEnvironment ResolutionEnvironment env) {
//        Map<Integer,CompletableFuture<List<Item>>> map = new HashMap<>();
//        users.forEach(user -> {
//            map.put(user.getId(), getItems(user,env));
//        });
//        return map;
//    }
}
