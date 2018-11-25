package brightspark.runicmagic.gui;

import brightspark.runicmagic.RunicMagic;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RMGuiScreen extends GuiScreen
{
    private final ResourceLocation guiImage;
    protected int xSize = 176;
    protected int ySize = 168;
    protected int guiLeft, guiTop;

    public RMGuiScreen(String guiImageName)
    {
        guiImage = new ResourceLocation(RunicMagic.MOD_ID, "textures/gui/" + guiImageName + ".png");
    }

    @Override
    public void initGui()
    {
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        GlStateManager.color(1F, 1F, 1F);

        //Draw GUI background
        mc.getTextureManager().bindTexture(guiImage);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawExtraBg();

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawText();

        List<String> tooltip = new ArrayList<>();
        drawTooltips(tooltip, mouseX, mouseY);
        if(!tooltip.isEmpty())
            drawHoveringText(tooltip, mouseX, mouseY);
    }

    protected void drawExtraBg() {}

    protected void drawText() {}

    protected void drawTooltips(List<String> tooltip, int mouseX, int mouseY) {}

    /**
     * Draws text which wraps by word to the next lines below if there's not enough space between xLeft and xRight
     * Returns how many lines the text took up
     */
    protected int wrapText(String text, int x, int y, int width, int colour, boolean drawShadow)
    {
        String[] textArray = text.split(" ");
        String line = null;
        int lineNum = 0;
        int lineHeight = fontRenderer.FONT_HEIGHT;
        for(String s : textArray)
        {
            //Add text to line
            if(s.equals("\n"))
            {
                drawString(line == null ? "" : line, x, y + (lineNum++ * lineHeight), colour, drawShadow);
                line = null;
                continue;
            }
            if(line == null)
                line = s;
            else
            {
                if(fontRenderer.getStringWidth(line + s) <= width)
                    line += " " + s;
                else
                {
                    //Draw string then go to next line
                    drawString(line, x, y + (lineNum++ * lineHeight), colour, drawShadow);
                    line = s;
                }
            }
        }
        if(line != null)
            drawString(line, x, y + (lineNum * lineHeight), colour, drawShadow);
        return lineNum + 1;
    }

    public void drawString(String text, int x, int y, int color, boolean drawShadow)
    {
        fontRenderer.drawString(text, (float)x, (float)y, color, drawShadow);
    }

    public void drawString(String text, int x, int y, int color)
    {
        drawString(text, x, y, color, false);
    }

    public void drawStringWithShadow(String text, int x, int y, int color)
    {
        drawString(text, x, y, color, true);
    }

    public void drawCenteredString(String text, int x, int y, int color, boolean drawShadow)
    {
        fontRenderer.drawString(text, (float)(x - fontRenderer.getStringWidth(text) / 2), (float)y, color, drawShadow);
    }

    public void drawCenteredString(String text, int x, int y, int color)
    {
        drawCenteredString(text, x, y, color, false);
    }

    public void drawCenteredStringWithShadow(String text, int x, int y, int color)
    {
        drawCenteredString(text, x, y, color, true);
    }
}
