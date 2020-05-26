package ku.ac.th;

public class Posts {
    public String uid, time, date, postimage, description, profileimage, username, usernamepost;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String postimage, String description, String profileimage, String username, String usernamepost) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.description = description;
        this.profileimage = profileimage;
        this.username = username;
        this.usernamepost = usernamepost;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    //function ชื่อของ Username ที่ post


    public String getUsernamepost() {
        return usernamepost;
    }

    public void setUsernamepost(String usernamepost) {
        this.usernamepost = usernamepost;
    }
}

