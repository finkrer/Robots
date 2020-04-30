package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;


public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final Config config;

    public MainApplicationFrame() {
        config = new Config("resources/config.properties",
                "resources/LocalizationResources");

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        var logWindow = createLogWindow();
        addWindow(logWindow);

        var gameWindow = createGameWindow();
        addWindow(gameWindow);

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

    protected GameWindow createGameWindow() {
        var gameWindow = new GameWindow(config);
        gameWindow.setSize(400, 400);
        return gameWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
}
