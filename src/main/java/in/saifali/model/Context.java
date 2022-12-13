package in.saifali.model;

import java.util.Map;

public class Context {
    String language;
    Object context;

    public Context(String language, Object context) {
        this.language = language;
        this.context = context;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}

