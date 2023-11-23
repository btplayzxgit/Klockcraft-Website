package net.lax1dude.eaglercraft.adapter;

import org.lwjgl.opengl.GL11;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int32Array;

public class Tessellator {

	/** The byte buffer used for GL allocation. */
	private Int32Array intBuffer;
	private Float32Array floatBuffer;

	/**
	 * The number of vertices to be drawn in the next draw call. Reset to 0 between
	 * draw calls.
	 */
	private int vertexCount = 0;

	/** The first coordinate to be used for the texture. */
	private float textureU;

	/** The second coordinate to be used for the texture. */
	private float textureV;

	/** The color (RGBA) value to be used for the following draw call. */
	private int color;

	/**
	 * Whether the current draw object for this tessellator has color values.
	 */
	private boolean hasColor = false;

	/**
	 * Whether the current draw object for this tessellator has texture coordinates.
	 */
	private boolean hasTexture = false;

	/** The index into the raw buffer to be used for the next data. */
	private int rawBufferIndex = 0;

	/**
	 * The number of vertices manually added to the given draw call. This differs
	 * from vertexCount because it adds extra vertices when converting quads to
	 * triangles.
	 */
	private int addedVertices = 0;

	/** Disables all color information for the following draw call. */
	private boolean isColorDisabled = false;

	/**
	 * An offset to be applied along the x-axis for all vertices in this draw call.
	 */
	private double xOffset;

	/**
	 * An offset to be applied along the y-axis for all vertices in this draw call.
	 */
	private double yOffset;

	/**
	 * An offset to be applied along the z-axis for all vertices in this draw call.
	 */
	private double zOffset;

	/** The static instance of the Tessellator. */
	public static final Tessellator instance = new Tessellator(525000);

	/** Whether this tessellator is currently in draw mode. */
	private boolean isDrawing = false;

	/** Whether we are currently using VBO or not. */
	private boolean useVBO = false;

	/** The size of the buffers used (in integers). */
	private int bufferSize;
	
	private int drawMode;

	private Tessellator(int par1) {
		this.bufferSize = par1;
		ArrayBuffer a = ArrayBuffer.create(par1 * 4);
		this.intBuffer = Int32Array.create(a);
		this.floatBuffer = Float32Array.create(a);
	}

