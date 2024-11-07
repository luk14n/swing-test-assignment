package org.example;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The NumberSorter class provides a Swing-based user interface for sorting and manipulating a set of random numbers.
 * Users can specify the number of numbers to generate, and the application will display them in a grid.
 * The user can then sort the numbers in ascending or descending order using a quicksort algorithm.
 * Additionally, users can select a number from the grid to generate a new set of numbers up to that value.
 *
 * @author Muzyka Lukian
 * @version 1.0
 */
public class NumberSorter extends JFrame {
    // Create colors for further usage
    private static final Color BUTTON_BLUE = new Color(67, 110, 238);
    private static final Color BUTTON_GREEN = new Color(46, 184, 93);
    public static final Color BUTTON_RED = Color.RED;
    private static final Color TEXT_WHITE = Color.WHITE;
    public static final int MAX_SMALL_NUMBER = 30;
    public static final int MAX_LARGE_NUMBER = 1000;
    private static final int MAX_COUNT = 1000;
    private static final int MIN_COUNT = 1;
    public static final String ARIAL_FONT = "Arial";
    public static final int MAX_NUMBERS_PER_COLUMN = 10;
    public static final Random random = new Random();
    public static final int SLEEP_TIME = 300;

    private ExecutorService executorService;
    private List<JButton> numButtons = new ArrayList<>();
    private JPanel introPanel;
    private JPanel sortPanel;
    private JTextField numberInput;
    private JPanel numbersPanel;
    private boolean isDescending = false;
    private boolean isInSortingProgress = false;
    private int[] numbers;

    /**
     * Constructs a new NumberSorter frame.
     * Sets the title, default close operation, window size, and background color.
     * Creates the intro and sort panels and adds the intro panel to the frame.
     * Makes the frame visible.
     */
    public NumberSorter() {
        setTitle("Number Sorter");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

    /**
     * Creates the intro panel, which includes a label, a text field, and an "Enter" button.
     * The "Enter" button calls the getActionListener() method when clicked.
     */
    private void createIntroPanel() {
        introPanel = new JPanel(new GridBagLayout());
        introPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Question style
        JLabel label = new JLabel("How many numbers to display?");
        label.setFont(new Font(ARIAL_FONT, Font.PLAIN, 24));

        // Input box style
        numberInput = new JTextField(20);
        numberInput.setPreferredSize(new Dimension(150, 40));
        numberInput.setFont(new Font(ARIAL_FONT, Font.PLAIN, 16));

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

    /**
     * Returns an ActionListener that is used for the "Enter" button in the intro panel.
     * The ActionListener parses the user input, validates the number, and calls the generateNumbers() method if valid.
     * If the input is invalid, it displays an error message.
     *
     * @return the ActionListener for the "Enter" button
     */
    private ActionListener getActionListener() {
        return event -> {
            try {
                int count = Integer.parseInt(numberInput.getText().trim());
                if (MAX_COUNT < count || count < MIN_COUNT) {
                    JOptionPane.showMessageDialog(this, String.format("Please enter a number in range: %d-%d", MIN_COUNT, MAX_COUNT));
                } else {
                    generateNumbers(count);
                    switchToSortPanel();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            }
        };
    }

    /**
     * Creates the sort panel, which includes a button panel with "Sort" and "Reset" buttons,
     * and a numbers panel that displays the generated numbers.
     */
    private void createSortPanel() {
        sortPanel = new JPanel(new BorderLayout(20, 20));
        sortPanel.setBackground(Color.WHITE);
        sortPanel.setBorder(null);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(null);
        GridBagConstraints gbc = new GridBagConstraints();

        // Sort and Reset buttons
        JButton sortButton = createStyledButton("Sort", BUTTON_GREEN);
        JButton resetButton = createStyledButton("Reset", BUTTON_GREEN);

        Dimension controlButtonSize = new Dimension(100, 40);
        sortButton.setPreferredSize(controlButtonSize);
        resetButton.setPreferredSize(controlButtonSize);

        // Buttons layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(sortButton, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        buttonPanel.add(resetButton, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        // Numbers panel
        numbersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        numbersPanel.setBackground(Color.WHITE);

        // Listeners to execute required methods when buttons pressed
        sortButton.addActionListener(event -> startSortingWithExecutor());
        resetButton.addActionListener(event -> switchToIntroPanel());

        numbersPanel.setBorder(null);
        sortPanel.add(buttonPanel, BorderLayout.EAST);
        sortPanel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
    }

    /**
     * Creates a styled button with the given text and background color.
     *
     * @param text            the text to display on the button
     * @param backgroundColor the background color of the button
     * @return the styled button
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setForeground(TEXT_WHITE);
        button.setFont(new Font(ARIAL_FONT, Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBackground(backgroundColor);

        return button;
    }

    /**
     * Updates the numbers display in the sort panel with the current sorting state.
     * It creates a grid of panels, each displaying a number button.
     * The number buttons are styled using the createStyledButton() method.
     */
    private void updateNumbersDisplay() {
        numbersPanel.removeAll();
        numButtons = new ArrayList<>();  // Clear the list before adding new buttons
        int columns = (numbers.length + 9) / MAX_NUMBERS_PER_COLUMN;
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
                if (isInSortingProgress) {
                    JOptionPane.showMessageDialog(this,
                            "Please wait, sorting in progress");
                } else {
                    if (numbers[index] <= MAX_SMALL_NUMBER) {
                        generateNumbers(numbers[index]);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Please select a value smaller or equal to 30.");
                    }
                }
            });
            // Add button to the list
            numButtons.add(numButton);
            columnPanels[i / MAX_NUMBERS_PER_COLUMN].add(numButton);
        }
        numbersPanel.revalidate();
        numbersPanel.repaint();
    }


