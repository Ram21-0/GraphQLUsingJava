package friends.queries;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GraphQLEndpoint {

    @PostMapping(value = "/endpoint")
    public Map<String,Object> function(@RequestBody Map<String,Object> request) {
        String operationName = (String) request.get("operationName");
        String query = (String) request.get("query");
        Map<String,Object> variables = (Map<String, Object>) request.get("variables");

        return GraphQLGenerator.execute(operationName, query, variables);
    }
}