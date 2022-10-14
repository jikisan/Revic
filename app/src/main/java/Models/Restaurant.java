package Models;

public class Restaurant {

    String UsersId, Email, Password, ImageUrl, ImageName, Establishment, Fname, Lname,
            ContactNum, DOE, ValidIdUrl, ValidIdName, Category;

    public Restaurant() {
    }

    public Restaurant(String usersId, String email, String password, String imageUrl, String imageName,
                      String establishment, String fname, String lname, String contactNum, String DOE,
                      String validIdUrl, String validIdName, String category) {
        UsersId = usersId;
        Email = email;
        Password = password;
        ImageUrl = imageUrl;
        ImageName = imageName;
        Establishment = establishment;
        Fname = fname;
        Lname = lname;
        ContactNum = contactNum;
        this.DOE = DOE;
        ValidIdUrl = validIdUrl;
        ValidIdName = validIdName;
        Category = category;
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

    public String getEstablishment() {
        return Establishment;
    }

    public void setEstablishment(String establishment) {
        Establishment = establishment;
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

    public String getDOE() {
        return DOE;
    }

    public void setDOE(String DOE) {
        this.DOE = DOE;
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

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
