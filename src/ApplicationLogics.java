import org.apache.commons.math3.random.MersenneTwister;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import javax.swing.*;
public class ApplicationLogics implements ActionListener{
    JFrame frame;
    JButton generate;
    JButton change;
    JButton odsz;
    JButton pok;
    JTextField messageText;
    JTextField messageText_2;
    JLabel helpFirstLabel;
    JPanel p1;
    JPanel p2;
    JPanel p5;
    JPanel p6;
    String check = "";
    String message;
    PublicKey keyPairaa;
    Signature signerr;
    Signature sig;
    byte[] bya;
    String helpString;

    public static void main(String[] args) {
        ApplicationLogics przyciski = new ApplicationLogics();
        przyciski.buildGUI();
    }

    public void buildGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        generate = new JButton();
        generate.setText("Wygeneruj hash i klucze!");
        generate.addActionListener(this);
        p1 = new JPanel();
        p1.add(generate);
        p2 = new JPanel();
        p2.setLayout(new GridLayout(3,1));
        messageText = new JTextField();
        messageText.setHorizontalAlignment(JLabel.CENTER);
        helpFirstLabel = new JLabel("Uzupełnij wiadomość w polu tekstowym!");
        helpFirstLabel.setHorizontalAlignment(JLabel.CENTER);
        p2.add(messageText);
        p2.add(helpFirstLabel);
        p2.add(p1);
        frame.add(p2);
        frame.setVisible(true);
    }
    public void drugie(){

        odsz = new JButton();
        odsz.setText("Odszyfruj");
        odsz.addActionListener(this);
        change = new JButton();
        change.setText("Zmień");
        change.addActionListener(this);
        pok = new JButton();
        pok.setText("Wróć do oryginalnej wiadomości");
        pok.addActionListener(this);
        p5 = new JPanel();
        p5.add(odsz);
        p5.add(change);
        p5.add(pok);
        p6 = new JPanel();
        p6.setLayout(new GridLayout(3,1));
        messageText_2 = new JTextField();
        messageText_2.setText(messageText.getText());
        messageText_2.setHorizontalAlignment(JLabel.CENTER);
        helpFirstLabel = new JLabel("Odszyfruj podpis bądź wprowadź poprawki do wiadomości i kliknij przycisk Zmień. Możesz następnie odszyfrować ponownie podpis!");
        helpFirstLabel.setHorizontalAlignment(JLabel.CENTER);
        p6.add(messageText_2);
        p6.add(helpFirstLabel);
        p6.add(p5);
        frame.add(p6);
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
        if(action.getSource() == pok){
            signerr = sig;
            messageText_2.setText(messageText.getText());

        }
        if(action.getSource() == odsz){
            p5.setVisible(true);
            p6.setVisible(true);
            byte[] signatureValue = new byte[0];
            try {
                signatureValue = signerr.sign();
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            try {
                signerr.initVerify(keyPairaa);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            try {
                signerr.update(bya);
            } catch (SignatureException e) {
                e.printStackTrace();
            }

            //jeżeli wartości funkcji skrótu są równe to podpis jest tożsamy z otrzymaną wiadomością
            //zwraca true, jeżeli zaś nie, to zwraca false
            try {
                if(signerr.verify(signatureValue) == true){
                    check = "TAK";
                }
                else{
                    check = "NIE";
                }
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            messageText_2.setText("Czy poprawnie zweryfikowano podpis? " + check);
        }
        if(action.getSource() == change) {
            helpString = messageText_2.getText();
            byte[] messageByte = helpString.getBytes();
            File file = new File("paper_shredder_1.wav");
            InputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[(int) file.length()];
            try {
                fis.read(buffer, 0, buffer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            KeyPairGenerator keyGen = null;
            try {
                keyGen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            keyGen.initialize(2048, new SecureRandom(b));
            KeyPair keyPair = keyGen.generateKeyPair();
           // keyPairaa = keyPair;

            // obiekt dla operacji mojego podpisu cyfrowego
            Signature signer = null;
            try {
                signer = Signature.getInstance("SHA256withRSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // złożenie podpisu poprzez wyliczenie funkcji skrótu z wiadomości (wykorzystanie sha256)
            //i przekształcenie jej za pomocą klucza prywatnego
            try {
                signer.initSign(keyPair.getPrivate(), new SecureRandom(b));
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            try {
                signer.update(messageByte);
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            signerr = signer;
        }
    }

    private void generate() throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        message = messageText.getText();
        byte[] messageByte = message.getBytes();
        bya = messageByte;
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
        keyPairaa = keyPair.getPublic();

        // obiekt dla operacji mojego podpisu cyfrowego
        Signature signer = Signature.getInstance("SHA256withRSA");

        // złożenie podpisu poprzez wyliczenie funkcji skrótu z wiadomości (wykorzystanie sha256)
        //i przekształcenie jej za pomocą klucza prywatnego
        signer.initSign(keyPair.getPrivate(), new SecureRandom(b));
        signer.update(messageByte);
        sig = signer;
        signerr = signer;
        p1.setVisible(false);
        p2.setVisible(false);
        drugie();
    }
}