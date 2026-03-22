package ui;

import model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import Storage.WorkspaceStorage;

/**
 * Main UI panel with sophisticated, modern minimal design
 * Features a refined neutral palette with blacks, cool greys, and soft tans
 */

public class MainPanel extends JPanel {

    // Data
    private final Workspace workspace;
    private final TaskSummaryService summaryService;
    private Page currentPage;
    
    // UI Components
    private DefaultListModel<Page> pageListModel;
    private JList<Page> pageList;
    private JPanel contentArea;
    private JPanel sidebarContent;
    
// View states
    private enum ViewMode { PAGE, UPCOMING, OVERDUE, PRIORITY, SEARCH, ARCHIVED }
    private ViewMode currentView = ViewMode.PAGE;
    
    // Search and filter state
    private String searchQuery = "";
    private Status filterStatus = null;
    private Priority filterPriority = null;
    private Tag filterTag = null;
    private JTextField searchField;
    private JPanel sidebar;

// ========== REFINED MODERN NEUTRAL COLOR PALETTE ==========
    // Light theme colors (default)
    private static final Color BACKGROUND_LIGHT = new Color(252, 252, 251);           // Clean off-white
    private static final Color SIDEBAR_BG_LIGHT = new Color(247, 247, 246);           // Light cool grey
    private static final Color SIDEBAR_HOVER_LIGHT = new Color(240, 240, 238);        // Subtle hover
    private static final Color SIDEBAR_SELECTED_LIGHT = new Color(232, 232, 230);     // Selected state
    
    // Dark theme colors
    private static final Color BACKGROUND_DARK = new Color(25, 25, 25);               // Near black
    private static final Color SIDEBAR_BG_DARK = new Color(32, 32, 32);               // Dark grey
    private static final Color SIDEBAR_HOVER_DARK = new Color(45, 45, 45);            // Subtle hover
    private static final Color SIDEBAR_SELECTED_DARK = new Color(55, 55, 55);         // Selected state
    
    // Current theme colors (will be set based on dark mode)
    private Color BACKGROUND = BACKGROUND_LIGHT;
    private Color SIDEBAR_BG = SIDEBAR_BG_LIGHT;
    private Color SIDEBAR_HOVER = SIDEBAR_HOVER_LIGHT;
    private Color SIDEBAR_SELECTED = SIDEBAR_SELECTED_LIGHT;
    
// Borders and dividers - light theme
    private static final Color BORDER_COLOR_LIGHT = new Color(234, 234, 232);
    private static final Color BORDER_DARK_LIGHT = new Color(212, 212, 208);
    
    // Borders and dividers - dark theme  
    private static final Color BORDER_COLOR_DARK = new Color(55, 55, 55);
    private static final Color BORDER_DARK_DARK = new Color(70, 70, 70);
    
    // Current border colors
    private Color BORDER_COLOR = BORDER_COLOR_LIGHT;
    private Color BORDER_DARKER = BORDER_DARK_LIGHT;
    
    // Text colors - light theme
    private static final Color TEXT_PRIMARY_LIGHT = new Color(32, 32, 32);
    private static final Color TEXT_SECONDARY_LIGHT = new Color(96, 96, 96);
    private static final Color TEXT_TERTIARY_LIGHT = new Color(140, 140, 140);
    private static final Color TEXT_MUTED_LIGHT = new Color(175, 175, 175);
    
    // Text colors - dark theme
    private static final Color TEXT_PRIMARY_DARK = new Color(110, 110, 110);
    private static final Color TEXT_SECONDARY_DARK = new Color(180, 180, 180);
    private static final Color TEXT_TERTIARY_DARK = new Color(140, 140, 140);
    private static final Color TEXT_MUTED_DARK = new Color(100, 100, 100);
    
    // Current text colors
    private Color TEXT_PRIMARY = TEXT_PRIMARY_LIGHT;
    private Color TEXT_SECONDARY = TEXT_SECONDARY_LIGHT;
    private Color TEXT_TERTIARY = TEXT_TERTIARY_LIGHT;
    private Color TEXT_MUTED = TEXT_MUTED_LIGHT;
    
    // Accent colors - sophisticated muted taupe/tan
    private static final Color ACCENT = new Color(130, 120, 105);
    private static final Color ACCENT_HOVER = new Color(110, 100, 85);
    private static final Color ACCENT_LIGHT = new Color(242, 240, 236);
    
    // Status colors - muted and sophisticated
    private static final Color SUCCESS = new Color(92, 124, 96);
    private static final Color SUCCESS_LIGHT = new Color(240, 246, 240);
    private static final Color WARNING = new Color(168, 138, 84);
    private static final Color WARNING_LIGHT = new Color(252, 248, 240);
    private static final Color DANGER = new Color(158, 92, 88);
    private static final Color DANGER_LIGHT = new Color(252, 244, 244);
    
    // Card colors - light and dark
    private static final Color CARD_BG_LIGHT = Color.WHITE;
    private static final Color CARD_BG_DARK = new Color(38, 38, 38);
    private static final Color CARD_BORDER_LIGHT = new Color(238, 238, 236);
    private static final Color CARD_BORDER_DARK = new Color(55, 55, 55);
    
    // Current card colors
    private Color CARD_BG = CARD_BG_LIGHT;
    private Color CARD_BORDER = CARD_BORDER_LIGHT;

    // ========== ELEGANT FONTS ==========
    // Using clean typography hierarchy
    private static final Font FONT_LOGO = new Font("Georgia", Font.BOLD, 24);
    private static final Font FONT_TITLE = new Font("Georgia", Font.PLAIN, 28);
    private static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_SUBHEADING = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_TINY = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_MICRO = new Font("SansSerif", Font.PLAIN, 10);

public MainPanel(Workspace workspace) {
        this.workspace = workspace;
        this.summaryService = new TaskSummaryService(workspace);
        
        // Apply dark mode if enabled
        applyTheme(workspace.isDarkMode());
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        setupSidebar();
        setupContentArea();

        // Select first page or show welcome
        if (!workspace.getPages().isEmpty()) {
            selectPage(workspace.getPages().get(0));
        } else {
            showWelcome();
        }
    }
    
    private void applyTheme(boolean darkMode) {
        if (darkMode) {
            BACKGROUND = BACKGROUND_DARK;
            SIDEBAR_BG = SIDEBAR_BG_DARK;
            SIDEBAR_HOVER = SIDEBAR_HOVER_DARK;
            SIDEBAR_SELECTED = SIDEBAR_SELECTED_DARK;
            BORDER_COLOR = BORDER_COLOR_DARK;
            BORDER_DARKER = BORDER_DARK_DARK;
            TEXT_PRIMARY = TEXT_PRIMARY_DARK;
            TEXT_SECONDARY = TEXT_SECONDARY_DARK;
            TEXT_TERTIARY = TEXT_TERTIARY_DARK;
            TEXT_MUTED = TEXT_MUTED_DARK;
            CARD_BG = CARD_BG_DARK;
            CARD_BORDER = CARD_BORDER_DARK;
        } else {
            BACKGROUND = BACKGROUND_LIGHT;
            SIDEBAR_BG = SIDEBAR_BG_LIGHT;
            SIDEBAR_HOVER = SIDEBAR_HOVER_LIGHT;
            SIDEBAR_SELECTED = SIDEBAR_SELECTED_LIGHT;
            BORDER_COLOR = BORDER_COLOR_LIGHT;
            BORDER_DARKER = BORDER_DARK_LIGHT;
            TEXT_PRIMARY = TEXT_PRIMARY_LIGHT;
            TEXT_SECONDARY = TEXT_SECONDARY_LIGHT;
            TEXT_TERTIARY = TEXT_TERTIARY_LIGHT;
            TEXT_MUTED = TEXT_MUTED_LIGHT;
            CARD_BG = CARD_BG_LIGHT;
            CARD_BORDER = CARD_BORDER_LIGHT;
        }
    }
    
