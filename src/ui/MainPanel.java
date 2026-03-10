package ui;

import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class MainPanel extends JPanel {

    private Workspace workspace;
    private DefaultListModel<Page> pageListModel;
    private JList<Page> pageList;
    private JPanel contentArea;

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color SIDEBAR_COLOR = new Color(55, 71, 79);
    private static final Color BUTTON_COLOR = new Color(38, 166, 154);
    private static final Color TASK_ACTIVE_COLOR = new Color(255, 255, 255);
    private static final Color TASK_OVERDUE_COLOR = new Color(255, 205, 210);

    private Page currentPage;

    public MainPanel(Workspace workspace) {
        this.workspace = workspace;
        this.setLayout(new BorderLayout());
        this.setBackground(BACKGROUND_COLOR);

        setupSidebar();
        setupContentArea();

        if (!workspace.getPages().isEmpty()) {
            selectPage(workspace.getPages().get(0));
        }
    }

    private void setupSidebar() {
        pageListModel = new DefaultListModel<>();
        pageList = new JList<>(pageListModel);
        pageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pageList.setCellRenderer((list, page, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(page.getName());
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            label.setBackground(isSelected ? BUTTON_COLOR : SIDEBAR_COLOR);
            return label;
        });

        pageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Page selected = pageList.getSelectedValue();
                if (selected != null) selectPage(selected);
            }
        });

        JScrollPane scrollPane = new JScrollPane(pageList);
        scrollPane.setPreferredSize(new Dimension(200, 0));
        scrollPane.setBorder(null);

        JButton newPageButton = new JButton("New Page");
        newPageButton.setBackground(BUTTON_COLOR);
        newPageButton.setForeground(Color.WHITE);
        newPageButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter page name:");
            if (name != null && !name.trim().isEmpty()) {
                Page p = workspace.createPage(name);
                pageListModel.addElement(p);
                pageList.setSelectedValue(p, true);
            }
        });

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.add(scrollPane, BorderLayout.CENTER);
        sidebar.add(newPageButton, BorderLayout.SOUTH);

        this.add(sidebar, BorderLayout.WEST);

        // populate existing pages
        workspace.getPages().forEach(pageListModel::addElement);
    }

    private void setupContentArea() {
        contentArea = new JPanel();
        contentArea.setLayout(new BorderLayout());
        contentArea.setBackground(BACKGROUND_COLOR);

        JLabel welcomeLabel = new JLabel("<html><h1>Welcome!</h1><p>Select a page to start</p></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentArea.add(welcomeLabel, BorderLayout.CENTER);

        this.add(contentArea, BorderLayout.CENTER);
    }

    private void selectPage(Page page) {
        this.currentPage = page;
        workspace.selectPage(page.getId());
        updateContent(page);
    }

    private void updateContent(Page page) {
        contentArea.removeAll();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND_COLOR);

        JLabel pageLabel = new JLabel(page.getName());
        pageLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "New page name:", page.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                page.rename(newName);
                pageList.repaint();
                pageLabel.setText(newName);
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this page?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                workspace.deletePage(page.getId());
                pageListModel.removeElement(page);
                if (!pageListModel.isEmpty()) {
                    selectPage(pageListModel.get(0));
                } else {
                    contentArea.removeAll();
                    contentArea.add(new JLabel("<html><h1>Welcome!</h1><p>Select a page to start</p></html>"), BorderLayout.CENTER);
                    contentArea.revalidate();
                    contentArea.repaint();
                }
            }
        });

        topPanel.add(pageLabel);
        topPanel.add(renameButton);
        topPanel.add(deleteButton);

        contentArea.add(topPanel, BorderLayout.NORTH);

        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(BACKGROUND_COLOR);

        // Sort tasks by date
        page.getTasks().stream()
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(task -> tasksPanel.add(createTaskCard(task, page)));

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBorder(null);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.setBackground(BUTTON_COLOR);
        addTaskButton.setForeground(Color.WHITE);
        addTaskButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Task title:");
            if (title != null && !title.trim().isEmpty()) {
                Task newTask = new Task(title, null, Priority.MEDIUM, Status.NOT_STARTED);
                page.addTask(newTask);
                updateContent(page);
            }
        });

        contentArea.add(addTaskButton, BorderLayout.SOUTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createTaskCard(Task task, Page page) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(task.isOverdue(LocalDate.now()) ? TASK_OVERDUE_COLOR : TASK_ACTIVE_COLOR);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel infoLabel = new JLabel(
                "<html>Due: " + (task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ISO_DATE) : "N/A") +
                        " | Priority: " + task.getPriority() +
                        " | Status: " + task.getStatus() + "</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton deleteButton = new JButton("X");
        deleteButton.addActionListener(e -> {
            page.removeTask(task.getId());
            updateContent(page);
        });

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(infoLabel, BorderLayout.CENTER);
        card.add(deleteButton, BorderLayout.EAST);

        return card;
    }
}