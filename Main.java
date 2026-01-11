import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.List;

public class Main extends Application {
    private Dictionary dictionary;
    private TextArea outputArea;
    private Stage primaryStage;
    private String currentGeneratedSentences = ""; // Store generated sentences for saving

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        dictionary = new Dictionary();

        // Create main layout
        BorderPane root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create tabs
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                AddTab(),
                SrearchTab(),
                UpdateTab(),
                DeleteTab(),
                TranslateTab(),
                GenerateTab(),
                StatsTab()
        );

        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(100);

        VBox mainBox = new VBox(10, tabPane, new Label("Output:"), outputArea);
        mainBox.setPadding(new Insets(10));

        root.setCenter(mainBox);

        // Create scene and show stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Dictionary System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");

        // Load Dictionary
        MenuItem loadItem = new MenuItem("Load Dictionary");
        loadItem.setOnAction(e -> loadDictionary());

        // Save Dictionary
        MenuItem saveItem = new MenuItem("Save Dictionary");
        saveItem.setOnAction(e -> saveDictionary());

        // Exit
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());

        fileMenu.getItems().addAll(loadItem, saveItem, new SeparatorMenuItem(), exitItem);

        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    private void loadDictionary() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Dictionary File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            int count = dictionary.loadFromFile(file.getAbsolutePath());
            showOutput("Loaded " + count + " words from: " + file.getName());
        }
    }

    private void saveDictionary() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dictionary File");
        fileChooser.setInitialFileName("dictionary.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            int count = dictionary.saveToFile(file.getAbsolutePath());
            if (count >= 0) {
                showOutput("Saved " + count + " words to: " + file.getName());
            } else {
                showOutput("Error saving dictionary file!");
            }
        }
    }

    private void saveGeneratedSentences(String sentences) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Generated Sentences");
        fileChooser.setInitialFileName("sentences.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(sentences);
                writer.close();
                showOutput("Generated sentences saved to: " + file.getName());
            } catch (IOException e) {
                showOutput("Error saving sentences file: " + e.getMessage());
            }
        }
    }

    private Tab AddTab() {
        Tab tab = new Tab("Add Word");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField wordField = new TextField();
        TextField engField = new TextField();
        TextField arabicField = new TextField();
        TextField exampleField = new TextField();

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Noun", "Verb", "Adjective", "Adverb", "Other");
        typeBox.setValue("Noun");

        Button addButton = new Button("Add Word");

        addButton.setOnAction(e -> {
            if (wordField.getText().isEmpty() ||
                    engField.getText().isEmpty() ||
                    arabicField.getText().isEmpty()) {
                showOutput("Error: Please fill all required fields");
                return;
            }

            boolean success = dictionary.addWord(
                    wordField.getText(),
                    engField.getText(),
                    arabicField.getText(),
                    exampleField.getText(),
                    typeBox.getValue()
            );

            if (success) {
                showOutput("Word added successfully!");
                // Clear all fields
                wordField.clear();
                engField.clear();
                arabicField.clear();
                exampleField.clear();
                typeBox.setValue("Noun");
            } else {
                showOutput("Error: Word already exists!");
            }
        });

        grid.add(new Label("English Word:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("English Meaning:"), 0, 1);
        grid.add(engField, 1, 1);
        grid.add(new Label("Arabic Meaning:"), 0, 2);
        grid.add(arabicField, 1, 2);
        grid.add(new Label("Example:"), 0, 3);
        grid.add(exampleField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeBox, 1, 4);
        grid.add(addButton, 1, 5);

        tab.setContent(grid);
        return tab;
    }

    private Tab SrearchTab() {
        Tab tab = new Tab("Search Word");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        ToggleGroup searchGroup = new ToggleGroup();
        RadioButton englishRadio = new RadioButton("Search in English");
        RadioButton arabicRadio = new RadioButton("Search in Arabic");
        englishRadio.setToggleGroup(searchGroup);
        arabicRadio.setToggleGroup(searchGroup);
        englishRadio.setSelected(true);

        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);

        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                showOutput("Error: Please enter a word to search");
                return;
            }

            Word result;
            if (englishRadio.isSelected()) {
                result = dictionary.searchEnglish(searchText);
            } else {
                result = dictionary.searchArabic(searchText);
            }

            if (result != null) {
                resultArea.setText(
                        "Word: " + result.getWord() + "\n" +
                                "English Meaning: " + result.getEnglishMeaning() + "\n" +
                                "Arabic Meaning: " + result.getArabicMeaning() + "\n" +
                                "Example: " + result.getExample() + "\n" +
                                "Type: " + result.getType()
                );
                showOutput("Word found!");
            } else {
                resultArea.setText("Word not found!");
                showOutput("Word not found!");
            }
        });

        vbox.getChildren().addAll(
                new Label("Search Type:"),
                new HBox(10, englishRadio, arabicRadio),
                new Label("Enter word:"),
                searchField,
                searchButton,
                new Label("Result:"),
                resultArea
        );

        tab.setContent(vbox);
        return tab;
    }

    private Tab UpdateTab() {
        Tab tab = new Tab("Update Word");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField wordField = new TextField();
        Button findButton = new Button("Find");

        TextField engField = new TextField();
        TextField arabicField = new TextField();
        TextField exampleField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Noun", "Verb", "Adjective", "Adverb", "Other");

        engField.setDisable(true);
        arabicField.setDisable(true);
        exampleField.setDisable(true);
        typeBox.setDisable(true);

        Button updateButton = new Button("Update");
        updateButton.setDisable(true);

        findButton.setOnAction(e -> {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                showOutput("Error: Please enter a word");
                return;
            }

            Word found = dictionary.searchEnglish(word);
            if (found != null) {
                engField.setText(found.getEnglishMeaning());
                arabicField.setText(found.getArabicMeaning());
                exampleField.setText(found.getExample());
                typeBox.setValue(found.getType());

                // Enable fields
                engField.setDisable(false);
                arabicField.setDisable(false);
                exampleField.setDisable(false);
                typeBox.setDisable(false);
                updateButton.setDisable(false);

                showOutput("Word found! You can update it now.");
            } else {
                showOutput("Error: Word not found!");
            }
        });

        updateButton.setOnAction(e -> {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                showOutput("Error: Please enter a word");
                return;
            }

            boolean success = dictionary.updateWord(
                    word,
                    engField.getText(),
                    arabicField.getText(),
                    exampleField.getText(),
                    typeBox.getValue()
            );

            if (success) {
                showOutput("Word updated successfully!");
                updateButton.setDisable(true);
                // Clear fields
                wordField.clear();
                engField.clear();
                arabicField.clear();
                exampleField.clear();
                typeBox.setValue("Noun");

                // Disable fields again
                engField.setDisable(true);
                arabicField.setDisable(true);
                exampleField.setDisable(true);
                typeBox.setDisable(true);
            } else {
                showOutput("Error updating word!");
            }
        });

        grid.add(new Label("Word to update:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(findButton, 2, 0);

        grid.add(new Label("English Meaning:"), 0, 1);
        grid.add(engField, 1, 1);

        grid.add(new Label("Arabic Meaning:"), 0, 2);
        grid.add(arabicField, 1, 2);

        grid.add(new Label("Example:"), 0, 3);
        grid.add(exampleField, 1, 3);

        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeBox, 1, 4);

        grid.add(updateButton, 1, 5);

        tab.setContent(grid);
        return tab;
    }

    private Tab DeleteTab() {
        Tab tab = new Tab("Delete Word");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField wordField = new TextField();
        wordField.setPromptText("Enter English word to delete");

        Button deleteButton = new Button("Delete Word");


        deleteButton.setOnAction(e -> {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                showOutput("Error: Please enter a word");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete word: " + word);
            alert.setContentText("Are you sure?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = dictionary.deleteWord(word);
                    if (success) {
                        showOutput("Word deleted successfully!");
                        wordField.clear();
                    } else {
                        showOutput("Error: Word not found!");
                    }
                }
            });
        });

        vbox.getChildren().addAll(
                new Label("Enter English word to delete:"),
                wordField,
                deleteButton
        );

        tab.setContent(vbox);
        return tab;
    }

    private Tab TranslateTab() {
        Tab tab = new Tab("Translate");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        ToggleGroup translateGroup = new ToggleGroup();
        RadioButton engToArabic = new RadioButton("English to Arabic");
        RadioButton arabicToEng = new RadioButton("Arabic to English");
        engToArabic.setToggleGroup(translateGroup);
        arabicToEng.setToggleGroup(translateGroup);
        engToArabic.setSelected(true);

        TextArea inputArea = new TextArea();
        inputArea.setPrefHeight(100);

        HBox buttonBox = new HBox(10);
        Button translateButton = new Button("Translate");
        Button clearButton = new Button("Clear");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(100);

        translateButton.setOnAction(e -> {
            String text = inputArea.getText().trim();
            if (text.isEmpty()) {
                showOutput("Error: Please enter text to translate");
                return;
            }

            String translation;
            if (engToArabic.isSelected()) {
                translation = dictionary.translateEnglishToArabic(text);
            } else {
                translation = dictionary.translateArabicToEnglish(text);
            }

            outputArea.setText(translation);
            showOutput("Translation completed!");
        });

        clearButton.setOnAction(e -> {
            inputArea.clear();
            outputArea.clear();
            showOutput("Translation fields cleared!");
        });

        buttonBox.getChildren().addAll(translateButton, clearButton);

        vbox.getChildren().addAll(
                new Label("Translation Direction:"),
                new HBox(10, engToArabic, arabicToEng),
                new Label("Input Text:"),
                inputArea,
                buttonBox,
                new Label("Translation:"),
                outputArea
        );

        tab.setContent(vbox);
        return tab;
    }

    private Tab GenerateTab() {
        Tab tab = new Tab("Generate Sentences");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        ToggleGroup languageGroup = new ToggleGroup();
        RadioButton englishRadio = new RadioButton("Generate English");
        RadioButton arabicRadio = new RadioButton("Generate Arabic");
        englishRadio.setToggleGroup(languageGroup);
        arabicRadio.setToggleGroup(languageGroup);
        englishRadio.setSelected(true);

        TextField countField = new TextField("1");
        countField.setPrefWidth(50);

        HBox buttonBox = new HBox(10);
        Button generateButton = new Button("Generate");
        Button clearButton = new Button("Clear");
        Button saveButton = new Button("Save Sentences");

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);

        generateButton.setOnAction(e -> {
            try {
                int count = Integer.parseInt(countField.getText());
                if (count <= 0 || count > 20) {
                    showOutput("Error: Enter number between 1 and 20");
                    return;
                }

                StringBuilder sentences = new StringBuilder();
                for (int i = 1; i <= count; i++) {
                    String sentence;
                    if (englishRadio.isSelected()) {
                        sentence = dictionary.generateEnglishSentence();
                    } else {
                        sentence = dictionary.generateArabicSentence();
                    }
                    sentences.append(i).append(". ").append(sentence).append("\n");
                }

                currentGeneratedSentences = sentences.toString();
                resultArea.setText(currentGeneratedSentences);
                showOutput("Generated " + count + " sentences");
            } catch (NumberFormatException ex) {
                showOutput("Error: Please enter a valid number");
            }
        });

        clearButton.setOnAction(e -> {
            resultArea.clear();
            currentGeneratedSentences = "";
            countField.setText("1");
            showOutput("Cleared generated sentences!");
        });

        saveButton.setOnAction(e -> {
            if (currentGeneratedSentences.isEmpty()) {
                showOutput("Error: No sentences to save! Generate sentences first.");
                return;
            }

            saveGeneratedSentences(currentGeneratedSentences);
        });

        buttonBox.getChildren().addAll(generateButton, clearButton, saveButton);

        vbox.getChildren().addAll(
                new Label("Language:"),
                new HBox(10, englishRadio, arabicRadio),
                new HBox(10, new Label("Number:"), countField),
                buttonBox,
                new Label("Generated Sentences:"),
                resultArea
        );

        tab.setContent(vbox);
        return tab;
    }

    private Tab StatsTab() {
        Tab tab = new Tab("Statistics");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Button refreshButton = new Button("Refresh Statistics");
        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setPrefHeight(300);

        refreshButton.setOnAction(e -> {
            StringBuilder stats = new StringBuilder();

            stats.append("=== General Statistics ===\n");
            stats.append("Total words: ").append(dictionary.getTotalWords()).append("\n\n");

            stats.append("=== Words per Letter ===\n");
            List<DLinkedList.LetterCount> letterCounts = dictionary.getLetterCounts();
            for (DLinkedList.LetterCount lc : letterCounts) {
                if (lc.getCount() > 0) {
                    stats.append(lc.getLetter()).append(": ").append(lc.getCount()).append(" words\n");
                }
            }
            stats.append("\n");

            stats.append("=== Words per Type ===\n");
            List<DLinkedList.TypeCount> typeCounts = dictionary.getTypeCounts();
            for (DLinkedList.TypeCount tc : typeCounts) {
                stats.append(tc.getType()).append(": ").append(tc.getCount()).append(" words\n");
            }
            stats.append("\n");

            stats.append("=== AVL Tree Heights ===\n");
            List<DLinkedList.LetterHeight> treeHeights = dictionary.getTreeHeights();
            for (DLinkedList.LetterHeight th : treeHeights) {
                stats.append("Letter ").append(th.getLetter())
                        .append(": height = ").append(th.getHeight()).append("\n");
            }

            statsArea.setText(stats.toString());
            showOutput("Statistics refreshed!");
        });

        vbox.getChildren().addAll(
                refreshButton,
                new Label("Statistics:"),
                statsArea
        );

        tab.setContent(vbox);
        return tab;
    }

    private void showOutput(String message) {
        outputArea.appendText(message + "\n");
        outputArea.setScrollTop(Double.MAX_VALUE);
    }
}