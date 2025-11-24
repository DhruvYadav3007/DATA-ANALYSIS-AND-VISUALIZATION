package com.example; 
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtils;

public class DataVisualizationGUI extends JFrame implements ActionListener {
    private JTable table;
    private JFrame frame;
    private DefaultTableModel model;
    private JButton pieChartBtn, barChartBtn, histogramBtn, ogiveBtn;
    private JButton insertBtn, updateBtn, filterBtn, deleteBtn,PredictBtn,sortButton,downloadJButton,switchFileBtn;
    private Connection connection;
    public String jdbcURL = "jdbc:mysql://localhost:3306/major";
    public String username = "Dhruv";
    public String password = "dhruv";

    public DataVisualizationGUI() {
        boolean login=showLoginDialog();
        if (login==false) {
            System.exit(0); // If login is unsuccessful, exit the application
        }
        if(login==true){
        setTitle("Data Visualization");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(75);
        table.setFont(new Font("Calibri", Font.PLAIN, 20));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 4, 10, 10)); // 3 rows, 4 columns with spacing
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding around the panel
        
        // Create buttons with a modern color palette
        pieChartBtn = createButton("Pie Chart", new Color(255, 140, 0));  // Dark Orange
        barChartBtn = createButton("Bar Chart", new Color(60, 179, 113)); // Medium Sea Green
        histogramBtn = createButton("Histogram", new Color(220, 20, 60)); // Crimson
        ogiveBtn = createButton("Ogive", new Color(30, 144, 255));        // Dodger Blue
        PredictBtn = createButton("Predict Value", new Color(100, 100, 100)); // Light Gray
        
        insertBtn = createButton("Insert", new Color(72, 209, 204));    // Medium Turquoise
        updateBtn = createButton("Update", new Color(186, 85, 211));    // Medium Orchid
        filterBtn = createButton("Filter", new Color(255, 215, 0));     // Gold
        deleteBtn = createButton("Delete", new Color(255, 105, 180));   // Hot Pink
        sortButton = createButton("Sort", new Color(0, 191, 255));      // Deep Sky Blue
        downloadJButton = createButton("Download", new Color(50, 205, 50)); // Lime Green
        
        switchFileBtn = createButton("Switch File", new Color(255, 165, 0)); // Orange
buttonPanel.add(switchFileBtn);

// Step 2: Add ActionListener to handle file switching
switchFileBtn.addActionListener(e -> switchFileAndReloadData());
        // Add buttons to the panel in an organized manner
        buttonPanel.add(sortButton);
        buttonPanel.add(filterBtn);
        buttonPanel.add(downloadJButton);
        buttonPanel.add(pieChartBtn);
        buttonPanel.add(barChartBtn);
        buttonPanel.add(histogramBtn);
        buttonPanel.add(ogiveBtn);
        buttonPanel.add(PredictBtn);
        buttonPanel.add(insertBtn);
        buttonPanel.add(updateBtn);
       
        buttonPanel.add(deleteBtn);
       
       
        
        // Apply a modern background color
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light Gray Background
        
        
        add(buttonPanel, BorderLayout.SOUTH);

