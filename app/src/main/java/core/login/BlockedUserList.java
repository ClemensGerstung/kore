package core.login;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import com.typingsolutions.passwordmanager.services.LoginService;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockedUserList implements Iterable<BlockedUser> {
    LoginService loginService;
    private List<BlockedUser> blockedUserList;

    public BlockedUserList(LoginService loginService) {
        this.loginService = loginService;
        blockedUserList = new ArrayList<>();
    }

    @Override
    public Iterator<BlockedUser> iterator() {
        return blockedUserList.iterator();
    }

    public void add(int id) {
        BlockedUser user = null;

        for (BlockedUser blockedUser : blockedUserList) {
            if (blockedUser.id == id) {
                user = blockedUser;
                break;
            }
        }

        if (user != null) {
            user.increaseTries();
        } else {
            user = new BlockedUser(this);
            user.id = id;
            user.increaseTries();
            blockedUserList.add(user);
        }
    }

    public void remove(int id) {
        for (BlockedUser blockedUser : blockedUserList) {
            if (blockedUser.id == id) {
                if (!blockedUser.isBlocked()) {
                    blockedUserList.remove(blockedUser);
                }
                break;
            }
        }
    }

    public BlockedUser getUserById(int id) {
        for (BlockedUser blockedUser : blockedUserList) {
            if (blockedUser.id == id) {
//                Log.d(getClass().getSimpleName(), "getUserById: " + blockedUser.toString());
                return blockedUser;
            }
        }
        return null;
    }

    public String toJson() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        writer.beginArray();
        for (BlockedUser user : blockedUserList) {
            writer.beginObject()
                    .name("id")
                    .value(user.getId())
                    .name("completeTime")
                    .value(user.getCompleteTime())
                    .name("timeRemaining")
                    .value(user.getTimeRemaining())
                    .name("tries")
                    .value(user.tries)
                    .endObject();
        }
        writer.endArray();
        writer.close();

        return stringWriter.toString();
    }

    public void fromJson(String json, boolean match) throws IOException {
        StringReader stringReader = new StringReader(json);
        JsonReader reader = new JsonReader(stringReader);

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            BlockedUser user = new BlockedUser(this);
            while (reader.hasNext()) {
                String name = reader.nextName();

                switch (name) {
                    case "id":
                        user.id = reader.nextInt();
                        break;
                    case "completeTime":
                        user.completeTime = match ? reader.nextInt() : LoginService.FINAL_BLOCK_TIME;
                        break;
                    case "timeRemaining":
                        user.timeRemaining = match ? reader.nextInt() : LoginService.FINAL_BLOCK_TIME;
                        break;
                    case "tries":
                        user.tries = reader.nextInt();
                        break;
                }
            }
            reader.endObject();

            if(!blockedUserList.contains(user)) {
                blockedUserList.add(user);
                if(user.isBlocked())
                    user.startBlocking();
            }
        }
        reader.endArray();
    }
}
