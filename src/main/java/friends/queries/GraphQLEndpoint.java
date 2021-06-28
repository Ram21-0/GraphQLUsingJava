package friends.queries;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GraphQLEndpoint {

    @GetMapping(value = "/hello")
    @ResponseBody
    public Map<String,Object> hello(@RequestBody String request) {
        System.out.println(request);
        return Map.of("a",3);

    }

    @PostMapping(value = "/sampleRequest")
    public Map<String,Object> function(@RequestBody Map<String,Object> request) {
        return Map.of("hello","world");
    }
}