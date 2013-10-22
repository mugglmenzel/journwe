package providers;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.EmailIdentity;
import models.dao.UserDAO;
import models.user.User;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.NameIdentity;

public class MyUsernamePasswordAuthUser extends UsernamePasswordAuthUser implements NameIdentity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;

    public MyUsernamePasswordAuthUser(final MySignup signup) {
        super(signup.password,signup.email);
        this.name = signup.name;
//        User u = UserDAO.findByEmail(signup.email);
//        if(u!=null)
//            userId = u.getId();
//        else
//            Logger.error("In constructor of MyUsernamePasswordAuthUser the user u is null.");
    }

    @Override
    public String getId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }
}