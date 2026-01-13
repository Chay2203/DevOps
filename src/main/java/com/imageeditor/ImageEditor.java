package com.imageeditor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * Image Editor - A command-line image processing utility.
 * Supports grayscale conversion, brightness adjustment, rotation, flip, and blur operations.
 */
public class ImageEditor {

    /**
     * Prints RGB pixel values of an image to standard output.
     *
     * @param inputImage the image to analyze
     */
    public static void printPixelValue(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(inputImage.getRGB(j, i) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Converts an image to grayscale.
     *
     * @param inputImage the color image to convert
     * @return grayscale version of the image
     */
    public static BufferedImage convertToGrayscale(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                outputImage.setRGB(j, i, inputImage.getRGB(j, i));
            }
        }
        return outputImage;
    }

    /**
     * Adjusts the brightness of an image by a percentage.
     *
     * @param inputImage the image to adjust
     * @param percentage brightness adjustment (-100 to darken, +100 to brighten)
     * @return brightness-adjusted image
     */
    public static BufferedImage adjustBrightness(BufferedImage inputImage, int percentage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color pixel = new Color(inputImage.getRGB(j, i));
                int red = pixel.getRed();
                int green = pixel.getGreen();
                int blue = pixel.getBlue();

                red += (red * percentage) / 100;
                green += (green * percentage) / 100;
                blue += (blue * percentage) / 100;

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                Color newPixel = new Color(red, green, blue);
                result.setRGB(j, i, newPixel.getRGB());
            }
        }
        return result;
    }

    /**
     * Rotates an image 90 degrees clockwise.
     *
     * @param inputImage the image to rotate
     * @return rotated image
     */
    public static BufferedImage rotateRight(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage rotatedImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rotatedImage.setRGB(i, j, inputImage.getRGB(j, i));
            }
        }

        int index = rotatedImage.getWidth() - 1;
        for (int i = 0; i < rotatedImage.getHeight(); i++) {
            for (int j = 0; j < rotatedImage.getWidth() / 2; j++) {
                Color temp = new Color(rotatedImage.getRGB(j, i));
                rotatedImage.setRGB(j, i, rotatedImage.getRGB(index - j, i));
                rotatedImage.setRGB(index - j, i, temp.getRGB());
            }
        }
        return rotatedImage;
    }

    /**
     * Rotates an image 90 degrees counter-clockwise.
     *
     * @param inputImage the image to rotate
     * @return rotated image
     */
    public static BufferedImage rotateLeft(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage rotatedImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rotatedImage.setRGB(i, j, inputImage.getRGB(j, i));
            }
        }

        int index = rotatedImage.getHeight() - 1;
        for (int j = 0; j < rotatedImage.getWidth(); j++) {
            for (int i = 0; i < rotatedImage.getHeight() / 2; i++) {
                Color temp = new Color(rotatedImage.getRGB(j, i));
                rotatedImage.setRGB(j, i, rotatedImage.getRGB(j, index - i));
                rotatedImage.setRGB(j, index - i, temp.getRGB());
            }
        }
        return rotatedImage;
    }

    /**
     * Flips an image horizontally (left-right mirror).
     *
     * @param inputImage the image to flip
     * @return horizontally flipped image
     */
    public static BufferedImage flipHorizontal(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                outputImage.setRGB(j, i, inputImage.getRGB(width - j - 1, i));
            }
        }
        return outputImage;
    }

    /**
     * Flips an image vertically (top-bottom mirror).
     *
     * @param inputImage the image to flip
     * @return vertically flipped image
     */
    public static BufferedImage flipVertical(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                outputImage.setRGB(j, i, inputImage.getRGB(j, height - i - 1));
            }
        }
        return outputImage;
    }

    /**
     * Applies a pixelated blur effect to an image.
     *
     * @param inputImage the image to blur
     * @param blockSize the size of pixel blocks for averaging
     * @return blurred image
     */
    public static BufferedImage applyBlur(BufferedImage inputImage, int blockSize) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < height / blockSize; i++) {
            for (int j = 0; j < width / blockSize; j++) {
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int k = i * blockSize; k < i * blockSize + blockSize; k++) {
                    for (int l = j * blockSize; l < j * blockSize + blockSize; l++) {
                        Color pixel = new Color(inputImage.getRGB(l, k));
                        red += pixel.getRed();
                        blue += pixel.getBlue();
                        green += pixel.getGreen();
                    }
                }

                int avgRed = red / (blockSize * blockSize);
                int avgGreen = green / (blockSize * blockSize);
                int avgBlue = blue / (blockSize * blockSize);

                for (int k = i * blockSize; k < i * blockSize + blockSize; k++) {
                    for (int l = j * blockSize; l < j * blockSize + blockSize; l++) {
                        Color newPixel = new Color(avgRed, avgGreen, avgBlue);
                        outputImage.setRGB(l, k, newPixel.getRGB());
                    }
                }
            }
        }
        return outputImage;
    }

    /**
     * Displays the help menu with available operations.
     */
    public static void printHelp() {
        System.out.println("Image Editor - Command Line Image Processing Tool");
        System.out.println("================================================");
        System.out.println();
        System.out.println("Usage: java -jar image-editor.jar [options]");
        System.out.println();
        System.out.println("Interactive Mode (no arguments):");
        System.out.println("  Run without arguments to enter interactive mode.");
        System.out.println();
        System.out.println("Available Operations:");
        System.out.println("  1 - Print pixel RGB values");
        System.out.println("  2 - Convert to grayscale");
        System.out.println("  3 - Adjust brightness (percentage)");
        System.out.println("  4 - Rotate right (90 degrees clockwise)");
        System.out.println("  5 - Rotate left (90 degrees counter-clockwise)");
        System.out.println("  6 - Flip horizontal (left-right mirror)");
        System.out.println("  7 - Flip vertical (top-bottom mirror)");
        System.out.println("  8 - Apply blur effect");
        System.out.println();
        System.out.println("Output: Results are saved to 'output.jpg'");
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     * @throws IOException if image file operations fail
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printHelp();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename: ");
        String filename = scanner.next();

        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            System.err.println("Error: File not found - " + filename);
            scanner.close();
            return;
        }

        BufferedImage inputImage = ImageIO.read(inputFile);
        if (inputImage == null) {
            System.err.println("Error: Could not read image file - " + filename);
            scanner.close();
            return;
        }

        System.out.println("\nSelect an operation:");
        System.out.println("1 - Print pixel values");
        System.out.println("2 - Convert to grayscale");
        System.out.println("3 - Adjust brightness");
        System.out.println("4 - Rotate right (90 degrees)");
        System.out.println("5 - Rotate left (90 degrees)");
        System.out.println("6 - Flip horizontal");
        System.out.println("7 - Flip vertical");
        System.out.println("8 - Apply blur");
        System.out.print("\nEnter choice: ");

        int choice = scanner.nextInt();
        BufferedImage result = null;

        switch (choice) {
            case 1:
                printPixelValue(inputImage);
                break;
            case 2:
                result = convertToGrayscale(inputImage);
                break;
            case 3:
                System.out.print("Enter brightness percentage (-100 to 100): ");
                int brightness = scanner.nextInt();
                result = adjustBrightness(inputImage, brightness);
                break;
            case 4:
                result = rotateRight(inputImage);
                break;
            case 5:
                result = rotateLeft(inputImage);
                break;
            case 6:
                result = flipHorizontal(inputImage);
                break;
            case 7:
                result = flipVertical(inputImage);
                break;
            case 8:
                System.out.print("Enter blur block size (e.g., 5): ");
                int blockSize = scanner.nextInt();
                result = applyBlur(inputImage, blockSize);
                break;
            default:
                System.err.println("Invalid choice: " + choice);
        }

        if (result != null) {
            File outputFile = new File("output.jpg");
            ImageIO.write(result, "jpg", outputFile);
            System.out.println("Output saved to: output.jpg");
        }

        scanner.close();
    }
}
