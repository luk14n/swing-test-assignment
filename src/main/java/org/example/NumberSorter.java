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
    public static final int MAX_SMALL_NUMBER = 31;
    public static final int MAX_LARGE_NUMBER = 1001;

    private JPanel introPanel;
    private JPanel sortPanel;
    private JTextField numberInput;
    private JPanel numbersPanel;
    private boolean isDescending = false;
    private int[] numbers;
    private boolean isSorted = false;

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
        enterButton.addActionListener(getActionListener());

        // Configured required layout
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

    private ActionListener getActionListener() {
        return event -> {
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
        };
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
        sortButton.addActionListener(event -> startSorting());
        resetButton.addActionListener(event -> switchToIntroPanel());

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
            columnPanels[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            columnPanels[i].setBackground(Color.WHITE);
            columnPanels[i].setPreferredSize(new Dimension(110, getHeight()));
            numbersPanel.add(columnPanels[i]);
        }

        for (int i = 0; i < numbers.length; i++) {
            JButton numButton = createStyledButton(String.valueOf(numbers[i]), BUTTON_BLUE);
            numButton.setPreferredSize(new Dimension(100, 40));

            final int index = i;
            numButton.addActionListener(event -> {
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
        isSorted = false;

        int indexOfNumberLessOrEqualThanThirty = random.nextInt(count);
        for (int i = 0; i < count; i++) {
            if (i == indexOfNumberLessOrEqualThanThirty) {
                numbers[i] = random.nextInt(MAX_SMALL_NUMBER);
            } else {
                numbers[i] = random.nextInt(MAX_LARGE_NUMBER);
            }
        }

        updateNumbersDisplay();
    }

    /*
     * It is only sorting once instead of on every button click
     * It uses the stored sorted array for further button clicks
     */
    private void startSorting() {
        isDescending = !isDescending;

        // Output for tracking sorting order
        String order = isDescending ? "Descending" : "Ascending";
        System.out.println("Displaying in " + order + " order");

        if (!isSorted) {
            quickSort(0, numbers.length - 1);
            isSorted = true;
        } else {
            reverseArray();
        }
        updateNumbersDisplay();
    }

    private void quickSort(int low, int high) {
        if (low >= high) {
            return;
        }

        // Choose random pivot
        int pivotIndex = new Random().nextInt(high - low) + low;
        int pivot = numbers[pivotIndex];
        swap(pivotIndex, high);

        int leftPointer = partition(low, high, pivot);

        quickSort(low, leftPointer - 1);
        quickSort(leftPointer + 1, high);
    }

    private int partition(int lowIndex, int highIndex, int pivot) {
        int leftPointer = lowIndex;
        int rightPointer = highIndex - 1;

        while (leftPointer < rightPointer) {
            // Adjust comparison based on sort direction
            if (isDescending) {
                while (numbers[leftPointer] >= pivot && leftPointer < rightPointer) {
                    leftPointer++;
                }
                while (numbers[rightPointer] <= pivot && leftPointer < rightPointer) {
                    rightPointer--;
                }
            } else {
                while (numbers[leftPointer] <= pivot && leftPointer < rightPointer) {
                    leftPointer++;
                }
                while (numbers[rightPointer] >= pivot && leftPointer < rightPointer) {
                    rightPointer--;
                }
            }
            swap(leftPointer, rightPointer);
        }

        // Fix for last value potentially being out of order
        if (isDescending) {
            if (numbers[leftPointer] < numbers[highIndex]) {
                swap(leftPointer, highIndex);
            } else {
                leftPointer = highIndex;
            }
        } else {
            if (numbers[leftPointer] > numbers[highIndex]) {
                swap(leftPointer, highIndex);
            } else {
                leftPointer = highIndex;
            }
        }

        return leftPointer;
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
        isSorted = false;
        revalidate();
        repaint();
    }

    // Switches back to the intro screen
    private void switchToIntroPanel() {
        getContentPane().removeAll();
        getContentPane().add(introPanel);
        numberInput.setText("");
        isSorted = false; // Sorting logic resets after reset
        revalidate();
        repaint();
    }

    private void reverseArray() {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            swap(left, right);
            left++;
            right--;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NumberSorter::new);
    }
}