    private void toggleDarkMode() {
        workspace.toggleDarkMode();
        applyTheme(workspace.isDarkMode());
        
        // Rebuild UI with new colors
        removeAll();
        setupSidebar();
        setupContentArea();
        
        if (currentView == ViewMode.PAGE && currentPage != null) {
            showPageContent(currentPage);
        } else if (currentView == ViewMode.SEARCH) {
            showSearchResults();
        } else if (currentView == ViewMode.ARCHIVED) {
            showArchivedPages();
        } else if (currentView != ViewMode.PAGE) {
            showSummaryView(currentView);
        } else {
            showWelcome();
        }
        
        revalidate();
        repaint();
    }

// ==================== SIDEBAR ====================

    private void setupSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Logo/Brand header
        JPanel header = createSidebarHeader();
        sidebar.add(header, BorderLayout.NORTH);

        // Scrollable content
        sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(SIDEBAR_BG);
        sidebarContent.setBorder(new EmptyBorder(12, 14, 14, 14));
        
        // Search bar
        JPanel searchPanel = createSearchBar();
        sidebarContent.add(searchPanel);
        sidebarContent.add(Box.createVerticalStrut(16));

        // Task Summaries Section
        addSidebarSection("VIEWS", sidebarContent);
        addSummaryButton("Upcoming", "Tasks due soon", ViewMode.UPCOMING, sidebarContent);
        addSummaryButton("Overdue", "Past due date", ViewMode.OVERDUE, sidebarContent);
        addSummaryButton("Priority", "By importance", ViewMode.PRIORITY, sidebarContent);
        addSummaryButton("Archived", "Hidden pages", ViewMode.ARCHIVED, sidebarContent);

        sidebarContent.add(Box.createVerticalStrut(20));

        // Pages Section
        addSidebarSection("PAGES", sidebarContent);
        
        // Page list
        pageListModel = new DefaultListModel<>();
        workspace.getPages().forEach(pageListModel::addElement);
        
        pageList = new JList<>(pageListModel);
        pageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pageList.setBackground(SIDEBAR_BG);
        pageList.setFixedCellHeight(42);
        pageList.setBorder(null);
        pageList.setCellRenderer(new PageListCellRenderer());
        
        pageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Page selected = pageList.getSelectedValue();
                if (selected != null) {
                    currentView = ViewMode.PAGE;
                    selectPage(selected);
                }
            }
        });

        // Wrap in a panel to control sizing
        JPanel pageListWrapper = new JPanel(new BorderLayout());
        pageListWrapper.setBackground(SIDEBAR_BG);
        pageListWrapper.add(pageList, BorderLayout.NORTH);
        sidebarContent.add(pageListWrapper);

        JScrollPane scrollPane = new JScrollPane(sidebarContent);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(SIDEBAR_BG);
        scrollPane.getViewport().setBackground(SIDEBAR_BG);
        sidebar.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = createSidebarBottom();
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }
    
    private JPanel createSearchBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        
        searchField = new JTextField();
        searchField.setFont(FONT_SMALL);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBackground(CARD_BG);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.setCaretColor(TEXT_PRIMARY);
        
        // Add placeholder text behavior
        searchField.setText("Search...");
        searchField.setForeground(TEXT_MUTED);
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search...");
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = searchField.getText().trim();
                    if (!query.isEmpty() && !query.equals("Search...")) {
                        searchQuery = query;
                        currentView = ViewMode.SEARCH;
                        pageList.clearSelection();
                        showSearchResults();
                    }
                }
                // Keyboard shortcut: Escape clears search
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    searchField.setText("");
                    searchQuery = "";
                    searchField.transferFocus();
                }
            }
        });
        
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSidebarBottom() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(10, 14, 10, 14)
        ));
        
        // New Page button
        JButton newPageBtn = createStyledButton("+ New Page", new Color(52, 52, 52), Color.WHITE);
        newPageBtn.setFont(FONT_BODY);
        newPageBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        newPageBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPageBtn.addActionListener(e -> createNewPage());
        panel.add(newPageBtn);
        
        panel.add(Box.createVerticalStrut(8));
        
        // Bottom row with dark mode toggle and export/import
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottomRow.setBackground(SIDEBAR_BG);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Dark mode toggle
        JButton darkModeBtn = createTextButton(workspace.isDarkMode() ? "Light Mode" : "Dark Mode", TEXT_SECONDARY);
        darkModeBtn.setFont(FONT_TINY);
        darkModeBtn.addActionListener(e -> toggleDarkMode());
        bottomRow.add(darkModeBtn);
        
        // Export button
        JButton exportBtn = createTextButton("Export", TEXT_SECONDARY);
        exportBtn.setFont(FONT_TINY);
        exportBtn.addActionListener(e -> exportWorkspace());
        bottomRow.add(exportBtn);
        
        // Import button
        JButton importBtn = createTextButton("Import", TEXT_SECONDARY);
        importBtn.setFont(FONT_TINY);
        importBtn.addActionListener(e -> importWorkspace());
        bottomRow.add(importBtn);
        
        panel.add(bottomRow);
        
        return panel;
    }

    private JPanel createSidebarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(22, 18, 18, 18)
        ));

        // Logo with icon
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setBackground(SIDEBAR_BG);
        
        // Custom logo icon (elegant K monogram in a circle)
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                
                // Draw elegant circle background - deep charcoal
                g2.setColor(new Color(42, 42, 42));
                g2.fill(new Ellipse2D.Double(0, 0, 34, 34));
                
                // Draw the K letter
                g2.setColor(new Color(255, 255, 255));
                g2.setFont(new Font("Georgia", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String letter = "K";
                int x = (34 - fm.stringWidth(letter)) / 2;
                int y = ((34 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(letter, x, y);
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(34, 34);
            }
        };
        logoIcon.setOpaque(false);
        logoPanel.add(logoIcon);
        
        logoPanel.add(Box.createHorizontalStrut(12));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(SIDEBAR_BG);
        
        JLabel logo = new JLabel("Kairo");
        logo.setFont(FONT_LOGO);
        logo.setForeground(TEXT_PRIMARY);
        textPanel.add(logo);

        JLabel tagline = new JLabel("Task & Scheduling");
        tagline.setFont(FONT_MICRO);
        tagline.setForeground(TEXT_MUTED);
        tagline.setBorder(new EmptyBorder(2, 0, 0, 0));
        textPanel.add(tagline);
        
        logoPanel.add(textPanel);

        header.add(logoPanel, BorderLayout.WEST);
        return header;
    }

    private void addSidebarSection(String title, JPanel parent) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(TEXT_MUTED);
        label.setBorder(new EmptyBorder(12, 10, 8, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        parent.add(label);
    }

    private void addSummaryButton(String title, String subtitle, ViewMode mode, JPanel parent) {
        JPanel btn = new RoundedPanel(8);
        btn.setLayout(new BorderLayout());
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon placeholder
        JLabel icon = new JLabel(getViewIcon(mode));
        icon.setFont(FONT_BODY);
        icon.setForeground(TEXT_TERTIARY);
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_BODY);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_MICRO);
        subtitleLabel.setForeground(TEXT_MUTED);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subtitleLabel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(icon, BorderLayout.WEST);
        leftPanel.add(textPanel, BorderLayout.CENTER);
        
        btn.add(leftPanel, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (currentView != mode) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(currentView == mode ? SIDEBAR_SELECTED : SIDEBAR_BG);
            }
@Override
            public void mouseClicked(MouseEvent e) {
                currentView = mode;
                pageList.clearSelection();
                if (mode == ViewMode.ARCHIVED) {
                    showArchivedPages();
                } else {
                    showSummaryView(mode);
                }
                btn.setBackground(SIDEBAR_SELECTED);
            }
        });

        parent.add(btn);
        parent.add(Box.createVerticalStrut(4));
    }
    
    private String getViewIcon(ViewMode mode) {
        switch (mode) {
            case UPCOMING: return "\u25CB";  // Circle
            case OVERDUE: return "\u25C6";   // Diamond
            case PRIORITY: return "\u2605";  // Star
            case ARCHIVED: return "\u2610";  // Empty checkbox
            default: return "\u25A0";        // Square
        }
    }

    private JPanel createNewPageButton() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JButton newPageBtn = createStyledButton("+ New Page", new Color(52, 52, 52), Color.WHITE);
        newPageBtn.setFont(FONT_BODY);
        newPageBtn.addActionListener(e -> createNewPage());
        
        panel.add(newPageBtn, BorderLayout.CENTER);
        return panel;
    }

    // ==================== CONTENT AREA ====================

    private void setupContentArea() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BACKGROUND);
        add(contentArea, BorderLayout.CENTER);
    }

    private void showWelcome() {
        contentArea.removeAll();
        
        JPanel welcome = new JPanel();
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setBackground(BACKGROUND);
        welcome.setBorder(new EmptyBorder(100, 70, 70, 70));

        JLabel title = new JLabel("Welcome to Kairo");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Your all in one task and scheduling companion");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Decorative line
        JPanel line = new JPanel();
        line.setBackground(new Color(52, 52, 52));
        line.setMaximumSize(new Dimension(50, 2));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Create a new page to begin organizing your tasks and notes.");
        hint.setFont(FONT_BODY);
        hint.setForeground(TEXT_TERTIARY);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcome.add(title);
        welcome.add(Box.createVerticalStrut(8));
        welcome.add(subtitle);
        welcome.add(Box.createVerticalStrut(20));
        welcome.add(line);
        welcome.add(Box.createVerticalStrut(24));
        welcome.add(hint);
        welcome.add(Box.createVerticalStrut(28));

        JButton createBtn = createStyledButton("Create your first page", new Color(52, 52, 52), Color.WHITE);
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.addActionListener(e -> createNewPage());
        welcome.add(createBtn);

        contentArea.add(welcome, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void selectPage(Page page) {
        this.currentPage = page;
        workspace.selectPage(page.getId());
        showPageContent(page);
    }

    private void showPageContent(Page page) {
        contentArea.removeAll();
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND);
        mainContent.setBorder(new EmptyBorder(36, 56, 36, 56));

        // Header with page name and actions
        JPanel header = createPageHeader(page);
        mainContent.add(header, BorderLayout.NORTH);

        // Content sections (Tasks and Notes)
        JPanel sections = new JPanel();
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        sections.setBackground(BACKGROUND);

        // Tasks section
        JPanel tasksSection = createTasksSection(page);
        sections.add(tasksSection);
        
        sections.add(Box.createVerticalStrut(36));

        // Notes section
        JPanel notesSection = createNotesSection(page);
        sections.add(notesSection);

        JScrollPane scrollPane = new JScrollPane(sections);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        
        mainContent.add(scrollPane, BorderLayout.CENTER);
        contentArea.add(mainContent, BorderLayout.CENTER);
        
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createPageHeader(Page page) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));

        // Page title (editable on click)
        JLabel titleLabel = new JLabel(page.getName());
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        titleLabel.setToolTipText("Click to rename");
        
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String newName = showStyledInputDialog("Rename Page", "Enter new name:", page.getName());
                if (newName != null && !newName.trim().isEmpty()) {
                    page.rename(newName.trim());
                    titleLabel.setText(newName.trim());
                    pageList.repaint();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(TEXT_SECONDARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(TEXT_PRIMARY);
            }
        });

