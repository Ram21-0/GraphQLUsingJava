package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.Item;
import friends.queries.model.User;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public CompletableFuture<List<Item>> getItems(@GraphQLContext User user, @GraphQLEnvironment ResolutionEnvironment environment) {
        DataLoader<String,Item> loader = environment.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.ITEM_FETCHER);
        return loader.loadMany(user.itemList());
    }
}
