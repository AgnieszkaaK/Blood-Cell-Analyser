package com.example.demo2;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import java.util.*;

public class ImageProcessor {

    private Image originalImage;
    private WritableImage tricolourImage;
    private Canvas overlayCanvas; // overlay for boxes
    private int width, height;
    private int[] parent; // union-find parent for clustering
    private String[] cellMap; // stores red, purple and white for each pixel
    private int redCellCount = 0;
    private int whiteCellCount = 0;

    public void setOriginalImage(Image image, Canvas canvas) {
        this.originalImage = image;
        this.overlayCanvas = canvas;
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    // converts image into tricolours - red, purple and white
    public void convertToTricolour() {
        if (originalImage == null) { // check for image
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR); // make error popup
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please load an image first!");
            alert.showAndWait();
            return;
        }

        PixelReader reader = originalImage.getPixelReader(); // reads pixel colours
        tricolourImage = new WritableImage(width, height);
        PixelWriter writer = tricolourImage.getPixelWriter(); // write to set pixels
        cellMap = new String[width * height]; // array that stores colours

        // loop through each pixel in image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y); // get pixel colour
                double hue = color.getHue(); // extract hue
                double sat = color.getSaturation(); // extract saturation
                double brightness = color.getBrightness(); // extract brightness
                int index = y * width + x; // convert 2D (x,y) to 1D index

                String cellType = "WHITE";

                // pixel based on hue and saturation
                if (sat > 0.2) {
                    if ((hue >= 330 || hue <= 25)) { // detect red
                        cellType = "RED";
                    } else if (hue >= 240 && hue <= 290) { // detect purple
                        cellType = "PURPLE";
                    }
                }

                cellMap[index] = cellType; //

                // set pixel color based on cell type
                writer.setColor(x, y, switch (cellType) {
                    case "RED" -> Color.rgb(200, 30, 30); // red
                    case "PURPLE" -> Color.rgb(90, 0, 160); // purple
                    default -> Color.WHITE;
                });
            }
        }
    }

    public WritableImage getTriColorImage() {
        return tricolourImage;
    }

    // find clusters and cells
    public int analyseCells(ImageView displayView, boolean showLabels) {
        parent = new int[width * height]; // union-find structure
        Arrays.setAll(parent, i -> i); // initially each pixel is its own parent
        redCellCount = 0;
        whiteCellCount = 0;

        // join adjacent pixels into clusters
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x; // index for this pixel
                if (cellMap[idx].equals("WHITE")) continue;

                // check right and join if same
                if (x + 1 < width && cellMap[idx].equals(cellMap[y * width + (x + 1)]))
                    union(idx, y * width + (x + 1));

                // Check bottom and join if same
                if (y + 1 < height && cellMap[idx].equals(cellMap[(y + 1) * width + x]))
                    union(idx, (y + 1) * width + x);
            }
        }

        // group pixels by their cluster root
        Map<Integer, List<int[]>> groups = new HashMap<>(); // root is pixel list
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x; // pixel index
                if (cellMap[idx].equals("WHITE")) continue; // skip anything that isnt a cell
                int root = find(idx); // find the root of pixel
                groups.computeIfAbsent(root, k -> new ArrayList<>()).add(new int[]{x, y}); // add
            }
        }

        GraphicsContext gc = overlayCanvas.getGraphicsContext2D(); // get drawing context
        gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());

        double scaleX = overlayCanvas.getWidth() / width; // x scale
        double scaleY = overlayCanvas.getHeight() / height; // y scale

        int label = 1; // start numbering clusters
        for (var group : groups.values()) {
            if (group.size() < 50) continue; // skip small noise

            int minX = width, minY = height, maxX = 0, maxY = 0; // box
            for (int[] pixel : group) {
                minX = Math.min(minX, pixel[0]); // left edge
                minY = Math.min(minY, pixel[1]); // top edge
                maxX = Math.max(maxX, pixel[0]); // right edge
                maxY = Math.max(maxY, pixel[1]); // bottom edge
            }

            String type = cellMap[group.get(0)[1] * width + group.get(0)[0]]; // type of first pixel
            Color color = switch (type) {
                case "RED" -> {
                    redCellCount++; // count red
                    yield Color.GREEN; // red box
                }
                case "PURPLE" -> {
                    whiteCellCount++; // count purple
                    yield Color.DARKVIOLET; // purple box
                }
                default -> Color.GRAY;
            };

            // draw box
            gc.setStroke(color);
            gc.setLineWidth(1.5);
            gc.strokeRect(minX * scaleX, minY * scaleY,
                    (maxX - minX + 1) * scaleX, (maxY - minY + 1) * scaleY);

            // draw label number
            if (showLabels) {
                gc.setFill(color);
                gc.fillText("#" + label, minX * scaleX + 3, minY * scaleY + 12);
            }

            label++;
        }

        return label - 1;
    }

    public int getRedCellCount() {
        return redCellCount;
    }

    public int getWhiteCellCount() {
        return whiteCellCount;
    }

// Union Find

    // find root of set containing element x
    private int find(int x) {
        if (parent[x] != x) { // if not root
            parent[x] = find(parent[x]); // make x point to root
        }
        return parent[x]; // return root
    }

    // union (merge) the sets containing x and y
    private void union(int x, int y) {
        int rootX = find(x); // get root of x
        int rootY = find(y); // fet root of y

        if (rootX != rootY) { // only merge if different
            parent[rootY] = rootX; // make y's root point to x's root
        }
    }
}
