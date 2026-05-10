package gui;

import core.Board;
import core.Position;
import core.State;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BoardPanel extends JPanel {
    private static final int DEFAULT_WIDTH = 560;
    private static final int DEFAULT_HEIGHT = 420;
    private static final int CELL_SIZE = 42;
    private static final int PADDING = 18;

    private Board board;
    private State state;

    public BoardPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
    }

    public void setBoard(Board board) {
        this.board = board;
        this.state = null;
        updatePreferredSize();
        repaint();
    }

    public void setState(State state) {
        this.state = state;
        repaint();
    }

    private void updatePreferredSize() {
        if (board == null) {
            setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        } else {
            int width = board.getCols() * CELL_SIZE + PADDING * 2;
            int height = board.getRows() * CELL_SIZE + PADDING * 2;
            setPreferredSize(new Dimension(Math.max(360, width), Math.max(260, height)));
        }
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (board == null) {
            drawEmptyState(g);
            g.dispose();
            return;
        }

        int cellSize = Math.max(18, Math.min(
                (getWidth() - PADDING * 2) / board.getCols(),
                (getHeight() - PADDING * 2) / board.getRows()));
        int gridWidth = cellSize * board.getCols();
        int gridHeight = cellSize * board.getRows();
        int startX = (getWidth() - gridWidth) / 2;
        int startY = (getHeight() - gridHeight) / 2;

        char[][] grid = renderedGrid();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                drawCell(g, grid[row][col], startX + col * cellSize, startY + row * cellSize, cellSize);
            }
        }

        g.dispose();
    }

    private void drawEmptyState(Graphics2D g) {
        g.setColor(new Color(90, 90, 90));
        g.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        String text = "Choose an input file to display the board.";
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2, getHeight() / 2);
    }

    private char[][] renderedGrid() {
        char[][] grid = board.copyTileGrid();
        if (state == null) {
            return grid;
        }

        Position start = board.getStart();
        Position actor = state.getPosition();
        if (!start.equals(actor)) {
            grid[start.getRow()][start.getCol()] = '*';
        }
        grid[actor.getRow()][actor.getCol()] = 'Z';
        return grid;
    }

    private void drawCell(Graphics2D g, char symbol, int x, int y, int size) {
        g.setColor(colorFor(symbol));
        g.fillRect(x, y, size, size);
        g.setColor(new Color(165, 165, 165));
        g.drawRect(x, y, size, size);

        g.setColor(textColorFor(symbol));
        g.setFont(getFont().deriveFont(Math.max(12f, size * 0.42f)));
        String text = Character.toString(symbol);
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (size - metrics.stringWidth(text)) / 2;
        int textY = y + (size - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(text, textX, textY);
    }

    private Color colorFor(char symbol) {
        if (symbol == 'X') {
            return new Color(70, 70, 70);
        }
        if (symbol == 'L') {
            return new Color(225, 120, 100);
        }
        if (symbol == 'O') {
            return new Color(130, 190, 130);
        }
        if (symbol == 'Z') {
            return new Color(95, 145, 215);
        }
        if (Character.isDigit(symbol)) {
            return new Color(235, 210, 120);
        }
        return new Color(245, 245, 245);
    }

    private Color textColorFor(char symbol) {
        if (symbol == 'X' || symbol == 'Z') {
            return Color.WHITE;
        }
        return new Color(35, 35, 35);
    }
}
