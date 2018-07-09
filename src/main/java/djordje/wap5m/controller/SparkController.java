package djordje.wap5m.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import djordje.wap5m.model.EntryDAO;
import spark.Spark;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.servlet.SparkApplication;
import spark.utils.IOUtils;

/**
 *
 * @author djordje gavrilovic
 */
public class SparkController implements SparkApplication{
    
    private static EntryDAO entryDao = new EntryDAO();
    private static ObjectMapper om = new ObjectMapper();

    @Override
    public void init() {
        
        // landing page in src/main/resources/public
        Spark.staticFileLocation("/public");
        get("/", (request, response) -> IOUtils.toString(Spark.class.getResourceAsStream("/index.html")));
        
        // GET all entries
        get("/entry", (request, response) -> {
            if (entryDao.getAllEntries().isEmpty()) {
                return om.writeValueAsString("user not found");
            } else {
                return om.writeValueAsString(entryDao.getAllEntries());
            }
        });
        
        // POST - Add new entry
        post("/entry/add", (request, response) -> { 
            String title = request.queryParams("title");
            String text = request.queryParams("text");
            if(title != null) {
                entryDao.addNewEntry(title, text);
                response.status(200);
                return om.writeValueAsString("New entry added.");
            } else {
                response.status(404);
                return om.writeValueAsString("Title not provided");
            }            
        });
        
        // DELETE entry
        delete("/entry/:id", (request, response) -> {
            String id = request.params(":id");
            if (id != null) {
                entryDao.removeEntry(id);
                return om.writeValueAsString("Entry with id " + id + " is deleted!");
            } else {
                response.status(404);
                return om.writeValueAsString("Entry not found");
            }
        });
        
    }
    
}
