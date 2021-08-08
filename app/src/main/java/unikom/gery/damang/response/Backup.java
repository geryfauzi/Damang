package unikom.gery.damang.response;

import com.google.gson.annotations.SerializedName;

public class Backup {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("path")
    private String path;
    @SerializedName("tanggal")
    private String tanggal;

    public Backup(int code, String message, String path, String tanggal) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.tanggal = tanggal;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTanggal() {
        return tanggal;
    }
}
