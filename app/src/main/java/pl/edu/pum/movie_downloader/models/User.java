package pl.edu.pum.movie_downloader.models;

public class User
{
    private String mUserNickname;
    private String mUserEmail;
    private String mUserPassword;

    public User(String nickname, String email, String password)
    {
        this.mUserNickname = nickname;
        this.mUserEmail = email;
        this.mUserPassword = password;
    }

    public String getUserNickname()
    {
        return mUserNickname;
    }

    public void setUserNickname(String nickname)
    {
        if (nickname.isEmpty())
        {
            throw new IllegalArgumentException("Nick cannot be empty!");
        }
        else
        {
            this.mUserNickname = nickname;
        }
    }

    public String getUserEmail()
    {
        return mUserEmail;
    }

    public void setUserEmail(String email)
    {
        if (email.isEmpty())
        {
            throw new IllegalArgumentException("Email cannot be empty!");
        }
        else
        {
            this.mUserEmail = email;
        }
    }

    public String getUserPassword()
    {
        return mUserPassword;
    }

    public void setUserPassword(String mUserPassword)
    {
        if (mUserPassword.isEmpty())
        {
            throw new IllegalArgumentException("Password cannot be empty!");
        }
        else
        {
            this.mUserPassword = mUserPassword;
        }
    }
}
