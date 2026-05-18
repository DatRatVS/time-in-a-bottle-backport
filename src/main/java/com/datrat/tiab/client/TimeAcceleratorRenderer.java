package com.datrat.tiab.client;

import com.datrat.tiab.entity.TimeAcceleratorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TimeAcceleratorRenderer extends Render {
    private static final float FACE_OFFSET = 0.506F;
    private static final float TEXT_SCALE = 0.0125F;

    public TimeAcceleratorRenderer() {
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        TimeAcceleratorEntity accelerator = (TimeAcceleratorEntity) entity;
        String timeText = Math.max(0, (accelerator.getRemainingTime() + 19) / 20) + "s";
        String rateText = "x" + (accelerator.getTimeRate() * 2);
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawFace(font, timeText, rateText, 0.0F, 0.0F, FACE_OFFSET, 0.0F, 0.0F);
        drawFace(font, timeText, rateText, 0.0F, 0.0F, -FACE_OFFSET, 0.0F, 180.0F);
        drawFace(font, timeText, rateText, FACE_OFFSET, 0.0F, 0.0F, 0.0F, 90.0F);
        drawFace(font, timeText, rateText, -FACE_OFFSET, 0.0F, 0.0F, 0.0F, -90.0F);
        drawFace(font, timeText, rateText, 0.0F, FACE_OFFSET, 0.0F, 90.0F, 0.0F);
        drawFace(font, timeText, rateText, 0.0F, -FACE_OFFSET, 0.0F, -90.0F, 0.0F);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private void drawFace(FontRenderer font, String timeText, String rateText, float x, float y, float z, float rotateX, float rotateY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotateY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotateX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        drawCentered(font, timeText, -11);
        drawCentered(font, rateText, 1);

        GL11.glPopMatrix();
    }

    private void drawCentered(FontRenderer font, String text, int y) {
        font.drawStringWithShadow(text, -font.getStringWidth(text) / 2, y, 0xFFFFFFFF);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
