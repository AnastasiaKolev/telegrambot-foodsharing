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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.net.URLEncoder;

public class PostService {

    @Nullable
    public List<Item> findLatestPosts() {
        PostData postData = null;
        InputStream inputStream = null;
        try {
            URL url = new URL("http://95.163.251.105/api/posts");
            inputStream = url.openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

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

    @Nullable
    public List<Item> findPostsByGeo(String postCount, String location, String distance) {
        PostData postData = null;
        InputStream inputStream = null;
        try {
            String geo = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
            URI uri = new URI("http://95.163.251.105/api/posts"
                    + "?count=" + postCount
                    + "&geo=" + geo
                    + "&distance=" + distance);
            URL url = uri.toURL();

            inputStream = url.openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            postData = gson.fromJson(reader, PostData.class);
        } catch (IOException | URISyntaxException e) {
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

    @Nullable
    public List<Item> findNearestPosts(String location, String distance) {
        PostData postData = null;
        InputStream inputStream = null;
        try {
            String geo = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
            URI uri = new URI("http://95.163.251.105/api/posts"
                    + "?count=10&geo=" + geo
                    + "&distance=" + distance);
            URL url = uri.toURL();

            inputStream = url.openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            postData = gson.fromJson(reader, PostData.class);
        } catch (IOException | URISyntaxException e) {
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

    @Nullable
    public List<Item> findNewPosts() {
        PostData postData = null;
        InputStream inputStream = null;
        try {
            URI uri = new URI("http://95.163.251.105/api/posts?onlyNew=true");
            URL url = uri.toURL();

            inputStream = url.openStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            postData = gson.fromJson(reader, PostData.class);
        } catch (IOException | URISyntaxException e) {
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
