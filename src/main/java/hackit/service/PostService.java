package hackit.service;

import com.google.gson.Gson;
import hackit.dto.Item;
import hackit.dto.PostData;
import org.jvnet.hk2.annotations.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

public class PostService {

    public List<Item> findLatestPosts() {
        PostData postData = null;
        @Nullable InputStream inputStream = null;
        try {
            URL url = new URL("http://95.163.251.105/api/posts");
            inputStream = url.openStream();
            Reader reader = new InputStreamReader(inputStream, "UTF-8");

            Gson gson = new Gson();
            postData = gson.fromJson(reader, PostData.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
        return postData.getItems();
    }
}
