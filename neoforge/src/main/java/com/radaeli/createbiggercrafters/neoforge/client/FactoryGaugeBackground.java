package com.radaeli.createbiggercrafters.neoforge.client;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.gui.GuiGraphics;

/** Expands the original gauge sprites without scaling their pixels or borders. */
public final class FactoryGaugeBackground {
    private static final int CELL_SIZE = 20;

    private FactoryGaugeBackground() {}

    public static void renderRecipe(GuiGraphics graphics, int x, int y, int columns, int rows) {
        AllGuiTextures texture = AllGuiTextures.FACTORY_GAUGE_RECIPE;
        int extraWidth = (columns - 3) * CELL_SIZE;
        int extraHeight = (rows - 3) * CELL_SIZE;

        // Insert wood below the original grid; leave the sidebar and output arrow intact.
        renderWidened(graphics, texture, x, y, 0, 88, extraWidth, 86, CELL_SIZE);
        for (int offset = 0; offset < extraHeight; offset += 4) {
            renderWidened(graphics, texture, x, y + 88 + offset, 88,
                    Math.min(4, extraHeight - offset), extraWidth, 86, CELL_SIZE);
        }
        renderWidened(graphics, texture, x, y + 88 + extraHeight, 88, 8, extraWidth, 86, CELL_SIZE);

        // Keep the outer cells with their borders; repeat only the center cell.
        int gridX = x + 56;
        int gridY = y + 24;
        int innerWidth = (columns - 2) * CELL_SIZE;
        int innerHeight = (rows - 2) * CELL_SIZE;
        blit(graphics, texture, gridX, gridY, 56, 24, 30, 22);
        blit(graphics, texture, gridX + 30 + innerWidth, gridY, 106, 24, 30, 22);
        blit(graphics, texture, gridX, gridY + 22 + innerHeight, 56, 66, 30, 22);
        blit(graphics, texture, gridX + 30 + innerWidth, gridY + 22 + innerHeight, 106, 66, 30, 22);
        for (int column = 0; column < columns - 2; column++) {
            int cellX = gridX + 30 + column * CELL_SIZE;
            blit(graphics, texture, cellX, gridY, 86, 24, CELL_SIZE, 22);
            blit(graphics, texture, cellX, gridY + 22 + innerHeight, 86, 66, CELL_SIZE, 22);
            for (int row = 0; row < rows - 2; row++) {
                blit(graphics, texture, cellX, gridY + 22 + row * CELL_SIZE,
                        86, 46, CELL_SIZE, CELL_SIZE);
            }
        }
        for (int row = 0; row < rows - 2; row++) {
            int cellY = gridY + 22 + row * CELL_SIZE;
            blit(graphics, texture, gridX, cellY, 56, 46, 30, CELL_SIZE);
            blit(graphics, texture, gridX + 30 + innerWidth, cellY, 106, 46, 30, CELL_SIZE);
        }
    }

    public static void renderBottom(GuiGraphics graphics, int x, int y, int extraWidth) {
        // Extend the empty space before the divider, keeping the button wells at the right edge.
        AllGuiTextures texture = AllGuiTextures.FACTORY_GAUGE_BOTTOM;
        renderWidened(graphics, texture, x, y, 0, texture.getHeight(), extraWidth, 128, 2);
    }

    private static void renderWidened(GuiGraphics graphics, AllGuiTextures texture, int x, int y,
                                      int sourceY, int height, int extraWidth, int cutX, int tileWidth) {
        blit(graphics, texture, x, y, 0, sourceY, cutX, height);
        for (int offset = 0; offset < extraWidth; offset += tileWidth) {
            blit(graphics, texture, x + cutX + offset, y, cutX, sourceY,
                    Math.min(tileWidth, extraWidth - offset), height);
        }
        blit(graphics, texture, x + cutX + extraWidth, y, cutX, sourceY,
                texture.getWidth() - cutX, height);
    }

    private static void blit(GuiGraphics graphics, AllGuiTextures texture, int x, int y,
                             int sourceX, int sourceY, int width, int height) {
        graphics.blit(texture.getLocation(), x, y, texture.getStartX() + sourceX,
                texture.getStartY() + sourceY, width, height);
    }
}
