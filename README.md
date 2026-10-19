# Blood Cell Analyser 🔬

A JavaFX application I built for college to explore image processing and data structures.

The app loads an image, separates pixels by colour and groups neighbouring pixels to identify potential blood cell regions.

## Features

- Load PNG and JPEG images.
- Convert an image into red, purple and white regions.
- Group connected pixels using union-find.
- Display total, red and white cell counts.
- Draw bounding boxes around detected regions.
- Optionally display numbered labels.

## Technologies

Java 21 · JavaFX · FXML · Maven

## How it works

Pixels are classified using hue and saturation thresholds.
Neighbouring pixels of the same class are joined using union-find.
Small groups are filtered out, and the remaining regions are counted
and highlighted.

## Limitations

Detection depends on the image colours and fixed thresholds.
Touching cells may be grouped together, and small regions are ignored.

This is an educational image-processing project, not a validated
medical tool.
