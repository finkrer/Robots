package gui;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.*;

import NatSelection.ControlWindow;
import NatSelection.NSWindow;
import log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Config config;
    
    public MainApplicationFrame() {
        config = new Config("src/resources/config.properties",
                "resources/LocalizationResources");

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);


        NSWindow gameWindow = new NSWindow(config);
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        ControlWindow controlWindow = new ControlWindow(config, gameWindow);
        controlWindow.setSize(400, 200);
        controlWindow.setLocation(400, 0);
        addWindow(controlWindow);

        var windows = desktopPane.getAllFrames();
        if (config.windowConfigsArePresent() && userWantsToRestoreWindows())
            config.loadWindowStates(windows);

        setJMenuBar(new MenuBar(this, config));

        // Confirm close window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Object[] options = {config.getLocalization("yes"), config.getLocalization("no")};
                if (JOptionPane.showOptionDialog((Component) e.getSource(),
                        config.getLocalization("closeWindowQuestion"), config.getLocalization("closeWindowTitle"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, null) == 0) {
                    config.saveWindowStates(desktopPane.getAllFrames());
                    System.exit(0);
                }
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private boolean userWantsToRestoreWindows()
    {
        Object[] options = {config.getLocalization("yes"), config.getLocalization("no")};
        return JOptionPane.showOptionDialog(this,
                config.getLocalization("restoreWindowsQuestion"),
                config.getLocalization("restoreWindowsTitle"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, null) == 0;
    }

    protected LogWindow createLogWindow() {
        var logWindow = new LogWindow(Logger.getDefaultLogSource(), config);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(config.getLocalization("protocolWorking"));
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }


}
