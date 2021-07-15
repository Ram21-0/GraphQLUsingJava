package friends.queries.data;

import friends.queries.model.Item;
import friends.queries.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class DataRepository {

    private static DataRepository dataRepository;

    private final HashMap<Integer,User> userData;
    private final HashMap<String, Item> itemData;

    private static final Random random = new Random();

    private static final int NO_OF_USERS = 1000;
    private static final int NO_OF_ITEMS = 1000;
    private static final int ITEMS_PER_USER = 30;
    public static final int FRIENDS_PER_USER = 400;

    private static int calls = 0;

    private static final boolean randomData = false;

    private DataRepository(HashMap<Integer,User> userData, HashMap<String,Item> itemData) {
        this.userData = userData;
        this.itemData = itemData;
        System.out.println("generated data");
    }

    public static DataRepository getInstance() {
        if(dataRepository == null) dataRepository = new DataRepository(generateRandomUsers(), generateRandomItems());
        return dataRepository;
    }

    private static HashMap<String,Item> generateRandomItems() {
        HashMap<String,Item> data = new HashMap<>();
        for(int i=0;i<=NO_OF_ITEMS;i++) {
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
        for(int i = 1; i<= NO_OF_USERS; i++) {
            data.put(i,new User(i,"name" + i, "name" + i + "@gmail.com",i*i*i + "",new HashSet<>(),new ArrayList<>()));
        }

        if(!randomData) {

            try {
                Scanner friendScanner = new Scanner(new File("src/main/resources/friendsData.txt"));
                int i = 1;
                while (friendScanner.hasNextLine()) {
                    String friendLine = friendScanner.nextLine();
                    for (String id : friendLine.split(" ")) {
                        data.get(i).addFriend(Integer.parseInt(id));
                    }
                    i++;
                }

                i = 1;
                Scanner itemScanner = new Scanner(new File("src/main/resources/itemsData.txt"));
                while (itemScanner.hasNextLine()) {
                    String itemLine = itemScanner.nextLine();
                    List<String> items = new ArrayList<>(Arrays.asList(itemLine.split(" ")));
                    data.get(i).itemList().addAll(items);
                    i++;
                }
                return data;
            }
            catch (Exception e) {
                e.printStackTrace();
                return data;
            }
        }

        for(int i = 1; i<= NO_OF_USERS; i++) {

            List<Integer> a = new ArrayList<>(NO_OF_ITEMS);
            for(int j=1;j<=NO_OF_ITEMS;j++) {
                a.add(j);
            }
            Collections.shuffle(a);

            List<String> sampleItemList = new ArrayList<>();
            for(int id : a.subList(0,ITEMS_PER_USER)) {
                sampleItemList.add("item" + id);
            }
            data.get(i).itemList().addAll(sampleItemList);

            List<Integer> b = new ArrayList<>(NO_OF_USERS);
            for(int id=0;id<=NO_OF_USERS;id++) {
                b.add(id);
            }

            Collections.shuffle(b);

            for(int id : b.subList(0,FRIENDS_PER_USER)) {
                data.get(i).addFriend(id);
            }
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
        return NO_OF_USERS;
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
//        return ids.parallelStream().map(this::getUser).collect(Collectors.toList());
        return ids.stream().map(dataRepository.userData::get).collect(Collectors.toList()); // depends on data store
    }

    public List<Item> getManyItems(List<String> ids) {
        delay();
//        return ids.stream().map(this::getItem).collect(Collectors.toList());
        return ids.stream().map(dataRepository.itemData::get).collect(Collectors.toList()); // depends on data store
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

    public Item createItem(Item item) {
        delay();
        dataRepository.itemData.put(item.getId(), item);
        return item;
    }

    public User addFriendsOfUser(int id,Set<Integer> friends) {
        delay();
        User u = dataRepository.userData.get(id);
        u.connections().addAll(friends);
        return u;
    }

    public User deleteUser(int id) {
        delay();
        return dataRepository.userData.remove(id);
    }
}