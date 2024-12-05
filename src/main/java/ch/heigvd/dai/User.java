package ch.heigvd.dai;

public class User {
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public boolean compare(User u1, User u2) {
       return u1.getUsername().equals(u2.getUsername()) && u1.getEmail().equals(u2.getEmail());
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
