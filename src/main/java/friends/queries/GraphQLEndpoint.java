package friends.queries;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GraphQLEndpoint {

//    private GraphQLGenerator graphQLGenerator = GraphQLGenerator.getInstance();

    @GetMapping(value = "/hello")
    @ResponseBody
    public Map<String,Object> hello(@RequestBody String request) {
        System.out.println(request);
        String query = "query Q { users (first:10) { name id ";

        query += "}";
        query += "}";
        return GraphQLGenerator.execute(query);
        //return Map.of("a",3);
    }

    @PostMapping(value = "/endpoint")
    public Map<String,Object> function(@RequestBody Map<String,Object> request) {
        String operationName = (String) request.get("operationName");
        String query = (String) request.get("query");
        Map<String,Object> variables = (Map<String, Object>) request.get("variables");

        return GraphQLGenerator.execute(operationName, query, variables);
    }
}