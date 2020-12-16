package app.configuration;

import com.google.gson.annotations.SerializedName;

public class Properties {

    // Holds a method reference from Configuration.class to notify it when properties change.
    private final transient Runnable onPropertyUpdated;

    @SerializedName("acc")
    private String acc;

    @SerializedName("ref")
    private String ref;

    @SerializedName("exp")
    private String exp;

    @SerializedName("key")
    private String key;

    @SerializedName("url")
    private String url;

    @SerializedName("lastSession")
    private String lastSession;

    public String getAcc() { return acc; }
    public void setAcc(String value) {
        acc = value;
        onPropertyUpdated.run();
    }

    public String getRef() { return ref; }
    public void setRef(String value) {
        ref = value;
        onPropertyUpdated.run();
    }

    public String getExp() { return exp; }
    public void setExp(String value) {
        exp = value;
        onPropertyUpdated.run();
    }

    public String getKey() { return key; }
    public void setKey(String value) {
        key = value;
        onPropertyUpdated.run();
    }

    public String getUrl() { return url; }
    public void setUrl(String value) {
        url = value;
        onPropertyUpdated.run();
    }

    public String getLastSession() { return lastSession; }
    public void setLastSession(String lastSession) {
        this.lastSession = lastSession;
    }

    Properties(){
        this.onPropertyUpdated = Configuration::updateProperties;
    }

    Properties(
            String acc,
            String ref,
            String exp,
            String key,
            String url,
            String lastSession
    ) {
        this.onPropertyUpdated = Configuration::updateProperties;
        this.acc = acc;
        this.ref = ref;
        this.exp = exp;
        this.key = key;
        this.url = url;
        this.lastSession = lastSession;
    }
}