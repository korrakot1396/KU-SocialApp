package ku.ac.th;

public class FindFriends
{
    public String profileimage, username, faculty;

    public FindFriends()
    {

    }

    public FindFriends(String profileimage, String username, String faculty) {
        this.profileimage = profileimage;
        this.username = username;
        this.faculty = faculty;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
