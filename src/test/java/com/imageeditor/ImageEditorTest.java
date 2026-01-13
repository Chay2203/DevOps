package com.imageeditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ImageEditor operations.
 */
class ImageEditorTest {

    private BufferedImage testImage;
    private static final int TEST_WIDTH = 10;
    private static final int TEST_HEIGHT = 10;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(TEST_WIDTH, TEST_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                int red = (x * 25) % 256;
                int green = (y * 25) % 256;
                int blue = ((x + y) * 12) % 256;
                Color color = new Color(red, green, blue);
                testImage.setRGB(x, y, color.getRGB());
            }
        }
    }

    @Test
    @DisplayName("Grayscale conversion produces correct image dimensions")
    void testGrayscaleDimensions() {
        BufferedImage result = ImageEditor.convertToGrayscale(testImage);

        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    @DisplayName("Grayscale conversion produces grayscale image type")
    void testGrayscaleType() {
        BufferedImage result = ImageEditor.convertToGrayscale(testImage);

        assertEquals(BufferedImage.TYPE_BYTE_GRAY, result.getType());
    }

    @Test
    @DisplayName("Brightness increase makes image brighter")
    void testBrightnessIncrease() {
        int x = 5;
        int y = 5;
        Color originalColor = new Color(testImage.getRGB(x, y));

        BufferedImage result = ImageEditor.adjustBrightness(testImage, 50);
        Color resultColor = new Color(result.getRGB(x, y));

        assertTrue(resultColor.getRed() >= originalColor.getRed());
        assertTrue(resultColor.getGreen() >= originalColor.getGreen());
        assertTrue(resultColor.getBlue() >= originalColor.getBlue());
    }

    @Test
    @DisplayName("Brightness decrease makes image darker")
    void testBrightnessDecrease() {
        int x = 5;
        int y = 5;
        Color originalColor = new Color(testImage.getRGB(x, y));

        BufferedImage result = ImageEditor.adjustBrightness(testImage, -50);
        Color resultColor = new Color(result.getRGB(x, y));

        assertTrue(resultColor.getRed() <= originalColor.getRed());
        assertTrue(resultColor.getGreen() <= originalColor.getGreen());
        assertTrue(resultColor.getBlue() <= originalColor.getBlue());
    }

    @Test
    @DisplayName("Brightness values are clamped to valid range")
    void testBrightnessClamping() {
        BufferedImage result = ImageEditor.adjustBrightness(testImage, 500);

        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                Color color = new Color(result.getRGB(x, y));
                assertTrue(color.getRed() >= 0 && color.getRed() <= 255);
                assertTrue(color.getGreen() >= 0 && color.getGreen() <= 255);
                assertTrue(color.getBlue() >= 0 && color.getBlue() <= 255);
            }
        }
    }

    @Test
    @DisplayName("Rotate right swaps width and height")
    void testRotateRightDimensions() {
        BufferedImage result = ImageEditor.rotateRight(testImage);

        assertEquals(TEST_HEIGHT, result.getWidth());
        assertEquals(TEST_WIDTH, result.getHeight());
    }

    @Test
    @DisplayName("Rotate left swaps width and height")
    void testRotateLeftDimensions() {
        BufferedImage result = ImageEditor.rotateLeft(testImage);

        assertEquals(TEST_HEIGHT, result.getWidth());
        assertEquals(TEST_WIDTH, result.getHeight());
    }

    @Test
    @DisplayName("Four right rotations return to original orientation")
    void testFourRotationsReturnToOriginal() {
        BufferedImage result = testImage;

        for (int i = 0; i < 4; i++) {
            result = ImageEditor.rotateRight(result);
        }

        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    @DisplayName("Horizontal flip preserves dimensions")
    void testFlipHorizontalDimensions() {
        BufferedImage result = ImageEditor.flipHorizontal(testImage);

        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    @DisplayName("Double horizontal flip returns original pixels")
    void testDoubleHorizontalFlip() {
        BufferedImage result = ImageEditor.flipHorizontal(testImage);
        result = ImageEditor.flipHorizontal(result);

        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                assertEquals(testImage.getRGB(x, y), result.getRGB(x, y));
            }
        }
    }

    @Test
    @DisplayName("Vertical flip preserves dimensions")
    void testFlipVerticalDimensions() {
        BufferedImage result = ImageEditor.flipVertical(testImage);

        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    @DisplayName("Double vertical flip returns original pixels")
    void testDoubleVerticalFlip() {
        BufferedImage result = ImageEditor.flipVertical(testImage);
        result = ImageEditor.flipVertical(result);

        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                assertEquals(testImage.getRGB(x, y), result.getRGB(x, y));
            }
        }
    }

    @Test
    @DisplayName("Blur preserves image dimensions")
    void testBlurDimensions() {
        BufferedImage result = ImageEditor.applyBlur(testImage, 2);

        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    @DisplayName("Blur produces valid pixel values")
    void testBlurValidPixels() {
        BufferedImage result = ImageEditor.applyBlur(testImage, 2);

        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                int rgb = result.getRGB(x, y);
                Color color = new Color(rgb);
                assertTrue(color.getRed() >= 0 && color.getRed() <= 255);
                assertTrue(color.getGreen() >= 0 && color.getGreen() <= 255);
                assertTrue(color.getBlue() >= 0 && color.getBlue() <= 255);
            }
        }
    }

    @Test
    @DisplayName("Blur with block size 1 preserves original image")
    void testBlurBlockSizeOne() {
        BufferedImage result = ImageEditor.applyBlur(testImage, 1);

        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                assertEquals(testImage.getRGB(x, y), result.getRGB(x, y));
            }
        }
    }

    @Test
    @DisplayName("Null image handling in grayscale")
    void testGrayscaleWithNullThrows() {
        assertThrows(NullPointerException.class, () -> {
            ImageEditor.convertToGrayscale(null);
        });
    }

    @Test
    @DisplayName("Null image handling in brightness")
    void testBrightnessWithNullThrows() {
        assertThrows(NullPointerException.class, () -> {
            ImageEditor.adjustBrightness(null, 50);
        });
    }
}
