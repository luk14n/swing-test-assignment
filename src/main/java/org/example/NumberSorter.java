package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * NumberSorter app test assignment
 */
public class NumberSorter extends JFrame {
    // Create colors for further usage
    private static final Color BUTTON_BLUE = new Color(67, 110, 238);
    private static final Color BUTTON_GREEN = new Color(46, 184, 93);
    private static final Color TEXT_WHITE = Color.WHITE;

    private JPanel introPanel;
    private JPanel sortPanel;
    private JTextField numberInput;
    private JPanel numbersPanel;
    private boolean isDescending = false;
    private int[] numbers;
    private Timer sortingTimer;
    private int currentPivotIndex = -1;
    private int sortingStep = 0;

    public NumberSorter() {
        setTitle("Number Sorter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // set window size
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // set background
        getContentPane().setBackground(Color.WHITE);

        createIntroPanel();
        createSortPanel();

        add(introPanel);
        setVisible(true);
    }

    // Intro screen
    private void createIntroPanel() {
        introPanel = new JPanel(new GridBagLayout());
        introPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Question style
        JLabel label = new JLabel("How many numbers to display?");
        label.setFont(new Font("Arial", Font.PLAIN, 24));

        // Input box style
        numberInput = new JTextField(20);
        numberInput.setPreferredSize(new Dimension(150, 40));
        numberInput.setFont(new Font("Arial", Font.PLAIN, 16));

        // Enter button style
        JButton enterButton = createStyledButton("Enter", BUTTON_BLUE);
        enterButton.setPreferredSize(new Dimension(150, 40));

        // Listener to monitor user input
        enterButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(numberInput.getText().trim());
                if (count > 0) {
                    generateNumbers(count);
                    switchToSortPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a positive number");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            }
        });

        // Configured required layaut
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        introPanel.add(label, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        introPanel.add(numberInput, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 20, 20);
        introPanel.add(enterButton, gbc);
    }

    // Sort screen
    private void createSortPanel() {
        sortPanel = new JPanel(new BorderLayout(20, 20));
        sortPanel.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(null);
        GridBagConstraints gbc = new GridBagConstraints();

        // Sort and reset buttons
        JButton sortButton = createStyledButton("Sort", BUTTON_GREEN);
        JButton resetButton = createStyledButton("Reset", BUTTON_GREEN);

        Dimension controlButtonSize = new Dimension(100, 40);
        sortButton.setPreferredSize(controlButtonSize);
        resetButton.setPreferredSize(controlButtonSize);

        // Layout configuration for buttons
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 5, 5, 8); // Add spacing below the Sort button
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(sortButton, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 10, 8); // Add spacing below the Reset button
        buttonPanel.add(resetButton, gbc);

        // Numbers panel
        numbersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        numbersPanel.setBackground(Color.WHITE);

        // Listeners to execute required methods when buttons pressed
        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> switchToIntroPanel());

        numbersPanel.setBorder(null);
        sortPanel.add(buttonPanel, BorderLayout.EAST);
        sortPanel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
    }

    // Button style
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setForeground(TEXT_WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        return button;
    }

    // Updates numbers display with current sorting state
    private void updateNumbersDisplay() {
        numbersPanel.removeAll();
        int columns = (numbers.length + 9) / 10;
        JPanel[] columnPanels = new JPanel[columns];

        for (int i = 0; i < columns; i++) {
            columnPanels[i] = new JPanel(new GridLayout(0, 1, 5, 5));
            columnPanels[i].setBackground(Color.WHITE);
            numbersPanel.add(columnPanels[i]);
        }

        for (int i = 0; i < numbers.length; i++) {
            JButton numButton = createStyledButton(String.valueOf(numbers[i]), BUTTON_BLUE);
            numButton.setPreferredSize(new Dimension(100, 40));

            if (i == currentPivotIndex) {
                numButton.setBackground(Color.YELLOW);
            }

            final int index = i;
            numButton.addActionListener(e -> {
                if (numbers[index] <= 30) {
                    generateNumbers(numbers.length);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Please select a value smaller or equal to 30.");
                }
            });

            columnPanels[i / 10].add(numButton);
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    // Generates random numbers and adds a small number for demonstration
    private void generateNumbers(int count) {
        Random random = new Random();
        numbers = new int[count];
        boolean hasSmallNumber = false;

        int smallNumberIndex = random.nextInt(count);
        for (int i = 0; i < count; i++) {
            if (i == smallNumberIndex) {
                numbers[i] = random.nextInt(31);
                hasSmallNumber = true;
            } else {
                numbers[i] = random.nextInt(1001);
            }
        }

        updateNumbersDisplay();
    }

    private void startSorting() {
        if (sortingTimer != null && sortingTimer.isRunning()) {
            sortingTimer.stop();
        }

        sortingStep = 0;
        currentPivotIndex = -1;
        isDescending = !isDescending;

        // Output for tracking sorting order
        String order = isDescending ? "Descending" : "Ascending";
        System.out.println("Sorting in " + order + " order");

        // Start the sorting process with a timer
        sortingTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!quickSortStep(0, numbers.length - 1)) {
                    sortingTimer.stop();
                    currentPivotIndex = -1;
                    updateNumbersDisplay();
                }
            }
        });
        sortingTimer.start();
    }

    private boolean quickSortStep(int low, int high) {
        if (low < high) {
            if (sortingStep == 0) {
                currentPivotIndex = partition(low, high);
                updateNumbersDisplay();
                sortingStep++;
                return true;
            } else if (sortingStep == 1) {
                sortingStep = 0;
                quickSortStep(low, currentPivotIndex - 1);
                return true;
            } else {
                sortingStep = 0;
                quickSortStep(currentPivotIndex + 1, high);
                return true;
            }
        }
        return false;
    }

    private int partition(int low, int high) {
        int pivot = numbers[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if ((isDescending && numbers[j] >= pivot) ||
                    (!isDescending && numbers[j] <= pivot)) {
                i++;
                swap(i, j);
            }
        }

        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }

    // Switches to the sorting screen
    private void switchToSortPanel() {
        getContentPane().removeAll();
        getContentPane().add(sortPanel);
        revalidate();
        repaint();
    }

    // Switches back to the intro screen
    private void switchToIntroPanel() {
        getContentPane().removeAll();
        getContentPane().add(introPanel);
        numberInput.setText("");
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberSorter());
    }
}