	/**
	 * Draws the data set up in this tessellator and resets the state to prepare for
	 * new drawing.
	 */
	public int draw() {
		if (!this.isDrawing) {
			return 0;
		} else {
			this.isDrawing = false;

			if (this.vertexCount > 0) {
				
				if (this.hasTexture) {
					GL11.glEnableVertexAttrib(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					GL11.glEnableVertexAttrib(GL11.GL_COLOR_ARRAY);
				}

//     			if (this.hasNormals) {
//					GL11.glEnableVertexAttrib(GL11.GL_NORMAL_ARRAY);
//				}
				
				GL11.glDrawArrays(drawMode, 0, this.vertexCount, Int32Array.create(intBuffer.getBuffer(), 0, this.vertexCount * 7));
				
				if (this.hasTexture) {
					GL11.glDisableVertexAttrib(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					GL11.glDisableVertexAttrib(GL11.GL_COLOR_ARRAY);
				}

//				if (this.hasNormals) {
//					GL11.glDisableVertexAttrib(GL11.GL_NORMAL_ARRAY);
//			    }
			}

			int var1 = this.rawBufferIndex * 4;
			this.reset();
			return var1;
		}
	}

	/**
	 * Clears the tessellator state in preparation for new drawing.
	 */
	private void reset() {
		this.vertexCount = 0;
		//this.byteBuffer.clear();
		this.rawBufferIndex = 0;
		this.addedVertices = 0;
	}

	/**
	 * Resets tessellator state and prepares for drawing (with the specified draw
	 * mode).
	 */
	public void startDrawing(int drawMode) {
//		if (this.isDrawing) {
//			this.draw();
//		}
		this.drawMode = drawMode;
		this.isDrawing = true;
		this.reset();
		this.hasColor = false;
		this.hasTexture = false;
		this.isColorDisabled = false;
	}

	/**
	 * Sets the texture coordinates.
	 */
	public void setTextureUV(double par1, double par3) {
		this.hasTexture = true;
		this.textureU = (float) par1;
		this.textureV = (float) par3;
	}

	/**
	 * Sets the RGB values as specified, converting from floats between 0 and 1 to
	 * integers from 0-255.
	 */
	public void setColorOpaque_F(float par1, float par2, float par3) {
		this.setColorOpaque((int) (par1 * 255.0F), (int) (par2 * 255.0F), (int) (par3 * 255.0F));
	}

	/**
	 * Sets the RGBA values for the color, converting from floats between 0 and 1 to
	 * integers from 0-255.
	 */
	public void setColorRGBA_F(float par1, float par2, float par3, float par4) {
		this.setColorRGBA((int) (par1 * 255.0F), (int) (par2 * 255.0F), (int) (par3 * 255.0F), (int) (par4 * 255.0F));
	}

	/**
	 * Sets the RGB values as specified, and sets alpha to opaque.
	 */
	public void setColorOpaque(int par1, int par2, int par3) {
		this.setColorRGBA(par1, par2, par3, 255);
	}

	/**
	 * Sets the RGBA values for the color. Also clamps them to 0-255.
	 */
	public void setColorRGBA(int par1, int par2, int par3, int par4) {
		if (!this.isColorDisabled) {
			if (par1 > 255) {
				par1 = 255;
			}

			if (par2 > 255) {
				par2 = 255;
			}

			if (par3 > 255) {
				par3 = 255;
			}

			if (par4 > 255) {
				par4 = 255;
			}

			if (par1 < 0) {
				par1 = 0;
			}

			if (par2 < 0) {
				par2 = 0;
			}

			if (par3 < 0) {
				par3 = 0;
			}

			if (par4 < 0) {
				par4 = 0;
			}

			this.hasColor = true;
			this.color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
		}
	}

	/**
	 * Adds a vertex specifying both x,y,z and the texture u,v for it.
	 */
	public void addVertexWithUV(double par1, double par3, double par5, double par7, double par9) {
		this.setTextureUV(par7, par9);
		this.addVertex(par1, par3, par5);
	}

	/**
	 * Adds a vertex with the specified x,y,z to the current draw call. It will
	 * trigger a draw() if the buffer gets full.
	 */
	public void addVertex(double par1, double par3, double par5) {
		if(this.addedVertices > 65534) return;
		++this.addedVertices;
		++this.vertexCount;
		
		int bufferIndex = this.rawBufferIndex;
		Int32Array intBuffer0 = intBuffer;
		Float32Array floatBuffer0 = floatBuffer;

		floatBuffer0.set(bufferIndex + 0, (float) (par1 + this.xOffset));
		floatBuffer0.set(bufferIndex + 1, (float) (par3 + this.yOffset));
		floatBuffer0.set(bufferIndex + 2, (float) (par5 + this.zOffset));

		if (this.hasTexture) {
			floatBuffer0.set(bufferIndex + 3, this.textureU);
			floatBuffer0.set(bufferIndex + 4, this.textureV);
		}

		if (this.hasColor) {
			intBuffer0.set(bufferIndex + 5, this.color);
		}

		this.rawBufferIndex += 7;
	}

	/**
	 * Sets the color to the given opaque value (stored as byte values packed in an
	 * integer).
	 */
	public void setColorOpaque_I(int par1) {
		int var2 = par1 >> 16 & 255;
		int var3 = par1 >> 8 & 255;
		int var4 = par1 & 255;
		this.setColorOpaque(var2, var3, var4);
	}

	/**
	 * Sets the color to the given color (packed as bytes in integer) and alpha
	 * values.
	 */
	public void setColorRGBA_I(int par1, int par2) {
		int var3 = par1 >> 16 & 255;
		int var4 = par1 >> 8 & 255;
		int var5 = par1 & 255;
		this.setColorRGBA(var3, var4, var5, par2);
	}

	/**
	 * Disables colors for the current draw call.
	 */
	public void disableColor() {
		this.isColorDisabled = true;
	}

	/**
	 * Sets the normal for the current draw call.
	 */
	public void setNormal(float par1, float par2, float par3) {
		GL11.glNormal3f(par1, par2, par3);
	}

	/**
	 * Sets the translation for all vertices in the current draw call.
	 */
	public void setTranslationD(double par1, double par3, double par5) {
		this.xOffset = par1;
		this.yOffset = par3;
		this.zOffset = par5;
	}

	/**
	 * Offsets the translation for all vertices in the current draw call.
	 */
	public void setTranslationF(float par1, float par2, float par3) {
		this.xOffset += (float) par1;
		this.yOffset += (float) par2;
		this.zOffset += (float) par3;
	}
}
