package by.bsu.fpmi.vet.ui.frame;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.ui.action.Actions;
import by.bsu.fpmi.vet.util.MessageUtils;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainFrame extends JFrame {
    /**
     * {@link ApplicationContext} can be used inside this method.
     */
    public void init() {
        setTitle(MessageUtils.getMessage("ui.mainFrame.title"));
        setActionOnClose();
        setupMainMenu();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void setActionOnClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Actions.EXIT.get().actionPerformed(null);
            }
        });
    }

    private void setupMainMenu() {
        JMenuBar mainMenu = new JMenuBar();

        JMenu fileMenu = new JMenu(Actions.FILE.get());
        fileMenu.addSeparator();
        fileMenu.add(Actions.EXIT.get());
        mainMenu.add(fileMenu);

        setJMenuBar(mainMenu);
    }

    private void arrangeComponents() {
        // TODO: add components
    }

    private void setupSizeAndLocation() {
        pack();
        Dimension size = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
        setMinimumSize(size);
    }
}
