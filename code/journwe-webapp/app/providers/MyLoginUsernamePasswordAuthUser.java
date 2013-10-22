package providers;

import com.feth.play.module.pa.providers.password.DefaultUsernamePasswordAuthUser;

public class MyLoginUsernamePasswordAuthUser extends
        DefaultUsernamePasswordAuthUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The session timeout in seconds
	 * Defaults to two weeks
	 */
	final static long SESSION_TIMEOUT = 24 * 14 * 3600;
	private long expiration;
    private String userId;

    @Override
    public String getId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
	 * For logging the user in automatically
	 * 
	 * @param email
	 */
	public MyLoginUsernamePasswordAuthUser(final String email, final String userId) {
		this(null, email, userId);
	}

	public MyLoginUsernamePasswordAuthUser(final String clearPassword,
			final String email, final String userId) {
		super(clearPassword, email);
        this.userId = userId;

		expiration = System.currentTimeMillis() + 1000 * SESSION_TIMEOUT;
	}

	@Override
	public long expires() {
		return expiration;
	}

}