// Actions panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BACKGROUND);
        
        // Archive button
        JButton archiveBtn = createTextButton("Archive", TEXT_SECONDARY);
        archiveBtn.addActionListener(e -> {
            workspace.archivePage(page.getId());
            pageListModel.removeElement(page);
            if (!pageListModel.isEmpty()) {
                selectPage(pageListModel.get(0));
            } else {
                showWelcome();
            }
        });
        actions.add(archiveBtn);

        JButton deleteBtn = createTextButton("Delete Page", DANGER);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + page.getName() + "\"?",
                "Delete Page",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                workspace.deletePage(page.getId());
                pageListModel.removeElement(page);
                if (!pageListModel.isEmpty()) {
                    selectPage(pageListModel.get(0));
                } else {
                    showWelcome();
                }
            }
        });
        actions.add(deleteBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

private JPanel createTasksSection(Page page) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BACKGROUND);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section header
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(BACKGROUND);
        sectionHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLabel = new JLabel("Tasks");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(TEXT_PRIMARY);

        JButton addBtn = createTextButton("+ Add Task", new Color(52, 52, 52));
        addBtn.addActionListener(e -> showAddTaskDialog(page));

        sectionHeader.add(titleLabel, BorderLayout.WEST);
        sectionHeader.add(addBtn, BorderLayout.EAST);
        section.add(sectionHeader);
        section.add(Box.createVerticalStrut(8));
        
        // Filter bar
        JPanel filterBar = createFilterBar(page);
        section.add(filterBar);
        section.add(Box.createVerticalStrut(14));

        // Task cards - apply filters
        List<Task> tasks = page.getTasks().stream()
            .filter(this::matchesFilters)
            .sorted(Comparator.comparing(Task::getStatus)
                .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
            
        if (page.getTasks().isEmpty()) {
            JPanel emptyState = createEmptyState("No tasks yet", "Click \"+ Add Task\" to create your first task");
            section.add(emptyState);
        } else if (tasks.isEmpty()) {
            JPanel emptyState = createEmptyState("No tasks match filters", "Try adjusting your filter criteria");
            section.add(emptyState);
        } else {
            for (Task task : tasks) {
                JPanel card = createTaskCard(task, page, true);
                section.add(card);
                section.add(Box.createVerticalStrut(8));
            }
        }

        return section;
    }
    
    private JPanel createFilterBar(Page page) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(BACKGROUND);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(FONT_TINY);
        filterLabel.setForeground(TEXT_MUTED);
        bar.add(filterLabel);
        
        // Status filter
        String[] statusOptions = {"All Status", "Not Started", "In Progress", "Completed", "Cancelled"};
        JComboBox<String> statusFilter = new JComboBox<>(statusOptions);
        statusFilter.setFont(FONT_TINY);
        statusFilter.setBackground(CARD_BG);
        statusFilter.setForeground(TEXT_PRIMARY);
        if (filterStatus != null) {
            statusFilter.setSelectedItem(filterStatus.toString());
        }
        statusFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            if ("All Status".equals(selected)) {
                filterStatus = null;
            } else {
                try {
                    filterStatus = Status.valueOf(selected.toUpperCase().replace(" ", "_"));
                } catch (Exception ex) {
                    filterStatus = null;
                }
            }
            refreshCurrentView();
        });
        bar.add(statusFilter);
        
        // Priority filter
        String[] priorityOptions = {"All Priority", "High", "Medium", "Low"};
        JComboBox<String> priorityFilter = new JComboBox<>(priorityOptions);
        priorityFilter.setFont(FONT_TINY);
        priorityFilter.setBackground(CARD_BG);
        priorityFilter.setForeground(TEXT_PRIMARY);
        if (filterPriority != null) {
            priorityFilter.setSelectedItem(filterPriority.toString());
        }
        priorityFilter.addActionListener(e -> {
            String selected = (String) priorityFilter.getSelectedItem();
            if ("All Priority".equals(selected)) {
                filterPriority = null;
            } else {
                try {
                    filterPriority = Priority.valueOf(selected.toUpperCase());
                } catch (Exception ex) {
                    filterPriority = null;
                }
            }
            refreshCurrentView();
        });
        bar.add(priorityFilter);
        
        // Clear filters button
        if (filterStatus != null || filterPriority != null || filterTag != null) {
            JButton clearBtn = createTextButton("Clear", TEXT_TERTIARY);
            clearBtn.setFont(FONT_TINY);
            clearBtn.addActionListener(e -> {
                filterStatus = null;
                filterPriority = null;
                filterTag = null;
                refreshCurrentView();
            });
            bar.add(clearBtn);
        }
        
        return bar;
    }
    
    private boolean matchesFilters(Task task) {
        if (filterStatus != null && task.getStatus() != filterStatus) {
            return false;
        }
        if (filterPriority != null && task.getPriority() != filterPriority) {
            return false;
        }
        if (filterTag != null && !task.hasTag(filterTag.getName())) {
            return false;
        }
        return true;
    }

