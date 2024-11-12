import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractorApp extends JFrame {
    private JTextArea textArea;
    private JButton loadTextFileButton;
    private JButton loadStopWordsFileButton;
    private JButton saveResultsButton;
    private File textFile;
    private File stopWordsFile;
    private Set<String> stopWords = new TreeSet<>();
    private Map<String, Integer> wordFrequency = new HashMap<>();

    public TagExtractorApp() {
        setTitle("Tag Extractor");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        loadTextFileButton = new JButton("Load Text File");
        loadStopWordsFileButton = new JButton("Load Stop Words File");
        saveResultsButton = new JButton("Save Results");
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);

        panel.add(loadTextFileButton);
        panel.add(loadStopWordsFileButton);
        panel.add(saveResultsButton);
        panel.add(new JScrollPane(textArea));

        add(panel, BorderLayout.CENTER);

        loadTextFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTextFile();
            }
        });

        loadStopWordsFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStopWordsFile();
            }
        });

        saveResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveResults();
            }
        });
    }

    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textFile = fileChooser.getSelectedFile();
            textArea.append("Loaded text file: " + textFile.getName() + "\n");
            processTextFile();
        }
    }

    private void loadStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            stopWordsFile = fileChooser.getSelectedFile();
            loadStopWords();
            textArea.append("Loaded stop words file: " + stopWordsFile.getName() + "\n");
        }
    }

    private void loadStopWords() {
        stopWords.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while reading the stop words file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void processTextFile() {
        // Check if the text file is loaded
        if (textFile == null) {
            textArea.append("Please load a text file first.\n");
            return;
        }

        wordFrequency.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z']", " ").toLowerCase(); // Keep apostrophes for contractions
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!stopWords.contains(word) && !word.isEmpty()) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
            displayResults();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while reading the text file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayResults() {
        textArea.setText(""); // Clear previous results
        textArea.append("\nExtracted Tags and Frequencies:\n");
        textArea.append("-------------------------------\n");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        // Auto-scroll to the end
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private void saveResults() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(new FileWriter(saveFile))) {
                for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    out.println(entry.getKey() + ": " + entry.getValue());
                }
                textArea.append("\nResults saved to " + saveFile.getAbsolutePath() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "An error occurred while saving the results.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TagExtractorApp().setVisible(true));
    }
}
