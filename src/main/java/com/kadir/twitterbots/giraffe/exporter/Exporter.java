package com.kadir.twitterbots.giraffe.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kadir.twitterbots.authentication.BotAuthenticator;
import com.kadir.twitterbots.giraffe.util.GiraffeConstants;
import com.kadir.twitterbots.ratelimithandler.handler.RateLimitHandler;
import com.kadir.twitterbots.ratelimithandler.process.ApiProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akadir
 * Date: 15/01/2019
 * Time: 21:41
 */
public class Exporter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Twitter twitter;

    public void authenticate() {
        twitter = BotAuthenticator.authenticate(GiraffeConstants.PROPERTIES_FILE_NAME, GiraffeConstants.API_KEYS_PREFIX);
    }

    public void run() throws IOException, TwitterException {
        logger.info("export starting");
        exportLists();
    }

    private void exportLists() throws TwitterException, IOException {
        ResponseList<UserList> userLists = twitter.getUserLists(twitter.getId());

        if (!userLists.isEmpty()) {
            JsonArray listsToBeExported = new JsonArray();

            for (UserList userList : userLists) {
                List<User> listMembers = getListMembers(userList);
                JsonObject listJson = convertListToJsonObject(userList.getName(), userList.getDescription(), listMembers);
                listsToBeExported.add(listJson);
            }

            Path file = Paths.get(GiraffeConstants.EXPORT_FILE_NAME);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(file, gson.toJson(listsToBeExported).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            int totalMember = userLists.stream().mapToInt(UserList::getMemberCount).sum();

            logger.info("{} lists with approximately {} members have been successfully exported to file {}", userLists.size(), totalMember, GiraffeConstants.EXPORT_FILE_NAME);
        } else {
            logger.info("there is no list to export.");
        }
    }

    private List<User> getListMembers(UserList userList) throws TwitterException {
        PagableResponseList<User> members = null;
        List<User> listMembers = new ArrayList<>();

        long cursor = -1;
        do {
            if (members != null) {
                cursor = members.getNextCursor();
            }

            members = twitter.getUserListMembers(userList.getId(), 100, cursor);
            RateLimitHandler.handle(twitter.getId(), members.getRateLimitStatus(), ApiProcessType.GET_USER_LIST_MEMBERS);
            listMembers.addAll(members);
        } while (members.hasNext());

        return listMembers;
    }

    private JsonObject convertListToJsonObject(String listName, String listDescription, List<User> listMembers) {
        JsonObject listJson = new JsonObject();
        listJson.addProperty("name", listName);
        listJson.addProperty("description", listDescription);
        JsonArray membersJsonArray = new JsonArray();

        listMembers.forEach(user -> {
            JsonObject userJson = new JsonObject();
            userJson.addProperty("id", user.getId());
            userJson.addProperty("screenName", user.getScreenName());
            membersJsonArray.add(userJson);
        });

        listJson.add("members", membersJsonArray);

        logger.info("{} list with {} members converted to json", listName, listMembers.size());
        return listJson;
    }
}
