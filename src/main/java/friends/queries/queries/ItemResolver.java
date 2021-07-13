package friends.queries.queries;

import friends.queries.data.DataRepository;
import friends.queries.dataloader.DataLoaderConfig;
import friends.queries.model.Item;
import friends.queries.model.User;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@GraphQLApi
public class ItemResolver {

    DataRepository repository;
    private static ItemResolver itemResolver;

    private ItemResolver() {
        repository = DataRepository.getInstance();
    }

    public static ItemResolver getInstance() {
        if (itemResolver == null) {
            itemResolver = new ItemResolver();
        }
        return itemResolver;
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
        return loader.loadMany(list.subList(offset,end));

//        DataLoader<String,Item> loader = environment.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.ITEM_FETCHER);
//        return loader.loadMany(user.itemList());
    }

    @GraphQLMutation(name = "createItem")
    public Item createItem(@GraphQLArgument(name = "item") Item item,
                           @GraphQLEnvironment ResolutionEnvironment env) {

        env.dataFetchingEnvironment.getDataLoader(DataLoaderConfig.ITEM_FETCHER).clear(item.getId()).load(item.getId());
        return repository.createItem(item);
    }
}
