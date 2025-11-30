import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import gui.LoginFrame;

public class Main {

    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

            System.out.println("Could not set system look and feel");
        }



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                LoginFrame loginFrame = new LoginFrame();


                loginFrame.setVisible(true);
            }
        });


    }
}