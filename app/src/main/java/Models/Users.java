package Models;

public class Users {

    private String UsersId, Email, Password, ImageUrl, ImageName, Fname, Lname, ContactNum,
            DOB, Gender, ValidIdUrl, ValidIdName, Company, Category;
    private int Connections;
    private long rating;

    public Users() {
    }

    public Users(String usersId, String email, String password, String imageUrl, String imageName,
                 String fname, String lname, String contactNum, String DOB, String gender,
                 String validIdUrl, String validIdName, String company, String category,
                 int connections, long rating) {

        UsersId = usersId;
        Email = email;
        Password = password;
        ImageUrl = imageUrl;
        ImageName = imageName;
        Fname = fname;
        Lname = lname;
        ContactNum = contactNum;
        this.DOB = DOB;
        Gender = gender;
        ValidIdUrl = validIdUrl;
        ValidIdName = validIdName;
        Company = company;
        Category = category;
        Connections = connections;
        this.rating = rating;
    }

    public String getUsersId() {
        return UsersId;
    }

    public void setUsersId(String usersId) {
        UsersId = usersId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getContactNum() {
        return ContactNum;
    }

    public void setContactNum(String contactNum) {
        ContactNum = contactNum;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getValidIdUrl() {
        return ValidIdUrl;
    }

    public void setValidIdUrl(String validIdUrl) {
        ValidIdUrl = validIdUrl;
    }

    public String getValidIdName() {
        return ValidIdName;
    }

    public void setValidIdName(String validIdName) {
        ValidIdName = validIdName;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public int getConnections() {
        return Connections;
    }

    public void setConnections(int connections) {
        Connections = connections;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
