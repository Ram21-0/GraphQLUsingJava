package friends.queries.data;

import friends.queries.model.Item;
import friends.queries.model.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataRepository {

    private static DataRepository dataRepository;

    private final HashMap<Integer,User> userData;
    private final HashMap<String, Item> itemData;

    private static final Random random = new Random();

    private static final int MAX_VALUE = 100;
    private static int calls = 0;

    private DataRepository(HashMap<Integer,User> userData, HashMap<String,Item> itemData) {
        this.userData = userData;
        this.itemData = itemData;
    }

//    private DataRepository() {
//        dataRepository = new DataRepository(generateRandomData());
//    }

    public static DataRepository getInstance() {
        if(dataRepository == null) dataRepository = new DataRepository(generateRandomUsers(), generateRandomItems());
        return dataRepository;
    }

    private static HashMap<String,Item> generateRandomItems() {
        HashMap<String,Item> data = new HashMap<>();
        for(int i=0;i<=MAX_VALUE*100;i++) {
            String itemId = "item" + i;
            String itemName = "itemName" + i;
            int price = random.nextInt(3001);
            data.put(itemId, new Item(itemId,itemName,price));
        }
        return data;
    }

    private static HashMap<Integer, User> generateRandomUsers() {
        HashMap<Integer,User> data = new HashMap<>();
        data.put(0,new User(0,"Test User","test","testword",Set.of(1,2),List.of("item2","item3")));
        for(int i=1;i<=MAX_VALUE;i++) {
            data.put(i,new User(i,"name" + i, "name" + i + "@gmail.com",i*i*i + "",new HashSet<>(),new ArrayList<>()));
        }

        for(int i=1;i<=MAX_VALUE;i++) {
            int maxFriends = random.nextInt(MAX_VALUE) + 1;
//            if(i == 1) maxFriends = 5000;
            while(maxFriends-- > 0) {
                int friend = random.nextInt(MAX_VALUE) + 1;
                data.get(i).addFriend(friend);
//                data.get(i).getItems().add("item" + maxFriends);
            }

//            String xx = i + "items";
            List<String> sampleItemList = IntStream.range(1,100*i).mapToObj(item -> "item" + item).collect(Collectors.toList());
            data.get(i).itemList().addAll(sampleItemList);
        }

        return data;
    }

//    private HashMap<Integer, User> data = new HashMap<Integer,User>() {{
//        put(1,new User(1,"Ram","ram@gmail.com","12345", new HashSet<>(Arrays.asList(2,3))));
//        put(2,new User(2,"Sita","sita@gmail.com","12535", new HashSet<>(Arrays.asList(1))));
//        put(3,new User(3,"Aamir","aamir@gmail.com","12345", new HashSet<>(Arrays.asList(1,4))));
//        put(4,new User(4,"Tom","tom@gmail.com","12345", new HashSet<>(Arrays.asList(3))));
//    }} ;


    public static int size() {
        return MAX_VALUE;
    }

    public void delay() {
//        int time = 1;
//        try {
//            Thread.sleep(time);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        calls++;
    }

    public static int getCalls() {
        return calls;
    }

    public static void resetCalls() { calls = 0; }

    public List<Integer> getAllUserIds() {
        delay();
        return new ArrayList<>(dataRepository.userData.keySet());
    }

    public List<String> getAllItemIds() {
        delay();
        return new ArrayList<>(dataRepository.itemData.keySet());
    }

    public List<User> getAllUsers() {
        delay();
        return new ArrayList<>(dataRepository.userData.values());
    }

    public List<Item> getAllItems() {
        delay();
        return new ArrayList<>(dataRepository.itemData.values());
    }

    public User getUser(int i) {
        delay();
        return dataRepository.userData.get(i);
    }

    public Item getItem(String id) {
        delay();
        return dataRepository.itemData.get(id);
    }

    public List<User> getManyUsers(List<Integer> ids) {
        delay();
        return ids.parallelStream().map(this::getUser).collect(Collectors.toList());
    }

    public List<Item> getManyItems(List<String> ids) {
        delay();
        return ids.parallelStream().map(this::getItem).collect(Collectors.toList());
    }

    public User updateUser(int id, User user) {
        delay();
        dataRepository.userData.put(id,user);
        return user;
    }

    public Item updateItem(String id, Item item) {
        delay();
        dataRepository.itemData.put(id,item);
        return item;
    }

    public User addUser(User user) {
        delay();
        int id = user.getId();
        dataRepository.userData.put(id,user);
        user.connections().forEach(friend -> dataRepository.userData.get(friend).addFriend(id));
        return user;
    }

    public Item addItem(Item item) {
        delay();
        dataRepository.itemData.put(item.getId(), item);
        return item;
    }
}