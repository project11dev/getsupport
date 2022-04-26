package fr.pr11dev.getsupport.shared.storage.local;

import de.leonhard.storage.Json;

import java.io.File;
import java.util.List;

public class JSON {
    private Json json;
    public JSON(String fileName, String filePath) {
        this.json = new Json(fileName, filePath);
    }

    public String getString(String key) {
        return this.json.getString(key);
    }

    public int getInt(String key) {
        return this.json.getInt(key);
    }

    public boolean getBoolean(String key) {
        return this.json.getBoolean(key);
    }

    public List<String> getStringList(String key) {
        return this.json.getStringList(key);
    }

    public List<Integer> getIntList(String key) {
        return this.json.getIntegerList(key);
    }

    public Object getObject(String key) {
        return this.json.get(key);
    }

    public void setString(String key, String value) {
        this.json.getFileData().insert(key, value);
    }

    public void setInt(String key, int value) {
        this.json.getFileData().insert(key, value);
    }

    public void setBoolean(String key, boolean value) {
        this.json.getFileData().insert(key, value);
    }

    public void setStringList(String key, List<String> value) {
        this.json.getFileData().insert(key, value);
    }

    public void setIntList(String key, List<Integer> value) {
        this.json.getFileData().insert(key, value);
    }

    public void setObject(String key, Object value) {
        this.json.getFileData().insert(key, value);
    }

    public void clear() {
        this.json.clear();
    }

    public File getFile() {
        return this.json.getFile();
    }

    public String getName() {
        return this.json.getName();
    }

    public void removeString(String key) {
        this.json.getFileData().remove(key);
    }

    public void removeInt(String key) {
        this.json.getFileData().remove(key);
    }

    public void removeBoolean(String key) {
        this.json.getFileData().remove(key);
    }

    public void removeStringList(String key) {
        this.json.getFileData().remove(key);
    }

    public void removeIntList(String key) {
        this.json.getFileData().remove(key);
    }

    public void removeObject(String key) {
        this.json.getFileData().remove(key);
    }
}