        connectToDatabase();
        selectExcelFileAndImport();
        displayData();
    }}

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.addActionListener(this);
        return button;
    }
   
    private boolean showLoginDialog() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        
        // Set fonts for input fields
        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        usernameField.setFont(font);
        passwordField.setFont(font);
        
        // Set border and round corners for text fields
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
    
        // Create the login panel with more elegant layout
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(45, 45, 45));  // Dark background for a sleek look
        
        // Use GridBagConstraints for better control over component placement
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around elements
        
        // Username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);  // White text for contrast
        loginPanel.add(usernameLabel, gbc);
        
        // Username field
        gbc.gridx = 1;
        usernameField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(usernameField, gbc);
    
        // Password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);  // White text for contrast
        loginPanel.add(passwordLabel, gbc);
        
        // Password field
        gbc.gridx = 1;
        passwordField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(passwordField, gbc);
    
        // Login button with a sophisticated design
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(28, 169, 201));  // Soft teal color
        loginButton.setForeground(Color.WHITE);  // White text on the button
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));  // Modern, bold font
        loginButton.setFocusPainted(false);  // Removes the focus border for a cleaner look
        loginButton.setBorder(BorderFactory.createEmptyBorder());  // Remove default border
        
        // Make button slightly rounded
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(28, 169, 201), 2, true));
        
        // Button styling
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final boolean[] isLoginSuccessful = {false};
        // Action listener for login button
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            // Simple validation (replace with secure logic)
            if ("admin".equals(username) && "password".equals(password)) {
                this.dispose();  // Close dialog if login is successful
                isLoginSuccessful[0] = true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Set the layout and add login button to the panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;  // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
    
        // Create the dialog and display it
        JOptionPane optionPane = new JOptionPane(loginPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{loginButton}, null);
        JDialog dialog = optionPane.createDialog(this, "Login");
    
        // Boolean flag to track login success
        
        // Show the login dialog and wait for user input
        dialog.setVisible(true);
    
        // Return the login success status
        return   isLoginSuccessful[0];  // Return true if login is successful
    }
    
    
    public void connectToDatabase() {
        try {
           connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void selectExcelFileAndImport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File excelFile = fileChooser.getSelectedFile();
            storeExcelHeaders(excelFile);
            importExcelToDatabase(excelFile);
        }
    }

    private boolean[] isColumnEmpty;

    private void importExcelToDatabase(File file) {
        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            int columnCount = sheet.getRow(0).getLastCellNum();
    
            isColumnEmpty = new boolean[columnCount];
            Arrays.fill(isColumnEmpty, true);
    
            // Check for empty columns
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int colNum = 0; colNum < columnCount; colNum++) {
                        Cell cell = row.getCell(colNum);
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            isColumnEmpty[colNum] = false;
                        }
                    }
                }
            }
    
            // Insert data excluding empty columns
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    String name = row.getCell(0).getStringCellValue();
                    int category1 = (int) row.getCell(1).getNumericCellValue();
                    Integer category2 = (columnCount > 2 && !isColumnEmpty[2]) ? (int) row.getCell(2).getNumericCellValue() : null;
    
                    StringBuilder sql = new StringBuilder("INSERT INTO Major (CategoryNames, Category1");
                    if (category2 != null) {
                        sql.append(", Category2");
                    }
                    sql.append(") VALUES (?, ?");
                    if (category2 != null) {
                        sql.append(", ?");
                    }
                    sql.append(")");
    
                    try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                        statement.setString(1, name);
                        statement.setInt(2, category1);
                        if (category2 != null) {
                            statement.setInt(3, category2);
                        }
                        statement.executeUpdate();
                    }
                }
            }
            System.out.println("Data inserted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

// Function to switch file and reload table
private void switchFileAndReloadData() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {


        File newFile = fileChooser.getSelectedFile();
        storeExcelHeaders(newFile);
        importExcelToDatabase(newFile);
        displayData();  // Reload table with new data
    }
}

    
    public java.util.List<String> excelHeaders = new java.util.ArrayList<>();

 
    
    private void storeExcelHeaders(File file) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Major");
            System.out.println("Table cleared on exit.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
    
            excelHeaders.clear();
            for (Cell cell : headerRow) {
                excelHeaders.add(cell.getStringCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void displayData() {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Major")) {
    
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            model.setRowCount(0);
            model.setColumnCount(0);
    
            // Set column headers from Excel if available and matching
            if (excelHeaders.size() == columnCount) {
                for (String header : excelHeaders) {
                    model.addColumn(header);
                }
            } else {
                // Fallback to database column names
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnName(i));
                }
            }
    
            // Populate data rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
public void saveTableAsExcel(JTable table) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save as Excel File");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        if (!filePath.endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sorted Data");
            TableModel model = table.getModel();

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
            }

            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = excelRow.createCell(col);
                    cell.setCellValue(model.getValueAt(row, col).toString());
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, "Excel file saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
public void saveChartAsPNG(JFreeChart chart) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Chart as PNG");

    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(".png")) {
            filePath += ".png";  // Append .png extension if not provided
        }

        try {
            ChartUtils.saveChartAsPNG(new File(filePath), chart, 800, 600);
            JOptionPane.showMessageDialog(null, "Chart saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving chart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    @Override
    public void actionPerformed(ActionEvent e) {
if (e.getSource() == pieChartBtn) {
    String userColumn = JOptionPane.showInputDialog("Enter column name for the Pie Chart:");
    int index = excelHeaders.indexOf(userColumn);
    if (index == -1) {
        JOptionPane.showMessageDialog(null, "Invalid column name!");
        return;
    }
    String columnName = "Category" + index;
    DefaultPieDataset dataset = new DefaultPieDataset();
    String url = "jdbc:mysql://localhost:3306/Major";
    String user = "Dhuv";
    String password = "dhruv";

    try (Connection conn = DriverManager.getConnection(url, user, password);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT CategoryNames, " + columnName + " FROM Major")) {

        while (rs.next()) {
            String name = rs.getString("CategoryNames");
            int value = rs.getInt(columnName);
            dataset.setValue(name, value);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    // Create the pie chart
    JFreeChart chart = ChartFactory.createPieChart(
            "Pie Chart Representation", // Chart title
            dataset,                    // Dataset
            true,                        // Include legend
            true,                        // Tooltips
            false);                      // No URLs

    // Set up the label format to show percentage only, without index
    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})",        // Format: Category Name: Value (Percentage)
            NumberFormat.getNumberInstance(), 
            new DecimalFormat("0.00%")));  // Decimal format for percentage

    // Display the chart in a JFrame
    JFrame chartFrame = new JFrame("Pie Chart");
    chartFrame.setSize(800, 600);
    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    chartFrame.add(new ChartPanel(chart));
    chartFrame.setVisible(true);
    JButton saveButton = new JButton("Save as PNG");
saveButton.addActionListener(ev -> saveChartAsPNG(chart));

JPanel buttonPanel = new JPanel();
buttonPanel.add(saveButton);
chartFrame.add(buttonPanel, BorderLayout.SOUTH);

}
else if(e.getSource()==downloadJButton){
    try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Major")) {
    
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            model.setRowCount(0);
            model.setColumnCount(0);
    
            // Set column headers from Excel if available and matching
            if (excelHeaders.size() == columnCount) {
                for (String header : excelHeaders) {
                    model.addColumn(header);
                }
            } else {
                // Fallback to database column names
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnName(i));
                }
            }
    
            // Populate data rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        JTable sortedTable = new JTable(model);
                            sortedTable.setRowHeight(75);
                            sortedTable.setFont(new Font("Calibri", Font.PLAIN, 20));
                
                            JScrollPane scrollPane = new JScrollPane(sortedTable);
                            scrollPane.setPreferredSize(new Dimension(800, 400));
                
                            JButton saveButton = new JButton("Save as Excel");
                            saveButton.addActionListener(ex -> saveTableAsExcel(sortedTable));
                
                            JPanel buttonPanel = new JPanel();
                            buttonPanel.add(saveButton);
                
                            JPanel combinedPanel = new JPanel(new BorderLayout());
                            combinedPanel.add(scrollPane, BorderLayout.CENTER);
                            combinedPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                            JOptionPane.showMessageDialog(null, combinedPanel, "Sorted Data", JOptionPane.PLAIN_MESSAGE);

}
else if (e.getSource() == PredictBtn) {
    // Ask user to select the independent variable column
    String independentColumn = JOptionPane.showInputDialog("Enter the column name for the independent variable:");
    String dependentColumn = JOptionPane.showInputDialog("Enter the column name for the dependent variable:");
    
    if (independentColumn == null || dependentColumn == null || independentColumn.isEmpty() || dependentColumn.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Invalid column names!");
        return;
    }
    
    // Prompt user for input value
    String userInput = JOptionPane.showInputDialog("Enter the value for " + independentColumn + ":");
    
    try {
        // Parse the input value
        double inputValue = Double.parseDouble(userInput);
        
        // Database details
        String url = "jdbc:mysql://localhost:3306/Major";  // Database URL
        String user = "Dhruv";  // MySQL username
        String password = "dhruv";  // MySQL password

        int index = excelHeaders.indexOf(independentColumn);
    if (index == -1) {
        JOptionPane.showMessageDialog(null, "Invalid column name!");
        return;
    }
    String indcol = "Category" + index;
    int index2 = excelHeaders.indexOf(dependentColumn);
    if (index2 == -1) {
        JOptionPane.showMessageDialog(null, "Invalid column name!");
        return;
    }
    String depcol = "Category" + index2;

        String query = "SELECT " + indcol + ", " + depcol + " FROM Major";  // Query to fetch the data
        
        ArrayList<Double> independentValues = new ArrayList<>();
        ArrayList<Double> dependentValues = new ArrayList<>();

        // Fetch data from the database
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Fetch data from the database
            while (rs.next()) {
                double independentValue = rs.getDouble(indcol);
                double dependentValue = rs.getDouble(depcol);
                independentValues.add(independentValue);
                dependentValues.add(dependentValue);
            }

            // Now calculate the regression coefficients using simple linear regression
            int n = independentValues.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

            // Sum up necessary values
            for (int i = 0; i < n; i++) {
                sumX += independentValues.get(i);
                sumY += dependentValues.get(i);
                sumXY += independentValues.get(i) * dependentValues.get(i);
                sumX2 += independentValues.get(i) * independentValues.get(i);
            }

            // Calculate slope (β1) and intercept (β0)
            double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            double intercept = (sumY - slope * sumX) / n;

            // Use the regression model to predict the dependent variable value
            double predictedValue = intercept + (slope * inputValue);

            // Show the predicted value to the user
            JOptionPane.showMessageDialog(this, "Predicted value of " + userInput + " is : " + predictedValue);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.");
        }

    } catch (NumberFormatException ex) {
        // Handle invalid input
        JOptionPane.showMessageDialog(this, "Please enter a valid number.");
    }
}

