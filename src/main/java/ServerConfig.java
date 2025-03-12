import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;


public class ServerConfig {
    private Properties properties = new Properties();

    public ServerConfig(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        properties.load(fis);
        fis.close();
    }

    public String getRoot() {
        return properties.getProperty("server.root");
    }

    public int getPort(){
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public String getDefaultPageFile(){
        return properties.getProperty("server.default.page");
    }

    public String getDefaultPageExtension(){
        return properties.getProperty("server.default.page.extension");
    }

    public String getPage404() {
        return properties.getProperty("server.page.404");
    }

    public int getMaxRequests() {
        return Integer.parseInt(properties.getProperty("server.maximum.requests"));
    }
}
