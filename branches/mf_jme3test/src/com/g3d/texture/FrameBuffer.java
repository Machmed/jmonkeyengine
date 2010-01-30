package com.g3d.texture;

import com.g3d.renderer.GLObject;
import com.g3d.renderer.Renderer;
import com.g3d.texture.Image.Format;

public class FrameBuffer extends GLObject {

    private int width = 0;
    private int height = 0;
    private int samples = 0;
    private RenderBuffer colorBuf = null;
    private RenderBuffer depthBuf = null;

    public class RenderBuffer {

        Texture tex;
        Image.Format format;
        int id = -1;
        int slot = -1;

        public Format getFormat() {
            return format;
        }

        public Texture getTexture(){
            return tex;
        }

        public int getId() {
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public void resetObject(){
            id = -1;
        }
    }

    public FrameBuffer(int width, int height, int samples){
        super(Type.FrameBuffer);
        if (width <= 0 || height <= 0)
                throw new IllegalArgumentException("FrameBuffer must have valid size.");

        this.width = width;
        this.height = height;
        this.samples = samples;
    }

    protected FrameBuffer(FrameBuffer src){
        super(Type.FrameBuffer, src.id);
        this.colorBuf = src.colorBuf;
        this.depthBuf = src.depthBuf;
    }

    public void setDepthBuffer(Image.Format format){
        if (id != -1)
            throw new UnsupportedOperationException("FrameBuffer already initialized.");

        if (!format.isDepthFormat())
            throw new IllegalArgumentException("Depth buffer format must be depth.");
            
        depthBuf = new RenderBuffer();
        depthBuf.slot = -100; // -100 == special slot for DEPTH_BUFFER
        depthBuf.format = format;
    }

    public void setColorBuffer(Image.Format format){
        if (id != -1)
            throw new UnsupportedOperationException("FrameBuffer already initialized.");

        if (format.isDepthFormat())
            throw new IllegalArgumentException("Color buffer format must be color/luminance.");
        
        colorBuf = new RenderBuffer();
        colorBuf.slot = 0;
        colorBuf.format = format;
    }

    private void checkSetTexture(Texture tex, boolean depth){
        Image img = tex.getImage();
        if (img == null)
            throw new IllegalArgumentException("Texture not initialized with RTT.");

        if (depth && !img.getFormat().isDepthFormat())
            throw new IllegalArgumentException("Texture image format must be depth.");
        else if (!depth && img.getFormat().isDepthFormat())
            throw new IllegalArgumentException("Texture image format must be color/luminance.");

        // check that resolution matches texture resolution
        if (width != img.getWidth() || height != img.getHeight())
            throw new IllegalArgumentException("Texture image resolution " +
                                               "must match FB resolution");

        if (samples > 1)
            throw new IllegalStateException("Cannot attach texture to multisampled FB");
    }

    public void setColorTexture(Texture2D tex) {
        if (id != -1)
            throw new UnsupportedOperationException("FrameBuffer already initialized.");

        Image img = tex.getImage();
        checkSetTexture(tex, false);

        colorBuf = new RenderBuffer();
        colorBuf.slot = 0;
        colorBuf.tex = tex;
        colorBuf.format = img.getFormat();
    }

    public void setDepthTexture(Texture2D tex){
        if (id != -1)
            throw new UnsupportedOperationException("FrameBuffer already initialized.");

        Image img = tex.getImage();
        checkSetTexture(tex, true);
        
        depthBuf = new RenderBuffer();
        depthBuf.slot = -100; // indicates GL_DEPTH_ATTACHMENT
        depthBuf.tex = tex;
        depthBuf.format = img.getFormat();
    }

    public RenderBuffer getColorBuffer() {
        return colorBuf;
    }

    public RenderBuffer getDepthBuffer() {
        return depthBuf;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSamples() {
        return samples;
    }

    @Override
    public void resetObject() {
        this.id = -1;
        if (colorBuf != null)
            colorBuf.resetObject();
        
        if (depthBuf != null)
            depthBuf.resetObject();

        setUpdateNeeded();
    }

    @Override
    public void deleteObject(Renderer r) {
        r.deleteFrameBuffer(this);
    }

    public GLObject createDestructableClone(){
        return new FrameBuffer(this);
    }
}
