package com.mojang.minecraft.render;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;

import net.lax1dude.eaglercraft.EaglerImage;
import net.lax1dude.eaglercraft.GLAllocation;

public class RenderEngine {

	public RenderEngine() {
		singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
		imageDataB1 = GLAllocation.createDirectByteBuffer(0x100000);
		alpha = false;
		options = Minecraft.settings;
	}

	public int getTexture(String s) {
		Integer integer = (Integer) textureMap.get(s);
		if (integer != null) {
			return integer.intValue();
		}
		try {
			singleIntBuffer.clear();
			GLAllocation.generateTextureNames(singleIntBuffer);
			int i = singleIntBuffer.get(0);
			if(s.equals("/terrain.png") || s.contains("arrow") || s.contains("default")) {
				alpha = true;
			}
			setupTexture(readTextureImage(GL11.loadResourceBytes(s)), i);
			alpha = false;
			textureMap.put(s, Integer.valueOf(i));
			return i;
		} catch (IOException ioexception) {
			throw new RuntimeException("!!");
		}
	}

	public void setupTexture(EaglerImage bufferedimage, int i) {
		if(alpha) {
			GL11.glAlphaFunc(516, 0.1F);
		}
		bindTexture(i);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10241 /* GL_TEXTURE_MIN_FILTER */, 9728 /* GL_NEAREST */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10240 /* GL_TEXTURE_MAG_FILTER */, 9728 /* GL_NEAREST */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10242 /* GL_TEXTURE_WRAP_S */, 10497 /* GL_REPEAT */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10243 /* GL_TEXTURE_WRAP_T */, 10497 /* GL_REPEAT */);
		int j = bufferedimage.w;
		int k = bufferedimage.h;
		int ai[] = bufferedimage.data;
		byte abyte0[] = new byte[j * k * 4];
		for (int l = 0; l < ai.length; l++) {
			int j1 = ai[l] >> 24 & 0xff;
			int l1 = ai[l] >> 16 & 0xff;
			int j2 = ai[l] >> 8 & 0xff;
			int l2 = ai[l] >> 0 & 0xff;
			if (options != null && options.anaglyph) {
				int j3 = (l1 * 30 + j2 * 59 + l2 * 11) / 100;
				int l3 = (l1 * 30 + j2 * 70) / 100;
				int j4 = (l1 * 30 + l2 * 70) / 100;
				l1 = j3;
				j2 = l3;
				l2 = j4;
			}
			abyte0[l * 4 + 0] = (byte) l1;
			abyte0[l * 4 + 1] = (byte) j2;
			abyte0[l * 4 + 2] = (byte) l2;
			abyte0[l * 4 + 3] = (byte) j1;
		}
		imageDataB1.clear();
		imageDataB1.put(abyte0);
		imageDataB1.position(0).limit(abyte0.length);
		GL11.glTexImage2D(3553 /* GL_TEXTURE_2D */, 0, GL11.GL_RGBA /* GL_RGBA */, j, k, 0, GL11.GL_RGBA /* GL_RGBA */,
				5121 /* GL_UNSIGNED_BYTE */, imageDataB1);
	}

	public void deleteTexture(int i) {
		GL11.glDeleteTextures(i);
	}
	
	private EaglerImage readTextureImage(byte[] inputstream) throws IOException {
		return GL11.loadPNG(inputstream);
	}

	public void bindTexture(int i) {
		if (i < 0) {
			return;
		} else {
			GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, i);
			return;
		}
	}

	public static HashMap<String, Integer> textureMap = new HashMap<String, Integer>();
	private IntBuffer singleIntBuffer;
	private ByteBuffer imageDataB1;
	private GameSettings options;
	private boolean alpha;
}