package ku.ac.th;

public class Friends
{
    public String date, faculty;

    public Friends()
    {

    }

    public Friends(String date, String faculty)
    {
        this.date = date;
        this.faculty = faculty;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
