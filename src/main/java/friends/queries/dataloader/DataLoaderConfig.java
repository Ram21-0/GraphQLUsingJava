package friends.queries.dataloader;

import friends.queries.data.DataRepository;
import friends.queries.model.Item;
import friends.queries.model.User;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import org.dataloader.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;


@Configuration
@Component // added
public class DataLoaderConfig implements DataLoaderRegistryFactory {

    public static final String USER_FETCHER = "userFetcher";
    public static final String ITEM_FETCHER = "itemFetcher";

    static int count = 0;

    @Autowired
    private static final DataRepository repository = DataRepository.getInstance();

    private static final Executor threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    private static final DataLoaderOptions options = DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setCachingEnabled(true);


    @Bean(name = USER_FETCHER)
    private static DataLoader<Integer,User> userDataLoader() {
        BatchLoader<Integer,User> batchLoader =
                ids -> CompletableFuture.supplyAsync(
                        () -> repository.getManyUsers(ids),
                        threadPool
                );
        return DataLoader.newDataLoader(batchLoader,options);
    }

    @Bean(name = ITEM_FETCHER)
    public static DataLoader<String, Item> itemDataLoader() {
        BatchLoader<String,Item> loader =
                ids -> CompletableFuture.supplyAsync(
                    () -> repository.getManyItems(ids),
                    threadPool
                );
        return DataLoader.newDataLoader(loader,options);
    }

    @Bean
    public static DataLoaderRegistry registerLoader() {
        final DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register(USER_FETCHER, userDataLoader());
        registry.register(ITEM_FETCHER, itemDataLoader());
        return registry;
    }

    @Override
    public DataLoaderRegistry createDataLoaderRegistry() {
        return registerLoader();
    }
}