private JPanel createTaskCard(Task task, Page page, boolean showActions) {
        LocalDate today = LocalDate.now();
        boolean isOverdue = task.isOverdue(today);
        boolean isCompleted = task.isCompleted();
        boolean isDueSoon = !isCompleted && !isOverdue && task.getDueDate() != null 
            && task.getDueDate().isAfter(today) 
            && task.getDueDate().isBefore(today.plusDays(3));

        JPanel card = new RoundedPanel(8);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        
        // Calculate dynamic height based on tags
        int baseHeight = 80;
        if (!task.getTags().isEmpty()) {
            baseHeight += 24;
        }
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, baseHeight));
        
        if (isCompleted) {
            card.setBackground(SUCCESS_LIGHT);
        } else if (isOverdue) {
            card.setBackground(DANGER_LIGHT);
        } else if (isDueSoon) {
            card.setBackground(WARNING_LIGHT); // Visual reminder for approaching deadlines
        } else {
            card.setBackground(CARD_BG);
        }

        // Left side: checkbox and title
        JPanel leftPanel = new JPanel(new BorderLayout(12, 0));
        leftPanel.setOpaque(false);

        // Custom styled checkbox
        JPanel checkboxPanel = createStyledCheckbox(isCompleted, () -> {
            if (!task.isCompleted()) {
                task.markComplete();
            } else {
                task.setStatus(Status.NOT_STARTED);
            }
            refreshCurrentView();
        });
        leftPanel.add(checkboxPanel, BorderLayout.WEST);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(FONT_BODY);
        titleLabel.setForeground(isCompleted ? TEXT_TERTIARY : TEXT_PRIMARY);
        if (isCompleted) {
            titleLabel.setText("<html><s>" + task.getTitle() + "</s></html>");
        }
        titlePanel.add(titleLabel);

        // Due date and priority info
        StringBuilder infoText = new StringBuilder();
        if (task.getDueDate() != null) {
            String dateStr = task.getDueDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
            if (isOverdue) {
                infoText.append("<font color='#9E5C58'>").append(dateStr).append(" (Overdue)</font>");
            } else if (task.getDueDate().equals(today)) {
                infoText.append("<font color='#A88A54'>").append(dateStr).append(" (Today)</font>");
            } else if (isDueSoon) {
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDueDate());
                infoText.append("<font color='#A88A54'>").append(dateStr)
                    .append(" (Due in ").append(daysUntil).append(" day").append(daysUntil > 1 ? "s" : "").append(")</font>");
            } else {
                infoText.append(dateStr);
            }
        }
        if (task.getPriority() != null) {
            if (infoText.length() > 0) infoText.append("  <font color='#AFAFAF'>\u2022</font>  ");
            Color priorityColor = getPriorityColor(task.getPriority());
            infoText.append("<font color='").append(String.format("#%02x%02x%02x", 
                priorityColor.getRed(), priorityColor.getGreen(), priorityColor.getBlue()))
                .append("'>").append(task.getPriority()).append(" priority</font>");
        }
        
        if (infoText.length() > 0) {
            JLabel infoLabel = new JLabel("<html>" + infoText + "</html>");
            infoLabel.setFont(FONT_SMALL);
            infoLabel.setForeground(TEXT_SECONDARY);
            titlePanel.add(Box.createVerticalStrut(4));
            titlePanel.add(infoLabel);
        }
        
        // Display tags
        if (!task.getTags().isEmpty()) {
            JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            tagsPanel.setOpaque(false);
            for (Tag tag : task.getTags()) {
                JLabel tagLabel = createTagLabel(tag);
                tagsPanel.add(tagLabel);
            }
            titlePanel.add(Box.createVerticalStrut(4));
            titlePanel.add(tagsPanel);
        }

        leftPanel.add(titlePanel, BorderLayout.CENTER);
        card.add(leftPanel, BorderLayout.CENTER);

        // Right side: status badge and actions
        if (showActions) {
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            rightPanel.setOpaque(false);

            // Status badge
            JLabel statusBadge = createStatusBadge(task.getStatus());
            rightPanel.add(statusBadge);
            
            // Tags button
            JButton tagsBtn = createIconButton("Tags");
            tagsBtn.addActionListener(e -> showTagsDialog(task, page));
            rightPanel.add(tagsBtn);

            // Edit button
            JButton editBtn = createIconButton("Edit");
            editBtn.addActionListener(e -> showEditTaskDialog(task, page));
            rightPanel.add(editBtn);

            // Delete button
            JButton deleteBtn = createIconButton("Delete");
            deleteBtn.setForeground(DANGER);
            deleteBtn.addActionListener(e -> {
                page.removeTask(task.getId());
                refreshCurrentView();
            });
            rightPanel.add(deleteBtn);

            card.add(rightPanel, BorderLayout.EAST);
        }

        return card;
    }
    
    private JLabel createTagLabel(Tag tag) {
        JLabel label = new JLabel(tag.toString());
        label.setFont(FONT_TINY);
        label.setForeground(tag.getColor());
        label.setOpaque(true);
        label.setBackground(new Color(
            tag.getColor().getRed(), 
            tag.getColor().getGreen(), 
            tag.getColor().getBlue(), 
            30
        ));
        label.setBorder(new EmptyBorder(2, 6, 2, 6));
        return label;
    }
    
    private void showTagsDialog(Task task, Page page) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(350, 300));
        
        // Current tags
        JLabel currentLabel = new JLabel("Current Tags:");
        currentLabel.setFont(FONT_SMALL);
        currentLabel.setForeground(TEXT_SECONDARY);
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(currentLabel);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel currentTagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        currentTagsPanel.setOpaque(false);
        currentTagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentTagsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        if (task.getTags().isEmpty()) {
            JLabel noTags = new JLabel("No tags yet");
            noTags.setFont(FONT_TINY);
            noTags.setForeground(TEXT_MUTED);
            currentTagsPanel.add(noTags);
        } else {
            for (Tag tag : task.getTags()) {
                JPanel tagChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                tagChip.setOpaque(true);
                tagChip.setBackground(new Color(tag.getColor().getRed(), tag.getColor().getGreen(), tag.getColor().getBlue(), 30));
                
                JLabel tagLabel = new JLabel(tag.toString());
                tagLabel.setFont(FONT_TINY);
                tagLabel.setForeground(tag.getColor());
                tagChip.add(tagLabel);
                
                JButton removeBtn = new JButton("x");
                removeBtn.setFont(FONT_MICRO);
                removeBtn.setForeground(tag.getColor());
                removeBtn.setBorder(null);
                removeBtn.setContentAreaFilled(false);
                removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(e -> {
                    task.removeTag(tag);
                    refreshCurrentView();
                    // Close and reopen dialog to refresh
                    SwingUtilities.getWindowAncestor(panel).dispose();
                    showTagsDialog(task, page);
                });
                tagChip.add(removeBtn);
                
                currentTagsPanel.add(tagChip);
            }
        }
        panel.add(currentTagsPanel);
        panel.add(Box.createVerticalStrut(16));
        
        // Add new tag
        JLabel addLabel = new JLabel("Add Tag:");
        addLabel.setFont(FONT_SMALL);
        addLabel.setForeground(TEXT_SECONDARY);
        addLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(addLabel);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel addPanel = new JPanel(new BorderLayout(8, 0));
        addPanel.setOpaque(false);
        addPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        
        JTextField tagField = createStyledTextField("");
        tagField.setToolTipText("Enter tag name");
        addPanel.add(tagField, BorderLayout.CENTER);
        
        JButton addBtn = createStyledButton("Add", new Color(52, 52, 52), Color.WHITE);
        addBtn.setFont(FONT_SMALL);
        addBtn.addActionListener(e -> {
            String tagName = tagField.getText().trim();
            if (!tagName.isEmpty()) {
                Tag tag = workspace.createTag(tagName);
                task.addTag(tag);
                refreshCurrentView();
                tagField.setText("");
                // Close and reopen dialog to refresh
                SwingUtilities.getWindowAncestor(panel).dispose();
                showTagsDialog(task, page);
            }
        });
        addPanel.add(addBtn, BorderLayout.EAST);
        
        panel.add(addPanel);
        panel.add(Box.createVerticalStrut(16));
        
        // Existing workspace tags
        JLabel existingLabel = new JLabel("Workspace Tags:");
        existingLabel.setFont(FONT_SMALL);
        existingLabel.setForeground(TEXT_SECONDARY);
        existingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(existingLabel);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel existingTagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        existingTagsPanel.setOpaque(false);
        existingTagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (workspace.getTags().isEmpty()) {
            JLabel noTags = new JLabel("No workspace tags yet");
            noTags.setFont(FONT_TINY);
            noTags.setForeground(TEXT_MUTED);
            existingTagsPanel.add(noTags);
        } else {
            for (Tag tag : workspace.getTags()) {
                if (!task.hasTag(tag.getName())) {
                    JButton tagBtn = new JButton(tag.toString());
                    tagBtn.setFont(FONT_TINY);
                    tagBtn.setForeground(tag.getColor());
                    tagBtn.setBackground(Color.WHITE);
                    tagBtn.setBorder(new CompoundBorder(
                        new LineBorder(tag.getColor(), 1, true),
                        new EmptyBorder(2, 8, 2, 8)
                    ));
                    tagBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    tagBtn.addActionListener(e -> {
                        task.addTag(tag);
                        refreshCurrentView();
                        SwingUtilities.getWindowAncestor(panel).dispose();
                        showTagsDialog(task, page);
                    });
                    existingTagsPanel.add(tagBtn);
                }
            }
        }
        panel.add(existingTagsPanel);
        
        JOptionPane.showMessageDialog(this, panel, "Manage Tags - " + task.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }
    
    private JPanel createStyledCheckbox(boolean selected, Runnable onClick) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Draw circle
                if (selected) {
                    g2.setColor(SUCCESS);
                    g2.fill(new Ellipse2D.Double(x, y, size, size));
                    // Draw checkmark
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 5, y + 10, x + 8, y + 14);
                    g2.drawLine(x + 8, y + 14, x + 15, y + 6);
} else {
                    g2.setColor(BORDER_DARKER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new Ellipse2D.Double(x + 0.5, y + 0.5, size - 1, size - 1));
                }
                
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(24, 24);
            }
        };
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return panel;
    }

    private JPanel createNotesSection(Page page) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(BACKGROUND);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section header
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(BACKGROUND);
        sectionHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLabel = new JLabel("Notes");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(TEXT_PRIMARY);

        JButton addBtn = createTextButton("+ Add Note", new Color(52, 52, 52));
        addBtn.addActionListener(e -> showAddNoteDialog(page));

        sectionHeader.add(titleLabel, BorderLayout.WEST);
        sectionHeader.add(addBtn, BorderLayout.EAST);
        section.add(sectionHeader);
        section.add(Box.createVerticalStrut(14));

        // Note cards
        List<Note> notes = page.getNotes();
        if (notes.isEmpty()) {
            JPanel emptyState = createEmptyState("No notes yet", "Click \"+ Add Note\" to jot down your thoughts");
            section.add(emptyState);
        } else {
            for (Note note : notes) {
                JPanel card = createNoteCard(note, page);
                section.add(card);
                section.add(Box.createVerticalStrut(8));
            }
        }

        return section;
    }

    private JPanel createNoteCard(Note note, Page page) {
        JPanel card = new RoundedPanel(8);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Note content
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setFont(FONT_BODY);
        contentArea.setForeground(TEXT_PRIMARY);
        contentArea.setBackground(CARD_BG);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setBorder(null);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(contentArea, BorderLayout.CENTER);

        // Created date - Convert Instant to LocalDate for formatting
        String dateStr = DateTimeFormatter.ofPattern("MMM d, yyyy")
            .format(note.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate());
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(FONT_TINY);
        dateLabel.setForeground(TEXT_MUTED);
        dateLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        leftPanel.add(dateLabel, BorderLayout.SOUTH);

        card.add(leftPanel, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton editBtn = createIconButton("Edit");
        editBtn.addActionListener(e -> showEditNoteDialog(note, page));
        actions.add(editBtn);

        JButton deleteBtn = createIconButton("Delete");
        deleteBtn.setForeground(DANGER);
        deleteBtn.addActionListener(e -> {
            page.removeNote(note.getId());
            refreshCurrentView();
        });
        actions.add(deleteBtn);

        card.add(actions, BorderLayout.EAST);

        return card;
    }
    
    private JPanel createEmptyState(String title, String subtitle) {
        JPanel panel = new RoundedPanel(8);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 249));
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(28, 28, 28, 28)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_BODY);
        titleLabel.setForeground(TEXT_TERTIARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_SMALL);
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }

    // ==================== SUMMARY VIEWS ====================

    private void showSummaryView(ViewMode mode) {
        contentArea.removeAll();
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND);
        mainContent.setBorder(new EmptyBorder(36, 56, 36, 56));

        String title;
        String subtitle;
        List<Task> tasks;
        LocalDate today = LocalDate.now();

        switch (mode) {
            case UPCOMING:
                title = "Upcoming Tasks";
                subtitle = "Tasks with future due dates";
                tasks = summaryService.getUpcoming(today);
                break;
            case OVERDUE:
                title = "Overdue Tasks";
                subtitle = "Tasks past their due date";
                tasks = summaryService.getOverdue(today);
                break;
            case PRIORITY:
                title = "Tasks by Priority";
                subtitle = "Organized by importance level";
                tasks = summaryService.getPriority();
                break;
            default:
                return;
        }

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_BODY);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitleLabel);

        mainContent.add(header, BorderLayout.NORTH);

        // Task list
        JPanel taskList = new JPanel();
        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        taskList.setBackground(BACKGROUND);

        if (tasks.isEmpty()) {
            JPanel emptyState = createEmptyState("No tasks to display", "All caught up!");
            emptyState.setBorder(new EmptyBorder(36, 0, 0, 0));
            taskList.add(emptyState);
        } else {
            for (Task task : tasks) {
                // Find the page this task belongs to
                Page taskPage = findPageForTask(task);
                JPanel card = createTaskCard(task, taskPage, true);
                
                // Add page name indicator
                if (taskPage != null) {
                    JLabel pageLabel = new JLabel("in " + taskPage.getName());
                    pageLabel.setFont(FONT_TINY);
                    pageLabel.setForeground(TEXT_MUTED);
                    pageLabel.setBorder(new EmptyBorder(0, 48, 6, 0));
                    taskList.add(pageLabel);
                }
                
                taskList.add(card);
                taskList.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);

        mainContent.add(scrollPane, BorderLayout.CENTER);
        contentArea.add(mainContent, BorderLayout.CENTER);
        
        contentArea.revalidate();
        contentArea.repaint();
    }

