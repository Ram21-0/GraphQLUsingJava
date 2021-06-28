package friends.queries.dataloader;

import friends.queries.data.DataRepository;
import friends.queries.model.Item;
import friends.queries.model.User;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import org.dataloader.*;
import org.dataloader.impl.DefaultCacheMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Configuration
@Component // added
public class DataLoaderConfig implements DataLoaderRegistryFactory {

    public static final String USER_DATA_LOADER = "userDataLoader";
    public static final String FRIENDS_DATA_LOADER = "friendsDataLoader";
    public static final String USER_FETCHER = "userFetcher";
    public static final String ITEM_FETCHER = "itemFetcher";

    static int count = 0;

    @Autowired
    private static final DataRepository repository = DataRepository.getInstance();
    private static CacheMap<Integer,User> cacheMap = new DefaultCacheMap<>();

//    LoadingCache<Integer,User> loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<Integer,User>() {
//                @Override
//                public User load(Integer o) throws Exception {
//                    return this.load(o);
//                }
//            });

    private static final Executor threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final Executor threadPool2 =

            //Executors.newWorkStealingPool();

            Executors.newCachedThreadPool(new ThreadPoolTaskExecutor());

    private static final DataLoaderOptions options = DataLoaderOptions.newOptions()
//                .setCacheMap(cacheMap)
            .setBatchingEnabled(true)
            .setCachingEnabled(true);


    // only this is being used rn
    @Bean(name = USER_FETCHER)
    private static DataLoader<Integer,User> getUserData() {
        BatchLoader<Integer,User> batchLoader =
                ids -> CompletableFuture.supplyAsync(
                        () -> repository.getManyUsers(ids),
                                //ids.stream().map(repository::get).collect(Collectors.toList()),
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
//    @Bean(name = "F")

    @Bean(name = USER_DATA_LOADER)
    public static DataLoader<Integer,List<User>> userDataLoader() {
        BatchLoader<Integer, List<User>> userBatchLoader =
                ids -> CompletableFuture.supplyAsync(() -> {
                    List<User> users = repository.getAllUsers();
                    List<List<User>> friends = users.stream()
                            .map(user -> user.connections().stream()
                                    .map(repository::getUser).collect(Collectors.toList()))
                            .collect(Collectors.toList());

//                    repository.getAll().stream().map(User::getFriends).map(list -> repository.get(list));
//                    final List<List<User>> result = new ArrayList<>(repository.getAll().stream().collect(Collectors.groupingBy(User::getId)).values());
                    return friends;
                });

//        final BatchLoaderContextProvider sample=new BatchLoaderContextProvider();
//        DataLoaderOptions.newOptions().setCacheMap()
//        DataLoader.newDataLoader(commentBatchLoader).clearAll()

        return DataLoader.newDataLoader(userBatchLoader,options);
    }

    @Bean(name = FRIENDS_DATA_LOADER)
    public static DataLoader<Integer,User> friendsDataLoader() {
        BatchLoader<Integer, User> friendsBatchLoader =
                ids -> CompletableFuture.supplyAsync(() -> {
                    User user = repository.getUser(ids.get(0));
                    List<User> friends = user.connections().stream()
                                    .map(repository::getUser).collect(Collectors.toList());
                    return friends;
                });

//        final BatchLoaderContextProvider sample=new BatchLoaderContextProvider();
//        DataLoaderOptions.newOptions().setCacheMap()
//        DataLoader.newDataLoader(commentBatchLoader).clearAll()

        return DataLoader.newDataLoader(friendsBatchLoader);
    }

    @Bean
    public static DataLoaderRegistry registerLoader() {
        final DataLoaderRegistry registry = new DataLoaderRegistry();
//        registry.register(USER_DATA_LOADER, userDataLoader());
//        registry.register(FRIENDS_DATA_LOADER, friendsDataLoader());
        registry.register(USER_FETCHER,getUserData());
        registry.register(ITEM_FETCHER,itemDataLoader());
        return registry;
    }

    @Override
    public DataLoaderRegistry createDataLoaderRegistry() {
        return registerLoader();
    }
}