package friends.queries.dataloader;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import friends.queries.data.DataRepository;
import friends.queries.model.User;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import org.dataloader.*;
import org.dataloader.impl.DefaultCacheMap;
import org.dataloader.stats.Statistics;
import org.dataloader.stats.StatisticsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Configuration
@Component // added
public class DataLoaderConfig implements DataLoaderRegistryFactory {

    public static final String USER_DATA_LOADER = "userDataLoader";
    public static final String FRIENDS_DATA_LOADER = "friendsDataLoader";
    public static final String USER_FETCHER = "userFetcher";

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

    private static Executor threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static Executor threadPool2 =

            //Executors.newWorkStealingPool();

            Executors.newCachedThreadPool(new ThreadPoolTaskExecutor());


    // only this is being used rn
    @Bean(name = USER_FETCHER)
    private static DataLoader<Integer,User> getUserData() {
        BatchLoader<Integer,User> batchLoader =
                ids -> CompletableFuture.supplyAsync(
                        () -> repository.getMany(ids),
                                //ids.stream().map(repository::get).collect(Collectors.toList()),
                        threadPool
                );

//        System.out.println(count++);
//        System.out.println("executing");

        DataLoaderOptions options = DataLoaderOptions.newOptions()
//                .setCacheMap((CacheMap) cacheMap)
                .setBatchingEnabled(true)
                .setCachingEnabled(true);

        return DataLoader.newDataLoader(batchLoader,options);
    }

//    @Bean(name = "F")

    @Bean(name = USER_DATA_LOADER)
    public static DataLoader<Integer,List<User>> userDataLoader() {
        BatchLoader<Integer, List<User>> userBatchLoader =
                ids -> CompletableFuture.supplyAsync(() -> {
                    List<User> users = repository.getAll();
                    List<List<User>> friends = users.stream()
                            .map(user -> user.connections().stream()
                                    .map(repository::get).collect(Collectors.toList()))
                            .collect(Collectors.toList());

//                    repository.getAll().stream().map(User::getFriends).map(list -> repository.get(list));
//                    final List<List<User>> result = new ArrayList<>(repository.getAll().stream().collect(Collectors.groupingBy(User::getId)).values());
                    return friends;
                });

//        final BatchLoaderContextProvider sample=new BatchLoaderContextProvider();
//        DataLoaderOptions.newOptions().setCacheMap()
//        DataLoader.newDataLoader(commentBatchLoader).clearAll()

        DataLoaderOptions options = DataLoaderOptions.newOptions()
//                .setCacheMap(cacheMap)
                .setBatchingEnabled(true)
                .setCachingEnabled(true);

        return DataLoader.newDataLoader(userBatchLoader,options);
    }

    @Bean(name = FRIENDS_DATA_LOADER)
    public static DataLoader<Integer,User> friendsDataLoader() {
        BatchLoader<Integer, User> friendsBatchLoader =
                ids -> CompletableFuture.supplyAsync(() -> {
                    User user = repository.get(ids.get(0));
                    List<User> friends = user.connections().stream()
                                    .map(repository::get).collect(Collectors.toList());
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
        return registry;
    }

    @Override
    public DataLoaderRegistry createDataLoaderRegistry() {
        return registerLoader();
    }
}