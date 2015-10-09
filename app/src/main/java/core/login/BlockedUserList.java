package core.login;

import com.typingsolutions.passwordmanager.services.LoginService;

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
                return blockedUser;
            }
        }
        return null;
    }

}