    /**
     * Generates an array of random numbers, with one number being less than or equal to 30.
     * The number of elements in the array is determined by the provided count.
     * The array is then stored in the numbers field, and the updateNumbersDisplay() method is called to update the display.
     *
     * @param count the number of elements to generate
     */
    private void generateNumbers(int count) {
        numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = random.nextInt(MAX_LARGE_NUMBER) + 1;
        }
        int indexOfNumberLessOrEqualThanThirty = random.nextInt(count);
        numbers[indexOfNumberLessOrEqualThanThirty] = random.nextInt(MAX_SMALL_NUMBER) + 1;
        updateNumbersDisplay();
    }

    /**
     * Starts the sorting process. If the array is not yet sorted, it calls the quickSort() method to sort the array.
     * If the array is already sorted, it calls the reverseArray() method to reverse the order of the elements.
     * The updateNumbersDisplay() method is called to update the display with the new sorting state.
     */
    private void startSortingWithExecutor() {
        if (isInSortingProgress) {
            JOptionPane.showMessageDialog(this,
                    "Please wait, sorting in progress");
        } else {
            isInSortingProgress = true;
            // Submit the sorting task to the executor
            executorService.submit(() -> {
                isDescending = !isDescending;

                // Start the quicksort with periodic updates
                quickSort(0, numbers.length - 1);
                isInSortingProgress = false;
            });
        }
    }

    /**
     * Performs a quicksort algorithm to sort the numbers array in the given range.
     *
     * @param low  the starting index of the range to sort
     * @param high the ending index of the range to sort
     */
    private void quickSort(int low, int high) {
        if (low >= high) {
            return;
        }

        // Choose random pivot
        int pivotIndex = random.nextInt(high - low) + low;
        int pivot = numbers[pivotIndex];
        swap(pivotIndex, high);

        int leftPointer = partition(low, high, pivot);

        sleep();

        // Recurse on the left and right parts
        quickSort(low, leftPointer - 1);
        quickSort(leftPointer + 1, high);
    }

    /**
     * Shuts down the {@code executorService} if it is not already shut down.
     * <p>
     * This method checks if the {@code executorService} is not {@code null} and has not been shut down.
     * If both conditions are met, it calls the {@code shutdown()} method on the executor to initiate
     * an orderly shutdown where previously submitted tasks are executed, but no new tasks will be accepted.
     * </p>
     */
    private void shutdownExecutor() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Partitions the numbers array around a pivot element, based on the sort direction (ascending or descending).
     *
     * @param lowIndex  the starting index of the partition
     * @param highIndex the ending index of the partition
     * @param pivot     the pivot element
     * @return the index of the pivot element after partitioning
     */
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
        boolean sortingCondition = isDescending
                ? numbers[leftPointer] < numbers[highIndex]
                : numbers[leftPointer] > numbers[highIndex];
        if (sortingCondition) {
            swap(leftPointer, highIndex);
        } else {
            leftPointer = highIndex;
        }
        return leftPointer;
    }

    /**
     * Swaps the elements at the given indices in the numbers array.
     *
     * @param i the index of the first element to swap
     * @param j the index of the second element to swap
     */
    private void swap(int i, int j) {
        JButton buttonI = numButtons.get(i);
        JButton buttonJ = numButtons.get(j);

        SwingUtilities.invokeLater(() -> {
            buttonI.setBackground(BUTTON_RED);
            buttonJ.setBackground(BUTTON_RED);
        });

        sleep();

        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;

        SwingUtilities.invokeLater(() -> {
            buttonI.setText(String.valueOf(numbers[i]));
            buttonJ.setText(String.valueOf(numbers[j]));

            buttonI.setBackground(BUTTON_BLUE);
            buttonJ.setBackground(BUTTON_BLUE);
        });

        sleep();
    }

    /**
     * Shuts down the {@code executorService} if it is not already shut down.
     * <p>
     * This method checks if the {@code executorService} is not {@code null} and has not been shut down.
     * If both conditions are met, it calls the {@code shutdown()} method on the executor to initiate
     * an orderly shutdown where previously submitted tasks are executed, but no new tasks will be accepted.
     * </p>
     */
    private static void sleep() {
        try {
            Thread.sleep(NumberSorter.SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Switches the displayed content to the sort panel.
     * It removes all components from the content pane, adds the sort panel, and revalidates and repaints the frame.
     */
    private void switchToSortPanel() {
        getContentPane().removeAll();
        getContentPane().add(sortPanel);
        revalidate();
        repaint();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Switches the displayed content to the intro panel.
     * It removes all components from the content pane, adds the intro panel, clears the number input field,
     * sets the isSorted flag to false, and revalidates and repaints the frame.
     */
    private void switchToIntroPanel() {
        getContentPane().removeAll();
        getContentPane().add(introPanel);
        numberInput.setText("");
        revalidate();
        repaint();
        shutdownExecutor();
        isInSortingProgress = false;
        numbers = new int[0];
        numButtons = Collections.emptyList();
    }

    /**
     * The main method that serves as the entry point for the application.
     * This method schedules the application to run on the Event Dispatch Thread (EDT),
     * using {@link SwingUtilities#invokeLater}. This ensures that the GUI will be created
     * and updated in a thread-safe manner.
     *
     * @param args the command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(NumberSorter::new);
    }
}