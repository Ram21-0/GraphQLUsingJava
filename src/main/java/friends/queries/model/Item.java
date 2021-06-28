package friends.queries.model;

import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @GraphQLId @GraphQLQuery(name = "id") String id;
    @GraphQLQuery(name = "name") String name;
    @GraphQLQuery(name = "price") int price;
}
