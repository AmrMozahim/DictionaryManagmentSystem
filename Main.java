import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class Main extends Application {
    private DictionaryManager manager;
    private TabPane tabPane;
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        manager = new DictionaryManager();

        // إنشاء الواجهة الرئيسية
        BorderPane root = new BorderPane();

        // إنشاء شريط القوائم
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // إنشاء منطقة الإخراج
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(150);

        // إنشاء تبويبات الوظائف
        tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createAddWordTab(),
                createSearchTab(),
                createUpdateTab(),
                createDeleteTab(),
                createFileTab(primaryStage),
                createTranslateTab(),
                createGenerateTab(),
                createStatisticsTab()
        );

        VBox centerBox = new VBox(10, tabPane, new Label("الإخراج:"), outputArea);
        centerBox.setPadding(new Insets(10));
        root.setCenter(centerBox);

        // إنشاء المشهد وعرض النافذة
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("نظام القاموس - AVL Trees");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        // قائمة ملف
        Menu fileMenu = new Menu("ملف");
        MenuItem loadItem = new MenuItem("تحميل من ملف");
        loadItem.setOnAction(e -> loadFromFile(stage));

        MenuItem saveItem = new MenuItem("حفظ إلى ملف");
        saveItem.setOnAction(e -> saveToFile(stage));

        MenuItem exitItem = new MenuItem("خروج");
        exitItem.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(loadItem, saveItem, new SeparatorMenuItem(), exitItem);

        // قائمة مساعدة
        Menu helpMenu = new Menu("مساعدة");
        MenuItem aboutItem = new MenuItem("حول");
        aboutItem.setOnAction(e -> showAbout());

        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private Tab createAddWordTab() {
        Tab tab = new Tab("إضافة كلمة جديدة");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // حقول الإدخال
        TextField wordField = new TextField();
        TextField englishField = new TextField();
        TextField arabicField = new TextField();
        TextField exampleField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Noun", "Verb", "Adjective", "Adverb", "Preposition", "Other");
        typeCombo.setValue("Noun");

        // تسميات
        grid.add(new Label("الكلمة (الإنجليزية):"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("المعنى الإنجليزي:"), 0, 1);
        grid.add(englishField, 1, 1);
        grid.add(new Label("المعنى العربي:"), 0, 2);
        grid.add(arabicField, 1, 2);
        grid.add(new Label("مثال:"), 0, 3);
        grid.add(exampleField, 1, 3);
        grid.add(new Label("النوع:"), 0, 4);
        grid.add(typeCombo, 1, 4);

        // زر الإضافة
        Button addButton = new Button("إضافة الكلمة");
        addButton.setOnAction(e -> {
            if (wordField.getText().isEmpty() || englishField.getText().isEmpty() ||
                    arabicField.getText().isEmpty()) {
                showOutput("خطأ: يجب ملء جميع الحقول المطلوبة!");
                return;
            }

            boolean success = manager.addWord(
                    wordField.getText(),
                    englishField.getText(),
                    arabicField.getText(),
                    exampleField.getText(),
                    typeCombo.getValue()
            );

            if (success) {
                showOutput("تم إضافة الكلمة بنجاح!");
                clearFields(wordField, englishField, arabicField, exampleField);
            } else {
                showOutput("خطأ: الكلمة موجودة مسبقاً!");
            }
        });

        grid.add(addButton, 1, 5);

        tab.setContent(new ScrollPane(grid));
        return tab;
    }

    private Tab createSearchTab() {
        Tab tab = new Tab("بحث عن كلمة");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // اختيار نوع البحث
        ToggleGroup searchGroup = new ToggleGroup();
        RadioButton englishSearch = new RadioButton("بحث بالإنجليزية");
        RadioButton arabicSearch = new RadioButton("بحث بالعربية");
        englishSearch.setToggleGroup(searchGroup);
        arabicSearch.setToggleGroup(searchGroup);
        englishSearch.setSelected(true);

        HBox radioBox = new HBox(20, englishSearch, arabicSearch);

        // حقل البحث
        TextField searchField = new TextField();
        searchField.setPromptText("أدخل الكلمة للبحث");

        // زر البحث
        Button searchButton = new Button("بحث");

        // منطقة عرض النتائج
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(200);

        searchButton.setOnAction(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                showOutput("خطأ: أدخل كلمة للبحث!");
                return;
            }

            Word result;
            if (englishSearch.isSelected()) {
                result = manager.searchEnglish(searchText);
            } else {
                result = manager.searchArabic(searchText);
            }

            if (result != null) {
                resultArea.setText(
                        "الكلمة: " + result.getWord() + "\n" +
                                "المعنى الإنجليزي: " + result.getEnglishMeaning() + "\n" +
                                "المعنى العربي: " + result.getArabicMeaning() + "\n" +
                                "المثال: " + result.getExample() + "\n" +
                                "النوع: " + result.getType()
                );
                showOutput("تم العثور على الكلمة بنجاح!");
            } else {
                resultArea.setText("الكلمة غير موجودة في القاموس!");
                showOutput("الكلمة غير موجودة!");
            }
        });

        vbox.getChildren().addAll(
                new Label("اختر نوع البحث:"),
                radioBox,
                new Label("الكلمة:"),
                searchField,
                searchButton,
                new Label("النتيجة:"),
                resultArea
        );

        tab.setContent(new ScrollPane(vbox));
        return tab;
    }

    private Tab createUpdateTab() {
        Tab tab = new Tab("تحديث كلمة");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // حقل الكلمة للتحديث
        TextField wordField = new TextField();
        Button findButton = new Button("إيجاد الكلمة");

        // حقول التحديث
        TextField englishField = new TextField();
        TextField arabicField = new TextField();
        TextField exampleField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Noun", "Verb", "Adjective", "Adverb", "Preposition", "Other");

        // تعطيل الحقول حتى يتم إيجاد الكلمة
        englishField.setDisable(true);
        arabicField.setDisable(true);
        exampleField.setDisable(true);
        typeCombo.setDisable(true);

        findButton.setOnAction(e -> {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                showOutput("أدخل كلمة للبحث!");
                return;
            }

            Word foundWord = manager.searchEnglish(word);
            if (foundWord != null) {
                englishField.setText(foundWord.getEnglishMeaning());
                arabicField.setText(foundWord.getArabicMeaning());
                exampleField.setText(foundWord.getExample());
                typeCombo.setValue(foundWord.getType());

                // تمكين الحقول
                englishField.setDisable(false);
                arabicField.setDisable(false);
                exampleField.setDisable(false);
                typeCombo.setDisable(false);

                showOutput("تم إيجاد الكلمة، يمكنك تعديلها الآن.");
            } else {
                showOutput("الكلمة غير موجودة!");
            }
        });

        // زر التحديث
        Button updateButton = new Button("تحديث الكلمة");
        updateButton.setDisable(true);

        updateButton.setOnAction(e -> {
            boolean success = manager.updateWord(
                    wordField.getText(),
                    englishField.getText(),
                    arabicField.getText(),
                    exampleField.getText(),
                    typeCombo.getValue()
            );

            if (success) {
                showOutput("تم تحديث الكلمة بنجاح!");
                updateButton.setDisable(true);
            } else {
                showOutput("خطأ في تحديث الكلمة!");
            }
        });

        // تفعيل زر التحديث عند التعديل
        englishField.textProperty().addListener((obs, old, newVal) -> updateButton.setDisable(false));
        arabicField.textProperty().addListener((obs, old, newVal) -> updateButton.setDisable(false));

        // إضافة العناصر إلى الشبكة
        grid.add(new Label("الكلمة المراد تحديثها:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(findButton, 2, 0);

        grid.add(new Label("المعنى الإنجليزي الجديد:"), 0, 1);
        grid.add(englishField, 1, 1);

        grid.add(new Label("المعنى العربي الجديد:"), 0, 2);
        grid.add(arabicField, 1, 2);

        grid.add(new Label("المثال الجديد:"), 0, 3);
        grid.add(exampleField, 1, 3);

        grid.add(new Label("النوع الجديد:"), 0, 4);
        grid.add(typeCombo, 1, 4);

        grid.add(updateButton, 1, 5);

        tab.setContent(new ScrollPane(grid));
        return tab;
    }

    private Tab createDeleteTab() {
        Tab tab = new Tab("حذف كلمة");
        tab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField wordField = new TextField();
        wordField.setPromptText("أدخل الكلمة الإنجليزية لحذفها");

        Button deleteButton = new Button("حذف الكلمة");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

        deleteButton.setOnAction(e -> {
            String word = wordField.getText().trim();
            if (word.isEmpty()) {
                showOutput("أدخل كلمة للحذف!");
                return;
            }

            // طلب التأكيد
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("تأكيد الحذف");
            alert.setHeaderText("هل أنت متأكد من حذف الكلمة: " + word + "؟");
            alert.setContentText("هذا الإجراء لا يمكن التراجع عنه.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = manager.deleteWord(word);
                    if (success) {
                        showOutput("تم حذف الكلمة بنجاح!");
                        wordField.clear();
                    } else {
                        showOutput("خطأ: الكلمة غير موجودة!");
                    }
                }
            });
        });

        vbox.getChildren().addAll(
                new Label("أدخل الكلمة الإنجليزية:"),
                wordField,
                deleteButton
        );

        tab.setContent(vbox);
        return tab;
    }

    private Tab createFileTab(Stage stage) {
        Tab tab = new Tab("الملفات");
        tab.setClosable(false);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        // قسم التحميل
        VBox loadSection = new VBox(10);
        loadSection.setPadding(new Insets(10));
        loadSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

        Button loadButton = new Button("اختر ملف للتحميل");
        TextField loadPathField = new TextField();
        loadPathField.setEditable(false);
        loadPathField.setPromptText("سيتم عرض مسار الملف هنا");

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("اختر ملف القاموس");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                loadPathField.setText(file.getAbsolutePath());
                int count = manager.loadFromFile(file.getAbsolutePath());
                showOutput("تم تحميل " + count + " كلمة من الملف.");
            }
        });

        loadSection.getChildren().addAll(
                new Label("تحميل القاموس من ملف:"),
                loadPathField,
                loadButton
        );

        // قسم الحفظ
        VBox saveSection = new VBox(10);
        saveSection.setPadding(new Insets(10));
        saveSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

        Button saveButton = new Button("اختر موقع للحفظ");
        TextField savePathField = new TextField();
        savePathField.setEditable(false);
        savePathField.setPromptText("سيتم عرض مسار الملف هنا");

        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("حفظ القاموس إلى ملف");
            fileChooser.setInitialFileName("dictionary.txt");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                savePathField.setText(file.getAbsolutePath());
                int count = manager.saveToFile(file.getAbsolutePath());
                if (count >= 0) {
                    showOutput("تم حفظ " + count + " كلمة إلى الملف.");
                }
            }
        });

        saveSection.getChildren().addAll(
                new Label("حفظ القاموس إلى ملف:"),
                savePathField,
                saveButton
        );

        vbox.getChildren().addAll(loadSection, saveSection);
        tab.setContent(new ScrollPane(vbox));
        return tab;
    }

    private Tab createTranslateTab() {
        Tab tab = new Tab("ترجمة نص");
        tab.setClosable(false);

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        // اختيار نوع الترجمة
        ToggleGroup translateGroup = new ToggleGroup();
        RadioButton englishToArabic = new RadioButton("الإنجليزية → العربية");
        RadioButton arabicToEnglish = new RadioButton("العربية → الإنجليزية");
        englishToArabic.setToggleGroup(translateGroup);
        arabicToEnglish.setToggleGroup(translateGroup);
        englishToArabic.setSelected(true);

        HBox radioBox = new HBox(20, englishToArabic, arabicToEnglish);

        // حقل النص الأصلي
        TextArea originalText = new TextArea();
        originalText.setPromptText("أدخل النص للترجمة هنا...");
        originalText.setWrapText(true);
        originalText.setPrefHeight(100);

        // زر الترجمة
        Button translateButton = new Button("ترجمة");

        // حقل النص المترجم
        TextArea translatedText = new TextArea();
        translatedText.setEditable(false);
        translatedText.setWrapText(true);
        translatedText.setPrefHeight(100);
        translatedText.setStyle("-fx-control-inner-background: #f0f0f0;");

        translateButton.setOnAction(e -> {
            String text = originalText.getText().trim();
            if (text.isEmpty()) {
                showOutput("أدخل نصاً للترجمة!");
                return;
            }

            String translation;
            if (englishToArabic.isSelected()) {
                translation = manager.translateEnglishToArabic(text);
            } else {
                translation = manager.translateArabicToEnglish(text);
            }

            translatedText.setText(translation);
            showOutput("تمت الترجمة بنجاح!");
        });

        vbox.getChildren().addAll(
                new Label("اختر اتجاه الترجمة:"),
                radioBox,
                new Label("النص الأصلي:"),
                originalText,
                translateButton,
                new Label("النص المترجم:"),
                translatedText
        );

        tab.setContent(new ScrollPane(vbox));
        return tab;
    }

    private Tab createGenerateTab() {
        Tab tab = new Tab("توليد جمل");
        tab.setClosable(false);

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        // اختيار لغة الجملة
        ToggleGroup languageGroup = new ToggleGroup();
        RadioButton englishRadio = new RadioButton("توليد جمل إنجليزية");
        RadioButton arabicRadio = new RadioButton("توليد جمل عربية");
        englishRadio.setToggleGroup(languageGroup);
        arabicRadio.setToggleGroup(languageGroup);
        englishRadio.setSelected(true);

        HBox languageBox = new HBox(20, englishRadio, arabicRadio);

        // عدد الجمل
        HBox countBox = new HBox(10);
        countBox.setAlignment(Pos.CENTER_LEFT);
        Label countLabel = new Label("عدد الجمل:");
        TextField countField = new TextField("1");
        countField.setPrefWidth(50);
        countBox.getChildren().addAll(countLabel, countField);

        // زر التوليد
        Button generateButton = new Button("توليد جمل");

        // منطقة عرض الجمل
        TextArea sentencesArea = new TextArea();
        sentencesArea.setEditable(false);
        sentencesArea.setWrapText(true);
        sentencesArea.setPrefHeight(200);

        generateButton.setOnAction(e -> {
            try {
                int count = Integer.parseInt(countField.getText());
                if (count <= 0 || count > 100) {
                    showOutput("أدخل رقماً بين 1 و 100!");
                    return;
                }

                StringBuilder sentences = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    String sentence;
                    if (englishRadio.isSelected()) {
                        sentence = manager.generateEnglishSentence();
                    } else {
                        sentence = manager.generateArabicSentence();
                    }
                    sentences.append(i + 1).append(". ").append(sentence).append("\n");
                }

                sentencesArea.setText(sentences.toString());
                showOutput("تم توليد " + count + " جملة بنجاح!");
            } catch (NumberFormatException ex) {
                showOutput("أدخل رقماً صحيحاً!");
            }
        });

        vbox.getChildren().addAll(
                new Label("اختر لغة الجمل:"),
                languageBox,
                countBox,
                generateButton,
                new Label("الجمل المولدة:"),
                sentencesArea
        );

        tab.setContent(new ScrollPane(vbox));
        return tab;
    }

    private Tab createStatisticsTab() {
        Tab tab = new Tab("الإحصائيات والتقارير");
        tab.setClosable(false);

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        // زر تحديث الإحصائيات
        Button refreshButton = new Button("تحديث الإحصائيات");
        refreshButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        // منطقة عرض الإحصائيات
        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setWrapText(true);
        statsArea.setPrefHeight(300);

        refreshButton.setOnAction(e -> {
            StringBuilder stats = new StringBuilder();

            // العدد الكلي للكلمات
            stats.append("===== الإحصائيات العامة =====\n");
            stats.append("العدد الكلي للكلمات: ").append(manager.getTotalWords()).append("\n\n");

            // عدد الكلمات لكل حرف
            stats.append("===== عدد الكلمات لكل حرف =====\n");
            List<DLinkedList.LetterCount> letterCounts = manager.getWordCountByLetter();
            for (DLinkedList.LetterCount lc : letterCounts) {
                if (lc.getCount() > 0) {
                    stats.append("الحرف ").append(lc.getLetter()).append(": ").append(lc.getCount()).append(" كلمة\n");
                }
            }
            stats.append("\n");

            // عدد الكلمات لكل نوع
            stats.append("===== عدد الكلمات لكل نوع =====\n");
            List<DLinkedList.TypeCount> typeCounts = manager.getWordCountByType();
            for (DLinkedList.TypeCount tc : typeCounts) {
                stats.append(tc.getType()).append(": ").append(tc.getCount()).append(" كلمة\n");
            }
            stats.append("\n");

            // ارتفاع الأشجار
            stats.append("===== ارتفاع أشجار AVL =====\n");
            List<DLinkedList.LetterHeight> treeHeights = manager.getTreeHeights();
            for (DLinkedList.LetterHeight th : treeHeights) {
                stats.append("شجرة الحرف ").append(th.getLetter()).append(": ارتفاع = ").append(th.getHeight()).append("\n");
            }

            statsArea.setText(stats.toString());
            showOutput("تم تحديث الإحصائيات بنجاح!");
        });

        // قسم عرض الكلمات بحرف معين
        VBox letterSection = new VBox(10);
        letterSection.setPadding(new Insets(10));
        letterSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

        TextField letterField = new TextField();
        letterField.setPromptText("أدخل حرفاً واحداً");
        letterField.setPrefWidth(50);

        Button showWordsButton = new Button("عرض الكلمات");
        TextArea wordsArea = new TextArea();
        wordsArea.setEditable(false);
        wordsArea.setWrapText(true);
        wordsArea.setPrefHeight(150);

        showWordsButton.setOnAction(e -> {
            String letterStr = letterField.getText().trim().toUpperCase();
            if (letterStr.isEmpty() || letterStr.length() != 1) {
                showOutput("أدخل حرفاً واحداً فقط!");
                return;
            }

            char letter = letterStr.charAt(0);
            if (letter < 'A' || letter > 'Z') {
                showOutput("أدخل حرفاً إنجليزياً من A إلى Z!");
                return;
            }

            List<Word> words = manager.getWordsByLetter(letter);
            if (words.isEmpty()) {
                wordsArea.setText("لا توجد كلمات تبدأ بالحرف " + letter);
            } else {
                StringBuilder wordsText = new StringBuilder();
                wordsText.append("الكلمات التي تبدأ بالحرف ").append(letter).append(":\n\n");
                for (Word word : words) {
                    wordsText.append("- ").append(word.getWord()).append(": ")
                            .append(word.getEnglishMeaning()).append(" (")
                            .append(word.getArabicMeaning()).append(")\n");
                }
                wordsText.append("\nالعدد: ").append(words.size()).append(" كلمة");
                wordsArea.setText(wordsText.toString());
            }
        });

        letterSection.getChildren().addAll(
                new Label("عرض الكلمات التي تبدأ بحرف معين:"),
                new HBox(10, new Label("الحرف:"), letterField, showWordsButton),
                wordsArea
        );

        vbox.getChildren().addAll(
                refreshButton,
                new Label("الإحصائيات:"),
                statsArea,
                letterSection
        );

        tab.setContent(new ScrollPane(vbox));
        return tab;
    }

    private void loadFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("تحميل القاموس من ملف");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            int count = manager.loadFromFile(file.getAbsolutePath());
            showOutput("تم تحميل " + count + " كلمة من الملف: " + file.getName());
        }
    }

    private void saveToFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("حفظ القاموس إلى ملف");
        fileChooser.setInitialFileName("dictionary.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            int count = manager.saveToFile(file.getAbsolutePath());
            if (count >= 0) {
                showOutput("تم حفظ " + count + " كلمة إلى الملف: " + file.getName());
            }
        }
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("حول النظام");
        alert.setHeaderText("نظام القاموس باستخدام AVL Trees");
        alert.setContentText(
                "مشروع بنية البيانات\n" +
                        "نظام قاموس متكامل باستخدام:\n" +
                        "- Doubly Linked List مع dummy head دائري\n" +
                        "- أشجار AVL متوازنة\n" +
                        "- واجهة رسومية باستخدام JavaFX\n\n" +
                        "جميع الحقوق محفوظة © 2024"
        );
        alert.showAndWait();
    }

    private void showOutput(String message) {
        outputArea.appendText(message + "\n");
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}