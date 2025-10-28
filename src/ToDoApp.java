import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToDoApp {

    // Main Swing components
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> todoList;
    private JTextField inputField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton clearButton;

    public ToDoApp() {
        initComponents();
    }

    private void initComponents() {
        // Frame setup
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null); // center on screen

        // Layout: top input, middle list, bottom buttons
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        inputField = new JTextField();
        inputField.setToolTipText("Type a task and press Add or Enter");
        addButton = new JButton("Add");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // List area
        listModel = new DefaultListModel<>();
        todoList = new JList<>(listModel);
        todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoList.setVisibleRowCount(12);
        JScrollPane listScroll = new JScrollPane(todoList);

        // Buttons area (Delete, Clear All)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        deleteButton = new JButton("Delete Selected");
        clearButton = new JButton("Clear All");
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(listScroll, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        frame.setContentPane(mainPanel);

        // Wire actions
        wireActions();

        // Visible
        frame.setVisible(true);
    }

    private void wireActions() {
        // Add button click: add task
        addButton.addActionListener(e -> addTask());

        // Press Enter in the text field: add task
        inputField.addActionListener(e -> addTask());

        // Delete button: remove selected
        deleteButton.addActionListener(e -> deleteSelectedTask());

        // Clear button: remove all tasks (with confirmation)
        clearButton.addActionListener(e -> {
            if (listModel.getSize() == 0) {
                JOptionPane.showMessageDialog(frame, "No tasks to clear.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(frame, "Clear all tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                listModel.clear();
            }
        });

        // Double-click an item to edit it
        todoList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = todoList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        editTask(index);
                    }
                }
            }
        });

        // Keyboard Delete key removes selected task
        todoList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedTask();
                }
            }
        });
    }

    private void addTask() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a task.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listModel.addElement(text);
        inputField.setText("");
        inputField.requestFocusInWindow();
    }

    private void deleteSelectedTask() {
        int index = todoList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a task to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete selected task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listModel.remove(index);
        }
    }

    private void editTask(int index) {
        String current = listModel.get(index);
        String updated = JOptionPane.showInputDialog(frame, "Edit task:", current);
        if (updated != null) {
            updated = updated.trim();
            if (!updated.isEmpty()) {
                listModel.set(index, updated);
            } else {
                // If user cleared text, ask whether to delete
                int del = JOptionPane.showConfirmDialog(frame, "Empty text — delete this task?", "Empty Task", JOptionPane.YES_NO_OPTION);
                if (del == JOptionPane.YES_OPTION) {
                    listModel.remove(index);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Use the system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Create app in Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(ToDoApp::new);
    }
}