else if (e.getSource() == histogramBtn) {
    String userColumn = JOptionPane.showInputDialog("Enter column name for the Histogram:");
    int index = excelHeaders.indexOf(userColumn);

    if (index == -1) {
        JOptionPane.showMessageDialog(null, "Invalid column name!");
        return;
    }

    String columnName = "Category" + index;
    String url = "jdbc:mysql://localhost:3306/Major";
    String user = "Dhruv";
    String password = "dhruv";

    // Lists for X-axis labels & their numeric ranges
    TreeMap<Integer, Integer> rangeFrequency = new TreeMap<>(); // Keeps order sorted
    boolean isContinuous = true;
    
    try (Connection conn = DriverManager.getConnection(url, user, password);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT CategoryNames, " + columnName + " FROM Major")) {

        int prevUpperBound = -1; // Track previous range for continuity check

        while (rs.next()) {
            String categoryName = rs.getString("CategoryNames");
            int value = rs.getInt(columnName);

            // **Extract numeric ranges from category name**
            String[] rangeParts = categoryName.split("-");
            if (rangeParts.length == 2) {
                try {
                    int lowerBound = Integer.parseInt(rangeParts[0].trim());
                    int upperBound = Integer.parseInt(rangeParts[1].trim());

                    // **Check continuity**
                    if (prevUpperBound != -1 && lowerBound != prevUpperBound) {
                        isContinuous = false; // Not continuous if gap in ranges
                    }
                    prevUpperBound = upperBound;

                    // Store frequency count for the range
                    rangeFrequency.put(lowerBound, value);
                } catch (NumberFormatException ex) {
                    isContinuous = false;
                    break;
                }
            } else {
                isContinuous = false;
                break;
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    // **If X-axis labels are NOT continuous, show prompt & exit**
    if (!isContinuous) {
        JOptionPane.showMessageDialog(null,
                "Histogram is only for continuous numeric ranges on the X-axis.\nUse Bar Chart instead.",
                "Invalid Data for Histogram", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // **Create dataset for Histogram**
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<Integer, Integer> entry : rangeFrequency.entrySet()) {
        String label = entry.getKey() + "-" + (entry.getKey() + 10); // Reconstruct label
        dataset.addValue(entry.getValue(), "Frequency", label);
    }

    // **Create the Histogram Chart**
    JFreeChart chart = ChartFactory.createBarChart(
            "Histogram Representation",
            "Category Ranges",  // X-axis label
            "Frequency",  // Y-axis label
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false);

    // **Display the histogram**
    JFrame chartFrame = new JFrame("Histogram");
    chartFrame.setSize(800, 600);
    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // **Chart Panel**
    ChartPanel chartPanel = new ChartPanel(chart);
    chartFrame.add(chartPanel, BorderLayout.CENTER);

    // **Save as PNG Button**
    JButton saveButton = new JButton("Save as PNG");
    saveButton.addActionListener(ev -> saveChartAsPNG(chart));

    // **Button Panel**
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveButton);
    
    chartFrame.add(buttonPanel, BorderLayout.SOUTH);
    chartFrame.setVisible(true);
}


else if (e.getSource() == ogiveBtn) {
    String userColumn = JOptionPane.showInputDialog("Enter column name for the Ogive Chart:");
    int index = excelHeaders.indexOf(userColumn);
    
    // Validate the column name
    if (index == -1) {
        JOptionPane.showMessageDialog(null, "Invalid column name!");
        return;
    }

    String columnName = "Category" + index;
    XYSeries series = new XYSeries("Cumulative Frequency");
    String url = "jdbc:mysql://localhost:3306/Major";
    String user = "Dhruv";
    String password = "dhruv";
    
    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<Integer> cumulativeValues = new ArrayList<>();
    
    // Establish connection to the database
    try (Connection conn = DriverManager.getConnection(url, user, password);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT CategoryNames, " + columnName + " FROM Major ORDER BY " + columnName)) {
        
        int cumulative = 0;
        while (rs.next()) {
            String categoryName = rs.getString("CategoryNames");
            int value = rs.getInt(columnName);
            cumulative += value;  // Cumulative frequency
            categoryNames.add(categoryName);
            cumulativeValues.add(cumulative);
            series.add(cumulativeValues.size(), cumulative);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    XYSeriesCollection dataset = new XYSeriesCollection(series);
    
    // Create the Ogive (Cumulative Frequency) chart
    JFreeChart chart = ChartFactory.createXYLineChart(
            "Ogive (Cumulative Frequency Curve)",  // Chart Title
            "Category Names",                      // X-Axis Label
            "Cumulative Frequency",                // Y-Axis Label
            dataset                               // Dataset
    );

    // Annotate each data point on the plot with category names
    XYPlot plot = chart.getXYPlot();
    for (int i = 0; i < categoryNames.size(); i++) {
        XYTextAnnotation annotation = new XYTextAnnotation(categoryNames.get(i), i + 1, cumulativeValues.get(i));
        plot.addAnnotation(annotation);
    }

    // Create a panel for the "Save as PNG" button
    JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save as PNG");
    
    // Add action listener for the save button
    saveButton.addActionListener(ex -> {
        try {
            // Set file path to save chart as PNG
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Chart as PNG");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
            
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                }
                // Save the chart as PNG with 800x600 dimensions
                ChartUtils.saveChartAsPNG(fileToSave, chart, 800, 600);
                JOptionPane.showMessageDialog(null, "Chart saved as PNG: " + fileToSave.getAbsolutePath());
            }
        } catch (IOException exx) {
            JOptionPane.showMessageDialog(null, "Error saving chart: " + exx.getMessage());
        }
    });

    // Add the save button to the panel
    savePanel.add(saveButton);

    // Display the chart and save button in a combined panel
    JPanel combinedPanel = new JPanel(new BorderLayout());
    combinedPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
    combinedPanel.add(savePanel, BorderLayout.SOUTH);

    // Create and show the chart frame
    JFrame chartFrame = new JFrame("Ogive Chart");
    chartFrame.setSize(800, 600);
    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    chartFrame.add(combinedPanel);
    chartFrame.setVisible(true);
}
        else if (e.getSource() == insertBtn) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("ID or NAME:"));
            JTextField pidField = new JTextField();
            panel.add(pidField);
    
            panel.add(new JLabel("Value1:"));
            JTextField value1Field = new JTextField();
            panel.add(value1Field);
            
            panel.add(new JLabel("Value1:"));
            JTextField value2Field = new JTextField();
            panel.add(value2Field);

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Purchase Information", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String pid = pidField.getText();
                int val1 = Integer.parseInt(value1Field.getText());
                int val2 = Integer.parseInt(value2Field.getText());
                String sql = "INSERT INTO Major (CategoryNames, Category1, Category2) VALUES ('" + pid + "', '" + val1 + "', '" + val2 + "')";
                try {
                    connection = DriverManager.getConnection(jdbcURL, username, password);
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Row inserted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    displayData();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } }
        } else if (e.getSource() == updateBtn) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("ID or NAME:"));
            JTextField pidField = new JTextField();
            panel.add(pidField);
    
            panel.add(new JLabel("Value1:"));
            JTextField value1Field = new JTextField();
            panel.add(value1Field);
            
            panel.add(new JLabel("Value1:"));
            JTextField value2Field = new JTextField();
            panel.add(value2Field);

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Purchase Information", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String pid = pidField.getText();
               
                int val1 = Integer.parseInt(value1Field.getText());
                int val2 = Integer.parseInt(value2Field.getText());
                String sql = "UPDATE Major SET  Category1 =" + val1 + " ,Category2="+val2+" WHERE CategoryNames= '" + pid+"'";
                try {
                    connection = DriverManager.getConnection(jdbcURL, username, password);
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Row updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    displayData();
                    
                } catch (SQLException e1) {
              
                    e1.printStackTrace();
                }}
        } else if (e.getSource() == deleteBtn) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("ID or NAME:"));
            JTextField pidField = new JTextField();
            panel.add(pidField);
            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Purchase Information", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String pid = pidField.getText();
               
                
                String sql = "DELETE FROM Major WHERE CategoryNames= '" + pid+"'";
                try {
                    connection = DriverManager.getConnection(jdbcURL, username, password);
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Row deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    displayData();
                    
                } catch (SQLException e1) {
                
                    e1.printStackTrace();
                }
        }}
        else if (e.getSource() == filterBtn) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Product Column (Excel Name)"));
            JTextField categoryField = new JTextField();
            panel.add(categoryField);
        
            panel.add(new JLabel("Minimum Quantity:"));
            JTextField minQtyField = new JTextField();
            panel.add(minQtyField);
        
            panel.add(new JLabel("Maximum Quantity:"));
            JTextField maxQtyField = new JTextField();
            panel.add(maxQtyField);
        
            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Filter Criteria", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String Pcolumn = categoryField.getText().trim();
                int minQty = Integer.parseInt(minQtyField.getText().trim());
                int maxQty = Integer.parseInt(maxQtyField.getText().trim());
        
                int PcolumnIndex = -1;
                for (int i = 0; i < excelHeaders.size(); i++) {
                    if (excelHeaders.get(i).equalsIgnoreCase(Pcolumn)) {
                        PcolumnIndex = i;
                        break;
                    }
                }
        
                if (PcolumnIndex == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid column name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                // Map the Excel column to MySQL column
                String mysqlColumn = "Category" + (PcolumnIndex);
        
                try {
                    String sql = "SELECT * FROM Major WHERE " + mysqlColumn + " BETWEEN ? AND ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setInt(1, minQty);
                    stmt.setInt(2, maxQty);
        
                    ResultSet rs = stmt.executeQuery();
                    DefaultTableModel filteredModel = new DefaultTableModel();
        
                    filteredModel.setColumnCount(0);  // Reset columns
                    for (String header : excelHeaders) {
                        filteredModel.addColumn(header);
                    }
        
                    while (rs.next()) {
                        Object[] row = new Object[filteredModel.getColumnCount()];
                        for (int i = 0; i < filteredModel.getColumnCount(); i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        filteredModel.addRow(row);
                    }
        
                    JTable filteredTable = new JTable(filteredModel);
                    filteredTable.setRowHeight(75);
                    filteredTable.setFont(new Font("Calibri", Font.PLAIN, 20));
        
                    JScrollPane scrollPane = new JScrollPane(filteredTable);
                    scrollPane.setPreferredSize(new Dimension(800, 400));
        
                    // Create Save as Excel button
                    JButton saveButton = new JButton("Save as Excel");
                    saveButton.addActionListener(ex -> saveTableAsExcel(filteredTable));
        
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(saveButton);
        
                    JPanel combinedPanel = new JPanel(new BorderLayout());
                    combinedPanel.add(scrollPane, BorderLayout.CENTER);
                    combinedPanel.add(buttonPanel, BorderLayout.SOUTH);
        
                    // Display the panel in a dialog
                    JOptionPane.showMessageDialog(null, combinedPanel, "Filtered Inventory", JOptionPane.PLAIN_MESSAGE);
        
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error while querying the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
         
        else if (e.getSource() == barChartBtn) {
            // Dataset for Bar Chart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String url = "jdbc:mysql://localhost:3306/Major";
            String user = "root";
            String password = "root";
    
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT CategoryNames, Category1, Category2 FROM Major")) {
    
                while (rs.next()) {
                    String name = rs.getString("CategoryNames");
                    int column1 = rs.getInt("Category1");
                    int column2 = rs.getInt("Category2");
    
                    // Add data for both integer columns
                    dataset.addValue(column1, excelHeaders.get(1), name);
                    dataset.addValue(column2, excelHeaders.get(2), name);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
    
            // Create Bar Chart
            JFreeChart chart = ChartFactory.createBarChart(
                    "Comparison Chart",  // Chart title
                    excelHeaders.get(0),              // X-axis Label
                    "Values",            // Y-axis Label
                    dataset);
    
            // Display the chart in a new JFrame
            JFrame chartFrame = new JFrame("Bar Chart");
            chartFrame.setSize(800, 600);
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(new ChartPanel(chart));
            chartFrame.setVisible(true);

            JButton saveButton = new JButton("Save as PNG");
            saveButton.addActionListener(ev -> saveChartAsPNG(chart));
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(saveButton);
            chartFrame.add(buttonPanel, BorderLayout.SOUTH);
        }
    

      
        else if (e.getSource() == insertBtn) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("ID or NAME:"));
            JTextField pidField = new JTextField();
            panel.add(pidField);
    
            panel.add(new JLabel("Value1:"));
            JTextField value1Field = new JTextField();
            panel.add(value1Field);
            
            panel.add(new JLabel("Value1:"));
            JTextField value2Field = new JTextField();
            panel.add(value2Field);

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Purchase Information", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String pid = pidField.getText();
                int val1 = Integer.parseInt(value1Field.getText());
                int val2 = Integer.parseInt(value2Field.getText());
                String sql = "INSERT INTO Major (CategoryNames, Category1, Category2) VALUES ('" + pid + "', '" + val1 + "', '" + val2 + "')";
                try {
                    connection = DriverManager.getConnection(jdbcURL, username, password);
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Row inserted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    displayData();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } }
        }
        else if (e.getSource() == sortButton) {
            JPanel panel = new JPanel(new GridLayout(0, 2));
            panel.add(new JLabel("Enter Column Name:"));
            JTextField columnField = new JTextField();
            panel.add(columnField);
        
            // Create radio buttons for ASC and DESC
            JRadioButton ascButton = new JRadioButton("ASC");
            JRadioButton descButton = new JRadioButton("DESC");
            
            // Group the radio buttons so only one can be selected
            ButtonGroup orderGroup = new ButtonGroup();
            orderGroup.add(ascButton);
            orderGroup.add(descButton);
        
            // Set default selection to ASC
            ascButton.setSelected(true);
        
            // Add radio buttons to the panel
            panel.add(new JLabel("Sort Order:"));
            panel.add(ascButton);
            panel.add(new JLabel(""));
            panel.add(descButton);
        
            int result = JOptionPane.showConfirmDialog(null, panel, "Sort Data", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String columnName = columnField.getText().trim();
                
                // Determine the selected sort order (ASC or DESC)
                String sortOrder = ascButton.isSelected() ? "ASC" : "DESC";
        
                int columnIndex = -1;
                for (int i = 0; i < excelHeaders.size(); i++) {
                    if (excelHeaders.get(i).equalsIgnoreCase(columnName)) {
                        columnIndex = i;
                        break;
                    }
                }
        
                if (columnIndex == -1) {
                    JOptionPane.showMessageDialog(null, "Invalid Column Name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                String mysqlColumn = "Category" + (columnIndex);
        
                try {
                    String sql = "SELECT * FROM Major ORDER BY " + mysqlColumn + " " + sortOrder;
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();
        
                    DefaultTableModel sortedModel = new DefaultTableModel();
                    for (String header : excelHeaders) {
                        sortedModel.addColumn(header);
                    }
        
                    while (rs.next()) {
                        Object[] row = new Object[sortedModel.getColumnCount()];
                        for (int i = 0; i < sortedModel.getColumnCount(); i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        sortedModel.addRow(row);
                    }
        
                    JTable sortedTable = new JTable(sortedModel);
                    sortedTable.setRowHeight(75);
                    sortedTable.setFont(new Font("Calibri", Font.PLAIN, 20));
        
                    JScrollPane scrollPane = new JScrollPane(sortedTable);
                    scrollPane.setPreferredSize(new Dimension(800, 400));
        
                    JButton saveButton = new JButton("Save as Excel");
                    saveButton.addActionListener(ex -> saveTableAsExcel(sortedTable));
        
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(saveButton);
        
                    JPanel combinedPanel = new JPanel(new BorderLayout());
                    combinedPanel.add(scrollPane, BorderLayout.CENTER);
                    combinedPanel.add(buttonPanel, BorderLayout.SOUTH);
        
                    JOptionPane.showMessageDialog(null, combinedPanel, "Sorted Data", JOptionPane.PLAIN_MESSAGE);
        
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
                 }
    private void clearTableOnExit() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM Major");
            System.out.println("Table cleared on exit.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DataVisualizationGUI gui = new DataVisualizationGUI();
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gui.clearTableOnExit();
            }
        });
        gui.setVisible(true);
    }
} 
