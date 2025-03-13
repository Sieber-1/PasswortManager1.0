import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class PasswordManager extends JFrame {

    private static final long serialVersionUID = 1L;

    // Here is an Example of a Passwort for "mySecret" in a SHA-256-Hash (Base64, UTF-8):
    // XxM8HLQ2/COpjbzGrWfM3ywNd81CayO2v1LXg8DFk6o= <--- The Hash Algorithmen
    // Copy the Algorithmen which u had generated through a Internet-Website in the String,here I will use the Hash of "mySecret"
    private static final String MASTER_PASSWORD_HASH = "0L5zNClDL38A1CXhqwA0Eq+nXUH+KA2Lsus+gv78VrY=";

    private DefaultListModel<PasswordEntry> listModel;
    private JList<PasswordEntry> entryList;
    private JTextField serviceField;
    private JPasswordField passwordField;
    private final String DATA_FILE = "passwords.dat";

    public PasswordManager() {
        super("Password Manager ");
        initComponents();
        loadFromFile();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(entryList);

        serviceField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");

        // Panel für Eingabefelder
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Service:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(serviceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(passwordField, gbc);

        // Panel für Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(inputPanel, BorderLayout.NORTH);
        container.add(buttonPanel, BorderLayout.SOUTH);

        // ActionListener für die Buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String service = serviceField.getText().trim();
                String pwd = new String(passwordField.getPassword()).trim();
                if (!service.isEmpty() && !pwd.isEmpty()) {
                    PasswordEntry entry = new PasswordEntry(service, pwd);
                    listModel.addElement(entry);
                    serviceField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(
                            PasswordManager.this,
                            "Please enter both service and password."
                    );
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = entryList.getSelectedIndex();
                if (selected != -1) {
                    listModel.remove(selected);
                } else {
                    JOptionPane.showMessageDialog(
                            PasswordManager.this,
                            "Please select an entry to delete."
                    );
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFile();
            }
        });
    }

    private void saveToFile() {
        ArrayList<PasswordEntry> entries = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            entries.add(listModel.get(i));
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(entries);
            JOptionPane.showMessageDialog(this, "Data saved successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            @SuppressWarnings("unchecked")
            ArrayList<PasswordEntry> entries = (ArrayList<PasswordEntry>) ois.readObject();
            listModel.clear();
            for (PasswordEntry entry : entries) {
                listModel.addElement(entry);
            }
            JOptionPane.showMessageDialog(this, "Data loaded successfully.");
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Abfrage des Master-Passworts
        String input = JOptionPane.showInputDialog(null, "Enter Master Password:", "Login", JOptionPane.QUESTION_MESSAGE);

        // Falls auf "Abbrechen" geklickt wird oder nichts eingegeben wird, beenden
        if (input == null) {
            JOptionPane.showMessageDialog(null, "No password entered. Exiting.");
            System.exit(0);
        }

        // Debug-Ausgaben: Zeige, was der Benutzer eingegeben hat und welchen Hash wir berechnen
        System.out.println("Debug: Eingegebenes Passwort = '" + input + "'");

        String computedHash = hashString(input);
        System.out.println("Debug: Berechneter Hash = '" + computedHash + "'");
        System.out.println("Debug: Erwarteter Hash   = '" + MASTER_PASSWORD_HASH + "'");

        if (!MASTER_PASSWORD_HASH.equals(computedHash)) {
            JOptionPane.showMessageDialog(null, "Incorrect password. Exiting.");
            System.exit(0);
        }

        // Wenn das Passwort stimmt, GUI starten
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PasswordManager().setVisible(true);
            }
        });
    }

    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Klasse zum Speichern eines Service/Password-Eintrags.
class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private String service;
    private String password;

    public PasswordEntry(String service, String password) {
        this.service = service;
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return service + " : " + password;
    }
}
