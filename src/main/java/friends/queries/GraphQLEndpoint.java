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
        return Map.of("a",3);
    }

    @PostMapping(value = "/endpoint")
    public Map<String,Object> function(@RequestBody Map<String,Object> request) {
        String operationName = (String) request.get("operationName");
        String query = (String) request.get("query");
        Map<String,Object> variables = (Map<String, Object>) request.get("variables");
//        System.out.println(request.keySet());
//        System.out.println(variables);
        return GraphQLGenerator.execute(operationName, query, variables);
//                return Map.of("Hello","world");
//        System.out.println("query");
//        return Map.of("hello","world");
    }
}