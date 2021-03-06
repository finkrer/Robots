package gui;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Config {
    private final ResourceBundle localization;
    private final String path;
    private final Properties properties;

    public Config(String configPath, String localizationPath) {
        path = configPath;
        properties = loadProperties(configPath);
        localization = loadLocalization(localizationPath);
    }

    public boolean windowConfigsArePresent() {
        return hasPrefix("gui");
    }

    private boolean hasPrefix(String prefix) {
        var keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            var key = (String) keys.nextElement();
            if (key.startsWith(prefix))
                return true;
        }
        return false;
    }

    private Properties loadProperties(String path) {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream(path);
            property.load(fis);
        } catch (IOException e) {
            System.err.println("Error! Config file not found");
            System.exit(5);
        }

        return property;
    }

    public void saveWindowStates(JInternalFrame[] frames) {
        for (var frame: frames)
            saveWindowState(frame);
        save();
    }

    private void saveWindowState(JInternalFrame window) {
        var name = window.getClass().getName();
        if (!window.isClosed()) {
            setProperty(name, "Minimized", String.valueOf(window.isIcon()));
            setProperty(name, "PositionX", String.valueOf(window.getX()));
            setProperty(name, "PositionY", String.valueOf(window.getY()));
            setProperty(name, "Width", String.valueOf(window.getWidth()));
            setProperty(name, "Height", String.valueOf(window.getHeight()));
        }
    }

    public void setLocale(String lang, String country) {
        properties.setProperty("lang", lang);
        properties.setProperty("country", country);
        save();
    }

    public String getLocalization(String key) {
        String value;
        try{
            value = localization.getString(key);
        }
        catch (MissingResourceException e)
        {
            value = "error text";
        }
        return value;
    }

    private void save()
    {
        try {
            properties.store(new FileOutputStream(path), null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadWindowStates(JInternalFrame[] frames)
    {
        for (var frame: frames) {
            var name = frame.getClass().getName();
            if (hasPrefix(name))
                loadWindowState(frame);
        }
    }

    protected void loadWindowState(JInternalFrame window) {
        var name = window.getClass().getName();
        var isMinimized = Boolean.parseBoolean(getProperty(name, "Minimized"));
        var x = Integer.parseInt(getProperty(name, "PositionX"));
        var y = Integer.parseInt(getProperty(name, "PositionY"));
        var width = Integer.parseInt(getProperty(name, "Width"));
        var height = Integer.parseInt(getProperty(name, "Height"));
        try {
            window.setIcon(isMinimized);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        window.setLocation(x, y);
        window.setSize(width, height);
    }

    private String getProperty(String prefix, String property) {
        return properties.getProperty(prefix + "." + property);
    }

    private void setProperty(String prefix, String property, String value) {
        properties.setProperty(prefix + "." + property, value);
    }

    protected ResourceBundle loadLocalization(String localizationPath)
    {
        try {
            var locale = new Locale(properties.getProperty("lang"), properties.getProperty("country"));
            return ResourceBundle.getBundle(localizationPath, locale);
        } catch (MissingResourceException e) {
            var locale = new Locale("en");
            return ResourceBundle.getBundle(localizationPath, locale);
        }
    }
}
