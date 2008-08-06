/*
 * Copyright (c) 2003-2008 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.renderer.lwjgl;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.math.Matrix4f;
import com.jme.renderer.AbstractCamera;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>LWJGLCamera</code> defines a concrete implementation of a
 * <code>AbstractCamera</code> using the LWJGL library for view port setting.
 * Most functionality is provided by the <code>AbstractCamera</code> class with
 * this class handling the OpenGL specific calls to set the frustum and
 * viewport.
 * @author Mark Powell
 * @version $Id: LWJGLCamera.java,v 1.22 2007/09/20 15:14:43 nca Exp $
 */
public class LWJGLCamera extends AbstractCamera {

    private static final long serialVersionUID = 1L;

    private final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
    private final Matrix4f _transMatrix = new Matrix4f();

    public LWJGLCamera() {}

    /**
     * Constructor instantiates a new <code>LWJGLCamera</code> object. The
     * width and height are provided, which corresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public LWJGLCamera(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        update();
        apply();
    }
    
    /**
     * Constructor instantiates a new <code>LWJGLCamera</code> object. The
     * width and height are provided, which corresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public LWJGLCamera(int width, int height, boolean dataOnly) {
        super(dataOnly);
        this.width = width;
        this.height = height;
        setDataOnly(dataOnly);
        update();
        apply();
    }

    /**
     * @return the width/resolution of the display.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the height/resolution of the display.
     */
    public int getWidth() {
        return width;
    }

    /**
     * <code>resize</code> resizes this cameras view with the given width/height.
     * This is similar to constructing a new camera, but reusing the same
     * Object.
     * @param width int
     * @param height int
     */
    public void resize(int width, int height) {
      this.width = width;
      this.height = height;
      onViewPortChange();
    }

    private boolean frustumDirty;
    private boolean viewPortDirty;
    private boolean frameDirty;

    public void apply() {
        if ( frustumDirty ) {
            doFrustumChange();
            frustumDirty = false;
        }
        if ( viewPortDirty ) {
            doViewPortChange();
            viewPortDirty = false;
        }
        if ( frameDirty ) {
            doFrameChange();
            frameDirty = false;
        }
    }

    @Override
    public void onFrustumChange() {
        super.onFrustumChange();
        frustumDirty = true;
    }

    public void onViewPortChange() {
        viewPortDirty = true;
    }

    @Override
    public void onFrameChange() {
        super.onFrameChange();
        frameDirty = true;
    }

    /**
     * Sets the OpenGL frustum.
     * @see com.jme.renderer.Camera#onFrustumChange()
     */
    public void doFrustumChange() {

        if (!isDataOnly()) {
            // set projection matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            if ( !isParallelProjection() )
            {
                GL11.glFrustum(
                    frustumLeft,
                    frustumRight,
                    frustumBottom,
                    frustumTop,
                    frustumNear,
                    frustumFar);
            }
            else
            {
                GL11.glOrtho(
                        frustumLeft,
                        frustumRight,
                        frustumTop,
                        frustumBottom,
                        frustumNear,
                        frustumFar);
            }
            if ( projection != null )
            {
                tmp_FloatBuffer.rewind();
                GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, tmp_FloatBuffer);
                tmp_FloatBuffer.rewind();
                projection.readFloatBuffer( tmp_FloatBuffer );
            }
        }

    }

    /**
     * Sets OpenGL's viewport.
     * @see com.jme.renderer.Camera#onViewPortChange()
     */
    public void doViewPortChange() {

        if (!isDataOnly()) {
            // set view port
            int x = (int) (viewPortLeft * width);
            int y = (int) (viewPortBottom * height);
            int w = (int) ((viewPortRight - viewPortLeft) * width);
            int h = (int) ((viewPortTop - viewPortBottom) * height);
            GL11.glViewport(x, y, w, h);
        }
    }

    /**
     * Uses GLU's lookat function to set the OpenGL frame.
     * @see com.jme.renderer.Camera#onFrameChange()
     */
    public void doFrameChange() {

        if (!isDataOnly()) {
            // set view matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_MODELVIEW);

            GL11.glLoadMatrix(getModelViewMatrix().fillFloatBuffer(matrix));
        }
    }

    private static final FloatBuffer tmp_FloatBuffer = BufferUtils.createFloatBuffer(16);
    private Matrix4f projection;

    public Matrix4f getProjectionMatrix() {
        if ( projection == null )
        {
            projection = new Matrix4f();
            doFrustumChange();
        }
        return projection;
    }

    private Matrix4f modelView = new Matrix4f();

    public Matrix4f getModelViewMatrix() {
        // XXX: Cache results or is this low cost enough to happen every time it is called?
        modelView.loadIdentity();
        modelView.m00 = -left.x;
        modelView.m10 = -left.y;
        modelView.m20 = -left.z;

        modelView.m01 = up.x;
        modelView.m11 = up.y;
        modelView.m21 = up.z;

        modelView.m02 = -direction.x;
        modelView.m12 = -direction.y;
        modelView.m22 = -direction.z;

        _transMatrix.loadIdentity();
        _transMatrix.m30 = -location.x;
        _transMatrix.m31 = -location.y;
        _transMatrix.m32 = -location.z;

        _transMatrix.multLocal(modelView);
        modelView.set(_transMatrix);

        return modelView;
    }
}
