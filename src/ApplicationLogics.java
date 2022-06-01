import org.apache.commons.math3.random.MersenneTwister;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import javax.swing.*;
public class ApplicationLogics implements ActionListener{
    JFrame frame;
    JButton generate;
    JButton ret;
    JTextField messageText;
    JTextField score;
    JLabel helpFirstLabel;
    JLabel helpSecondLabel;
    JPanel p1;
    JPanel p2;
    JPanel p3;
    JPanel p4;
    boolean check = false;

    public static void main(String[] args) {
        ApplicationLogics przyciski = new ApplicationLogics();
        przyciski.buildGUI();
    }

    public void buildGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        generate = new JButton();
        generate.setText("Wygeneruj hash i klucze oraz odszyfruj wiadomość!");
        generate.addActionListener(this);
        p1 = new JPanel();
        p1.add(generate);
        p2 = new JPanel();
        p2.setLayout(new GridLayout(3,1));
        messageText = new JTextField();
        messageText.setHorizontalAlignment(JLabel.CENTER);
        helpFirstLabel = new JLabel("Uzupełnij wiadomość w polu tekstowym do wysłania!");
        helpFirstLabel.setHorizontalAlignment(JLabel.CENTER);
        p2.add(messageText);
        p2.add(helpFirstLabel);
        p2.add(p1);
        frame.add(p2);
        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent action) {
        if (action.getSource() == generate) {
            try {
                generate();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(action.getSource() == ret){
            p3.setVisible(false);
            p4.setVisible(false);
            p1.setVisible(true);
            p2.setVisible(true);
            messageText.setText("");
        }
    }

    private void generate() throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        String message = messageText.getText();
        byte[] messageByte = message.getBytes();
        File file = new File("paper_shredder_1.wav");
        InputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fis.read(buffer, 0, buffer.length);
        int[] Tab = new int[buffer.length];
        for(int i = 0; i< buffer.length; i++) {
            int i1 = buffer[i];
            if (i1 == 0) {
                continue;
            }
            Tab[i] = i1;
        }
        byte[] b = new byte[buffer.length];
        MersenneTwister m = new MersenneTwister(Tab);
        for(int i = 0; i< buffer.length; i++){
            b[i] = (byte) (m.nextInt() & 0xFF);
        }

        // generowanie kluczy w oparciu o 2048 bitowe wartości p i q wygenerowane z mojego
        // generatora poprzez skorzystanie z wartości z tablicy bajtowej b[]
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom(b));
        KeyPair keyPair = keyGen.generateKeyPair();

        // obiekt dla operacji mojego podpisu cyfrowego
        Signature signer = Signature.getInstance("SHA256withRSA");

        // złożenie podpisu poprzez wyliczenie funkcji skrótu z wiadomości (wykorzystanie sha256)
        //i przekształcenie jej za pomocą klucza prywatnego
        signer.initSign(keyPair.getPrivate(), new SecureRandom(b));
        signer.update(messageByte);

        // weryfikacja podpisu poprzez samodzielne wyliczenie funkcji skrótu z przekazanej wiadomości
        //i przekształcenie podpisu za pomocą klucza publicznego odzyskując w ten sposób
        //skrót wyliczony przy podpisie
        byte[] signatureValue = signer.sign();
        signer.initVerify(keyPair.getPublic());
        signer.update(messageByte);

        //jeżeli wartości funkcji skrótu są równe to podpis jest tożsamy z otrzymaną wiadomością
        //zwraca true, jeżeli zaś nie, to zwraca false
        if(signer.verify(signatureValue) == true){
            check = true;
        }
        else{
            check = false;
        }
        p1.setVisible(false);
        p2.setVisible(false);
        secondWindow();
        score.setText("Czy poprawnie zweryfikowano podpis? " + check);
        score.setHorizontalAlignment(JLabel.CENTER);
    }

    public void secondWindow() {
        p3 = new JPanel();
        ret = new JButton();
        ret.setText("Wykonaj jeszcze raz");
        ret.addActionListener(this);
        p3 = new JPanel();
        p3.add(ret);
        p4 = new JPanel();
        p4.setLayout(new GridLayout(3,1));
        score = new JTextField();
        helpSecondLabel = new JLabel("Możesz wykonań podpis jeszcze raz!");
        helpSecondLabel.setHorizontalAlignment(JLabel.CENTER);
        p4.add(score);
        p4.add(helpSecondLabel);
        p4.add(p3);
        frame.add(p4);
        frame.setVisible(true);
    }
}