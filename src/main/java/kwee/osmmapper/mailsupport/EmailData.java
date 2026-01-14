package kwee.osmmapper.mailsupport;

import java.io.File;
import java.util.List;

public class EmailData {
    private String name;
    private String email;
    private List<File> files;
    
    public EmailData(String name, String email, List<File> files) {
        this.name = name;
        this.email = email;
        this.files = files;
    }
    
    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<File> getFiles() { return files; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setFiles(List<File> files) { this.files = files; }
}