private Page findPageForTask(Task task) {
        for (Page page : workspace.getPages()) {
            for (Task t : page.getTasks()) {
                if (t.getId().equals(task.getId())) {
                    return page;
                }
            }
        }
        // Also check archived pages
        for (Page page : workspace.getArchivedPages()) {
            for (Task t : page.getTasks()) {
                if (t.getId().equals(task.getId())) {
                    return page;
                }
            }
        }
        return null;
    }
    
    // ==================== SEARCH VIEW ====================
    
    private void showSearchResults() {
        contentArea.removeAll();
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND);
        mainContent.setBorder(new EmptyBorder(36, 56, 36, 56));
        
        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));
        
        JLabel titleLabel = new JLabel("Search Results");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Results for: \"" + searchQuery + "\"");
        subtitleLabel.setFont(FONT_BODY);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitleLabel);
        
        mainContent.add(header, BorderLayout.NORTH);
        
        // Search results
        JPanel resultsList = new JPanel();
        resultsList.setLayout(new BoxLayout(resultsList, BoxLayout.Y_AXIS));
        resultsList.setBackground(BACKGROUND);
        
        String query = searchQuery.toLowerCase();
        boolean hasResults = false;
        
        // Search in pages
        for (Page page : workspace.getPages()) {
            // Check page name
            if (page.getName().toLowerCase().contains(query)) {
                JPanel pageResult = createSearchResultItem("Page", page.getName(), 
                    () -> { currentView = ViewMode.PAGE; selectPage(page); pageList.setSelectedValue(page, true); });
                resultsList.add(pageResult);
                resultsList.add(Box.createVerticalStrut(8));
                hasResults = true;
            }
            
            // Check tasks
            for (Task task : page.getTasks()) {
                if (task.getTitle().toLowerCase().contains(query)) {
                    JPanel taskResult = createSearchResultItem("Task in " + page.getName(), task.getTitle(),
                        () -> { currentView = ViewMode.PAGE; selectPage(page); pageList.setSelectedValue(page, true); });
                    resultsList.add(taskResult);
                    resultsList.add(Box.createVerticalStrut(8));
                    hasResults = true;
                }
                // Check task tags
                for (Tag tag : task.getTags()) {
                    if (tag.getName().toLowerCase().contains(query)) {
                        JPanel tagResult = createSearchResultItem("Tag on task: " + task.getTitle(), "#" + tag.getName(),
                            () -> { currentView = ViewMode.PAGE; selectPage(page); pageList.setSelectedValue(page, true); });
                        resultsList.add(tagResult);
                        resultsList.add(Box.createVerticalStrut(8));
                        hasResults = true;
                    }
                }
            }
            
            // Check notes
            for (Note note : page.getNotes()) {
                if (note.getContent().toLowerCase().contains(query)) {
                    JPanel noteResult = createSearchResultItem("Note in " + page.getName(), note.getPreview(50),
                        () -> { currentView = ViewMode.PAGE; selectPage(page); pageList.setSelectedValue(page, true); });
                    resultsList.add(noteResult);
                    resultsList.add(Box.createVerticalStrut(8));
                    hasResults = true;
                }
            }
        }
        
        if (!hasResults) {
            JPanel emptyState = createEmptyState("No results found", "Try a different search term");
            emptyState.setBorder(new EmptyBorder(36, 0, 0, 0));
            resultsList.add(emptyState);
        }
        
        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        
        mainContent.add(scrollPane, BorderLayout.CENTER);
        contentArea.add(mainContent, BorderLayout.CENTER);
        
        contentArea.revalidate();
        contentArea.repaint();
    }
    
    private JPanel createSearchResultItem(String type, String text, Runnable onClick) {
        JPanel card = new RoundedPanel(8);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(FONT_TINY);
        typeLabel.setForeground(TEXT_MUTED);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(FONT_BODY);
        textLabel.setForeground(TEXT_PRIMARY);
        
        textPanel.add(typeLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(textLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        JLabel arrowLabel = new JLabel("\u2192");
        arrowLabel.setFont(FONT_BODY);
        arrowLabel.setForeground(TEXT_MUTED);
        card.add(arrowLabel, BorderLayout.EAST);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        
        return card;
    }
    
    // ==================== ARCHIVED PAGES VIEW ====================
    
    private void showArchivedPages() {
        contentArea.removeAll();
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND);
        mainContent.setBorder(new EmptyBorder(36, 56, 36, 56));
        
        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 28, 0));
        
        JLabel titleLabel = new JLabel("Archived Pages");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Pages you've hidden from the main view");
        subtitleLabel.setFont(FONT_BODY);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitleLabel);
        
        mainContent.add(header, BorderLayout.NORTH);
        
        // Archived pages list
        JPanel pagesList = new JPanel();
        pagesList.setLayout(new BoxLayout(pagesList, BoxLayout.Y_AXIS));
        pagesList.setBackground(BACKGROUND);
        
        List<Page> archived = workspace.getArchivedPages();
        
        if (archived.isEmpty()) {
            JPanel emptyState = createEmptyState("No archived pages", "Archive pages you no longer need");
            emptyState.setBorder(new EmptyBorder(36, 0, 0, 0));
            pagesList.add(emptyState);
        } else {
            for (Page page : archived) {
                JPanel card = createArchivedPageCard(page);
                pagesList.add(card);
                pagesList.add(Box.createVerticalStrut(8));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(pagesList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        
        mainContent.add(scrollPane, BorderLayout.CENTER);
        contentArea.add(mainContent, BorderLayout.CENTER);
        
        contentArea.revalidate();
        contentArea.repaint();
    }
    
    private JPanel createArchivedPageCard(Page page) {
        JPanel card = new RoundedPanel(8);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(page.getName());
        nameLabel.setFont(FONT_BODY);
        nameLabel.setForeground(TEXT_PRIMARY);
        
        int taskCount = page.getTasks().size();
        int noteCount = page.getNotes().size();
        JLabel infoLabel = new JLabel(taskCount + " tasks, " + noteCount + " notes");
        infoLabel.setFont(FONT_TINY);
        infoLabel.setForeground(TEXT_MUTED);
        
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(infoLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        
        JButton restoreBtn = createTextButton("Restore", SUCCESS);
        restoreBtn.setFont(FONT_SMALL);
        restoreBtn.addActionListener(e -> {
            workspace.restorePage(page.getId());
            pageListModel.addElement(page);
            showArchivedPages();
        });
        actions.add(restoreBtn);
        
        JButton deleteBtn = createTextButton("Delete", DANGER);
        deleteBtn.setFont(FONT_SMALL);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete \"" + page.getName() + "\"? This cannot be undone.",
                "Delete Archived Page", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                workspace.deleteArchivedPage(page.getId());
                showArchivedPages();
            }
        });
        actions.add(deleteBtn);
        
        card.add(actions, BorderLayout.EAST);
        
        return card;
    }
    
    // ==================== EXPORT/IMPORT ====================
    
    private void exportWorkspace() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Workspace");
        chooser.setSelectedFile(new File("kairo-backup.json"));
        chooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            
            try {
                WorkspaceStorage storage = new WorkspaceStorage();
                String json = storage.toJson(workspace);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(json);
                }
                JOptionPane.showMessageDialog(this, "Workspace exported successfully!",
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to export workspace: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importWorkspace() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Importing will replace your current workspace. Continue?",
            "Import Workspace", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Workspace");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            
            try {
                String json = Files.readString(file.toPath());
                WorkspaceStorage storage = new WorkspaceStorage();
                Workspace imported = storage.fromJson(json);
                
                // Clear current workspace and copy data
                // Note: Since workspace is final, we copy the data manually
                while (!workspace.getPages().isEmpty()) {
                    workspace.deletePage(workspace.getPages().get(0).getId());
                }
                
                for (Page page : imported.getPages()) {
                    Page newPage = workspace.createPage(page.getName());
                    for (Task task : page.getTasks()) {
                        newPage.addTask(task);
                    }
                    for (Note note : page.getNotes()) {
                        newPage.addNote(note);
                    }
                }
                
                // Update UI
                pageListModel.clear();
                workspace.getPages().forEach(pageListModel::addElement);
                
                if (!workspace.getPages().isEmpty()) {
                    selectPage(workspace.getPages().get(0));
                } else {
                    showWelcome();
                }
                
                JOptionPane.showMessageDialog(this, "Workspace imported successfully!",
                    "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to import workspace: " + e.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== DIALOGS ====================

    private void createNewPage() {
        String name = showStyledInputDialog("New Page", "Enter page name:", "");
        if (name != null && !name.trim().isEmpty()) {
            Page p = workspace.createPage(name.trim());
            pageListModel.addElement(p);
            pageList.setSelectedValue(p, true);
            currentView = ViewMode.PAGE;
            selectPage(p);
        }
    }

    private void showAddTaskDialog(Page page) {
        JPanel panel = createStyledDialogPanel();

        // Title field
        JTextField titleField = createStyledTextField("");
        addStyledFormField(panel, "Title", titleField, true);

        // Due date with calendar picker
        JPanel datePanel = new JPanel(new BorderLayout(8, 0));
        datePanel.setOpaque(false);
        JTextField dueDateField = createStyledTextField("");
        dueDateField.setEditable(false);
        dueDateField.setToolTipText("Click calendar to select date");
        
        JButton calendarBtn = createCalendarButton(dueDateField);
        datePanel.add(dueDateField, BorderLayout.CENTER);
        datePanel.add(calendarBtn, BorderLayout.EAST);
        addStyledFormField(panel, "Due Date", datePanel, false);

        // Priority dropdown
        JComboBox<Priority> priorityCombo = createStyledComboBox(Priority.values());
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        addStyledFormField(panel, "Priority", priorityCombo, false);

        // Status dropdown
        JComboBox<Status> statusCombo = createStyledComboBox(Status.values());
        statusCombo.setSelectedItem(Status.NOT_STARTED);
        addStyledFormField(panel, "Status", statusCombo, false);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Task", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task title is required.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dueDate = null;
            String dueDateStr = dueDateField.getText().trim();
            if (!dueDateStr.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    // Date parsing failed, leave as null
                }
            }

            Task newTask = new Task(title, dueDate, 
                (Priority) priorityCombo.getSelectedItem(),
                (Status) statusCombo.getSelectedItem());
            page.addTask(newTask);
            refreshCurrentView();
        }
    }

    private void showEditTaskDialog(Task task, Page page) {
        JPanel panel = createStyledDialogPanel();

        // Title field
        JTextField titleField = createStyledTextField(task.getTitle());
        addStyledFormField(panel, "Title", titleField, true);

        // Due date with calendar picker
        JPanel datePanel = new JPanel(new BorderLayout(8, 0));
        datePanel.setOpaque(false);
        JTextField dueDateField = createStyledTextField(
            task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        dueDateField.setEditable(false);
        
        JButton calendarBtn = createCalendarButton(dueDateField);
        JButton clearBtn = createTextButton("Clear", TEXT_TERTIARY);
        clearBtn.setFont(FONT_TINY);
        clearBtn.addActionListener(e -> dueDateField.setText(""));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(clearBtn);
        btnPanel.add(calendarBtn);
        
        datePanel.add(dueDateField, BorderLayout.CENTER);
        datePanel.add(btnPanel, BorderLayout.EAST);
        addStyledFormField(panel, "Due Date", datePanel, false);

        // Priority dropdown
        JComboBox<Priority> priorityCombo = createStyledComboBox(Priority.values());
        priorityCombo.setSelectedItem(task.getPriority());
        addStyledFormField(panel, "Priority", priorityCombo, false);

        // Status dropdown
        JComboBox<Status> statusCombo = createStyledComboBox(Status.values());
        statusCombo.setSelectedItem(task.getStatus());
        addStyledFormField(panel, "Status", statusCombo, false);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Task", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task title is required.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dueDate = null;
            String dueDateStr = dueDateField.getText().trim();
            if (!dueDateStr.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    // Date parsing failed, leave as null
                }
            }

            task.setTitle(title);
            task.setDueDate(dueDate);
            task.setPriority((Priority) priorityCombo.getSelectedItem());
            task.setStatus((Status) statusCombo.getSelectedItem());
            refreshCurrentView();
        }
    }

    private void showAddNoteDialog(Page page) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(420, 180));
        
        JLabel label = new JLabel("Enter your note:");
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, BorderLayout.NORTH);
        
        JTextArea textArea = createStyledTextArea("");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Note", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String content = textArea.getText().trim();
            if (!content.isEmpty()) {
                Note newNote = new Note(content);
                page.addNote(newNote);
                refreshCurrentView();
            }
        }
    }

    private void showEditNoteDialog(Note note, Page page) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(420, 180));
        
        JLabel label = new JLabel("Edit your note:");
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, BorderLayout.NORTH);
        
        JTextArea textArea = createStyledTextArea(note.getContent());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Note", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String content = textArea.getText().trim();
            if (!content.isEmpty()) {
                note.setContent(content);
                refreshCurrentView();
            }
        }
    }
    
    // ==================== CALENDAR PICKER ====================
    
    private JButton createCalendarButton(JTextField dateField) {
        JButton btn = new JButton("\u25BC");
        btn.setFont(FONT_TINY);
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
            }
        });
        
        btn.addActionListener(e -> showCalendarPopup(btn, dateField));
        
        return btn;
    }
    
    private void showCalendarPopup(JButton anchor, JTextField dateField) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        popup.setBackground(Color.WHITE);
        
        // Parse current date or use today
        LocalDate selectedDate = LocalDate.now();
        if (!dateField.getText().isEmpty()) {
            try {
                selectedDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ex) {
                // Use today
            }
        }
        
        final LocalDate[] currentMonth = { YearMonth.from(selectedDate).atDay(1) };
        
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 10));
        calendarPanel.setBackground(Color.WHITE);
        
        // Month navigation header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JButton prevBtn = createNavButton("\u25C0");
        JButton nextBtn = createNavButton("\u25B6");
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(FONT_BODY);
        monthLabel.setForeground(TEXT_PRIMARY);
        
        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);
        
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Days grid
        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        daysPanel.setBackground(Color.WHITE);
        
        Runnable updateCalendar = () -> {
            daysPanel.removeAll();
            
            YearMonth ym = YearMonth.from(currentMonth[0]);
            monthLabel.setText(ym.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + ym.getYear());
            
            // Day headers
            for (DayOfWeek dow : DayOfWeek.values()) {
                JLabel dayLabel = new JLabel(dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).substring(0, 2), SwingConstants.CENTER);
                dayLabel.setFont(FONT_TINY);
                dayLabel.setForeground(TEXT_MUTED);
                daysPanel.add(dayLabel);
            }
            
            // Calculate first day offset
            LocalDate firstOfMonth = ym.atDay(1);
            int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            
            // Empty cells before first day
            for (int i = 1; i < startDayOfWeek; i++) {
                daysPanel.add(new JLabel(""));
            }
            
            // Days of month
            LocalDate today = LocalDate.now();
            for (int day = 1; day <= ym.lengthOfMonth(); day++) {
                LocalDate date = ym.atDay(day);
                JButton dayBtn = new JButton(String.valueOf(day));
                dayBtn.setFont(FONT_SMALL);
                dayBtn.setForeground(date.equals(today) ? new Color(52, 52, 52) : TEXT_PRIMARY);
                dayBtn.setBackground(Color.WHITE);
                dayBtn.setBorder(new EmptyBorder(6, 6, 6, 6));
                dayBtn.setFocusPainted(false);
                dayBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
                if (date.equals(today)) {
                    dayBtn.setBorder(new CompoundBorder(
                        new LineBorder(new Color(52, 52, 52), 1, true),
                        new EmptyBorder(5, 5, 5, 5)
                    ));
                }
                
                dayBtn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        dayBtn.setBackground(ACCENT_LIGHT);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        dayBtn.setBackground(Color.WHITE);
                    }
                });
                
                dayBtn.addActionListener(e -> {
                    dateField.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    popup.setVisible(false);
                });
                
                daysPanel.add(dayBtn);
            }
            
            daysPanel.revalidate();
            daysPanel.repaint();
        };
        
        prevBtn.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].minusMonths(1);
            updateCalendar.run();
        });
        
        nextBtn.addActionListener(e -> {
            currentMonth[0] = currentMonth[0].plusMonths(1);
            updateCalendar.run();
        });
        
        updateCalendar.run();
        
        calendarPanel.add(daysPanel, BorderLayout.CENTER);
        
        // Quick date buttons
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        quickPanel.setBackground(Color.WHITE);
        quickPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JButton todayBtn = createQuickDateButton("Today", LocalDate.now(), dateField, popup);
        JButton tomorrowBtn = createQuickDateButton("Tomorrow", LocalDate.now().plusDays(1), dateField, popup);
        JButton nextWeekBtn = createQuickDateButton("Next Week", LocalDate.now().plusWeeks(1), dateField, popup);
        
        quickPanel.add(todayBtn);
        quickPanel.add(tomorrowBtn);
        quickPanel.add(nextWeekBtn);
        
        calendarPanel.add(quickPanel, BorderLayout.SOUTH);
        
        popup.add(calendarPanel);
        popup.show(anchor, 0, anchor.getHeight() + 4);
    }
    
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_TINY);
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(TEXT_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_SECONDARY);
            }
        });
        
        return btn;
    }
    
    private JButton createQuickDateButton(String label, LocalDate date, JTextField dateField, JPopupMenu popup) {
        JButton btn = new JButton(label);
        btn.setFont(FONT_TINY);
        btn.setForeground(new Color(52, 52, 52));
        btn.setBackground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT_LIGHT);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        btn.addActionListener(e -> {
            dateField.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            popup.setVisible(false);
        });
        
        return btn;
    }
    
    // ==================== STYLED DIALOG COMPONENTS ====================
    
    private JPanel createStyledDialogPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(380, 280));
        return panel;
    }
    
    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 25);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setCaretColor(new Color(52, 52, 52));
        return field;
    }
    
    private JTextArea createStyledTextArea(String text) {
        JTextArea area = new JTextArea(text, 5, 30);
        area.setFont(FONT_BODY);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretColor(new Color(52, 52, 52));
        area.setBorder(null);
        return area;
    }
    
    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        return combo;
    }

    private void addStyledFormField(JPanel panel, String label, JComponent field, boolean required) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 6));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fieldPanel.setBorder(new EmptyBorder(0, 0, 12, 0));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label + (required ? " *" : ""));
        fieldLabel.setFont(FONT_SMALL);
        fieldLabel.setForeground(TEXT_SECONDARY);

        fieldPanel.add(fieldLabel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);

        panel.add(fieldPanel);
    }

    private String showStyledInputDialog(String title, String message, String initialValue) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(message);
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_SECONDARY);
        
        JTextField field = createStyledTextField(initialValue);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, panel, title,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            return field.getText();
        }
        return null;
    }

    // ==================== HELPER METHODS ====================

    private void refreshCurrentView() {
        switch (currentView) {
            case PAGE:
                if (currentPage != null) {
                    showPageContent(currentPage);
                }
                break;
            case UPCOMING:
            case OVERDUE:
            case PRIORITY:
                showSummaryView(currentView);
                break;
        }
    }

    private Color getPriorityColor(Priority priority) {
        switch (priority) {
            case HIGH: return DANGER;
            case MEDIUM: return WARNING;
            case LOW: return SUCCESS;
            default: return TEXT_SECONDARY;
        }
    }

    private JLabel createStatusBadge(Status status) {
        JLabel badge = new JLabel(status.toString().replace("_", " "));
        badge.setFont(FONT_TINY);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        switch (status) {
            case COMPLETED:
                badge.setBackground(SUCCESS_LIGHT);
                badge.setForeground(SUCCESS);
                break;
            case IN_PROGRESS:
                badge.setBackground(ACCENT_LIGHT);
                badge.setForeground(ACCENT);
                break;
            case NOT_STARTED:
                badge.setBackground(new Color(248, 248, 247));
                badge.setForeground(TEXT_TERTIARY);
                break;
            case CANCELLED:
                badge.setBackground(new Color(248, 248, 247));
                badge.setForeground(TEXT_MUTED);
                break;
        }
        
        return badge;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 22, 11, 22));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
        
        return button;
    }

    private JButton createTextButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY);
        button.setForeground(color);
        button.setBackground(BACKGROUND);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(color);
            }
        });
        
        return button;
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_TINY);
        button.setForeground(TEXT_MUTED);
        button.setBackground(CARD_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(TEXT_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(TEXT_MUTED);
            }
        });
        
        return button;
    }

    // ==================== CUSTOM COMPONENTS ====================

    /**
     * Custom JList cell renderer for pages with elegant styling
     */
    private class PageListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            Page page = (Page) value;
            
            JPanel panel = new RoundedPanel(6);
            panel.setLayout(new BorderLayout());
            panel.setBackground(isSelected ? SIDEBAR_SELECTED : SIDEBAR_BG);
            panel.setBorder(new EmptyBorder(10, 12, 10, 12));
            
            // Page icon
            JLabel icon = new JLabel("\u25A1");
            icon.setFont(FONT_SMALL);
            icon.setForeground(TEXT_MUTED);
            icon.setBorder(new EmptyBorder(0, 0, 0, 10));
            
            JLabel nameLabel = new JLabel(page.getName());
            nameLabel.setFont(FONT_BODY);
            nameLabel.setForeground(TEXT_PRIMARY);
            
            int taskCount = page.getTasks().size();
            JLabel countLabel = new JLabel(taskCount + (taskCount == 1 ? " task" : " tasks"));
            countLabel.setFont(FONT_TINY);
            countLabel.setForeground(TEXT_MUTED);
            
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setOpaque(false);
            leftPanel.add(icon, BorderLayout.WEST);
            leftPanel.add(nameLabel, BorderLayout.CENTER);
            
            panel.add(leftPanel, BorderLayout.CENTER);
            panel.add(countLabel, BorderLayout.EAST);
            
            return panel;
        }
    }

    /**
     * Custom JPanel with rounded corners
     */
    private static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(238, 238, 236));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }
}
