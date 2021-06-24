package friends.queries.data;

import friends.queries.model.User;

import java.util.*;
import java.util.stream.Collectors;

public class DataRepository {

    private static DataRepository dataRepository;

    private final HashMap<Integer,User> data;

    private static final int MAX_VALUE = 500;
    private static int calls = 0;

    private DataRepository(HashMap<Integer,User> data) {
        this.data = data;
    }

//    private DataRepository() {
//        dataRepository = new DataRepository(generateRandomData());
//    }

    public static DataRepository getInstance() {
        if(dataRepository == null) dataRepository = new DataRepository(generateRandomData());
        return dataRepository;
    }

    private static HashMap<Integer, User> generateRandomData() {
        HashMap<Integer,User> data = new HashMap<>();
        for(int i=1;i<=MAX_VALUE;i++) {
            data.put(i,new User(i,"name" + i, "name" + i + "@gmail.com",i*i*i + "",new HashSet<>(),new ArrayList<>()));
        }

        Random random = new Random();
        for(int i=1;i<=MAX_VALUE;i++) {
            int maxFriends = random.nextInt(MAX_VALUE) + 1;
            if(i == 1) maxFriends = 5000;
            while(maxFriends-- > 0) {
                int friend = random.nextInt(MAX_VALUE) + 1;
                data.get(i).addFriend(friend);
                data.get(i).getItems().add("item" + maxFriends);
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

    public List<Integer> getKeys() {
        delay();
        return new ArrayList<>(dataRepository.data.keySet());
    }

    public List<User> getAll() {
        delay();
        return new ArrayList<>(dataRepository.data.values());
    }

    public User get(int i) {
        delay();
        return dataRepository.data.get(i);
    }

    public List<User> getMany(List<Integer> ids) {
        delay();
        return ids.parallelStream().map(this::get).collect(Collectors.toList());
    }

    public User update(int i,User u) {
        delay();
        dataRepository.data.put(i,u);
        return u;
    }

    public User add(User user) {
        delay();
        int id = user.getId();
//        System.out.println(id + " " + user.connections());
        dataRepository.data.put(id,user);
        user.connections().forEach(friend -> dataRepository.data.get(friend).addFriend(id));
//        System.out.println(data);
        return user;
    }
}