package AHuffman.visualization;

import AHuffman.src.HuffmanTree;
import AHuffman.src.Node;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class HTreeVisualizer {
    private JFrame frame;
    private TreePanel treePanel;
    private JLabel statusLabel;
    
    public HTreeVisualizer() {
        frame = new JFrame("Adaptive Huffman Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Create layout with status panel at bottom
        frame.setLayout(new BorderLayout());
        
        // Add tree panel
        treePanel = new TreePanel();
        frame.add(treePanel, BorderLayout.CENTER);
        
        // Add status panel at the bottom
        statusLabel = new JLabel("Initializing tree...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void visualize(HuffmanTree tree) {
        treePanel.setTree(tree);
        frame.setVisible(true);
    }
    
    public void update() {
        treePanel.repaint();
    }
    
    /**
     * Highlights a node that had its frequency incremented
     */
    public void highlightFrequencyChange(Node node, char c) {
        // First clear any previous highlights
        treePanel.clearHighlights();
        
        String message = "Incremented frequency of '" + (node.character != null ? node.character : "Internal Node") + 
                         "' to " + node.frequency;
        statusLabel.setText(message);
        treePanel.markFrequencyChange(node);
        update();
        
        // Schedule clearing of highlights
        treePanel.scheduleHighlightClear();
    }
    
    /**
     * Highlights two nodes that were swapped
     */
    public void highlightSwap(Node node1, Node node2) {
        // First clear any previous highlights
        treePanel.clearHighlights();
        
        String message = "Swapped: " + (node1.character != null ? "'" + node1.character + "'" : "Internal Node") + 
                         " with " + (node2.character != null ? "'" + node2.character + "'" : "Internal Node");
        statusLabel.setText(message);
        treePanel.markSwap(node1, node2);
        update();
        
        // Schedule clearing of highlights
        treePanel.scheduleHighlightClear();
    }
    
    // Inner class for rendering the tree
    private class TreePanel extends JPanel {
        private HuffmanTree tree;
        private static final int NODE_RADIUS = 30;
        private static final int HORIZONTAL_SPACING = 60;
        private static final int VERTICAL_SPACING = 80;
        
        // Track recently updated nodes for highlighting
        private Set<Node> frequencyChangedNodes = new HashSet<>();
        private Node swappedNode1;
        private Node swappedNode2;
        private Timer highlightTimer;
        
        public TreePanel() {
            // Create the timer but don't start it yet
            highlightTimer = new Timer(1500, e -> {
                clearHighlights();
                repaint();
                // Stop the timer after it fires once
                ((Timer)e.getSource()).stop();
            });
            highlightTimer.setRepeats(false);
        }
        
        public void setTree(HuffmanTree tree) {
            this.tree = tree;
            repaint();
        }
        
        public void clearHighlights() {
            // Stop any running timer
            if (highlightTimer.isRunning()) {
                highlightTimer.stop();
            }
            
            frequencyChangedNodes.clear();
            swappedNode1 = null;
            swappedNode2 = null;
        }
        
        public void scheduleHighlightClear() {
            // Restart the timer
            if (highlightTimer.isRunning()) {
                highlightTimer.restart();
            } else {
                highlightTimer.start();
            }
        }
        
        public void markFrequencyChange(Node node) {
            frequencyChangedNodes.clear(); // Ensure only one node is highlighted
            frequencyChangedNodes.add(node);
        }
        
        public void markSwap(Node node1, Node node2) {
            swappedNode1 = node1;
            swappedNode2 = node2;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (tree != null) {
                int rootX = getWidth() / 2;
                int rootY = 50;
                drawTree(g2, tree.root, rootX, rootY, getWidth() / 4);
            }
        }
        
        private void drawTree(Graphics2D g, Node node, int x, int y, int xOffset) {
            if (node == null) return;
            
            // Determine node color based on state
            if (frequencyChangedNodes.contains(node)) {
                // Flash green for frequency change
                g.setColor(new Color(50, 220, 50));
            } else if (node == swappedNode1 || node == swappedNode2) {
                // Highlight orange for swap
                g.setColor(new Color(255, 165, 0));
            } else if (node == tree.NYT) {
                // Yellow for NYT
                g.setColor(Color.YELLOW);
            } else if (node.character == null) {
                // Light gray for internal
                g.setColor(Color.LIGHT_GRAY);
            } else {
                // White for leaf nodes with characters
                g.setColor(Color.WHITE);
            }
            
            // Draw node circle
            g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            
            // Draw outline
            if (node == swappedNode1 || node == swappedNode2) {
                g.setColor(new Color(255, 0, 0));
                g.setStroke(new BasicStroke(3));
            } else {
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1));
            }
            g.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g.setStroke(new BasicStroke(1));
            
            // Draw node content
            String content = node == tree.NYT ? "NYT" : 
                            (node.character != null ? node.character.toString() : "I");
            content += "\nF:" + node.frequency;
            content += "\nN:" + node.number;
            content += "\n" + node.code;
            
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            drawCenteredString(g, content, x, y);
            
            // Draw links to children
            if (node.left != null) {
                int childX = x - xOffset;
                int childY = y + VERTICAL_SPACING;
                g.drawLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
                g.drawString("0", (x + childX) / 2 - 10, (y + childY) / 2);
                drawTree(g, node.left, childX, childY, xOffset / 2);
            }
            
            if (node.right != null) {
                int childX = x + xOffset;
                int childY = y + VERTICAL_SPACING;
                g.drawLine(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
                g.drawString("1", (x + childX) / 2 + 5, (y + childY) / 2);
                drawTree(g, node.right, childX, childY, xOffset / 2);
            }
        }
        
        private void drawCenteredString(Graphics g, String text, int x, int y) {
            FontMetrics metrics = g.getFontMetrics();
            String[] lines = text.split("\n");
            int lineHeight = metrics.getHeight();
            int totalHeight = lines.length * lineHeight;
            
            y = y - totalHeight / 2 + lineHeight / 2;
            
            for (String line : lines) {
                int width = metrics.stringWidth(line);
                g.drawString(line, x - width / 2, y);
                y += lineHeight;
            }
        }
    }
}