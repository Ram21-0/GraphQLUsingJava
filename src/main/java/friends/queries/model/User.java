package friends.queries.model;

import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class User {
    @GraphQLId int id;
    String name;
    String username;
    String password;
    Set<Integer> friends;
    List<String> items;

    public User(int id, String name, String username, String password, Set<Integer> friends) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.friends = friends;
    }

    //    public static User valueOf(Document doc) {
//        return new User(
//                doc.get("_id").toString(),
//                doc.getString("name"),
//                doc.getString("email"),
//                doc.getString("password")
//        );
//    }

//    public Document toMongoDocument() {
//        Document doc = new Document();
//        doc.append("name",name)
//                .append("email",username)
//                .append("password",password);
//        return doc;
//    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User) o;
//        return Objects.equals(name, user.name) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(friends, user.friends);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(name, username, password, friends);
//    }

//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
    public int getId() {
        return id;
    }

    @GraphQLQuery(name = "name")
    public String getName() {
        return name;
    }

    @GraphQLQuery(name = "username")
    public String getUsername() {
        return username;
    }

    @GraphQLQuery(name = "password")
    public String getPassword() {
        return password;
    }

    public Set<Integer> connections() {
        return friends;
    }

    public void addFriend(int fid) {
        friends.add(fid);
//        System.out.println(friends);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", friends=" + friends +
                ", items=" + items +
                '}';
    }

    //    @GraphQLQuery(name = "items")
    public List<String> itemList() {
        return items;
    }
}