/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jmetest.renderer;

import org.lwjgl.input.Keyboard;

import com.jme.app.*;
import com.jme.image.Texture;
import com.jme.input.*;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;

/**
 * <code>TestPQTorus</code> demonstrates the construction and animation of
 * a parameterized torus, also known as a pq torus.
 * @author Eric Woroshow
 * @version $Id: TestPQTorus.java,v 1.4 2004-03-21 23:22:51 renanse Exp $
 */
public class TestPQTorus extends VariableTimestepGame {

    private Camera cam;
    private CameraNode camNode;
    private Node scene;
    private InputController input;
    private Timer timer;
    private Text fps;
    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0);
    private PQTorus t;

    private float p, q;

    /**
     * Entry point for the test.
     * @param args arguments passed to the program; ignored
     */
    public static void main(String[] args) {
        LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
        TestPQTorus app = new TestPQTorus();
        app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Animates the PQ torus.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)){
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
              p -= 0.01f;
            else
              p += 0.01f;
            generatePQTorus();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)){
          if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            q -= 0.01f;
          else
            q += 0.01f;
            generatePQTorus();
        }

        input.update(interpolation * 5);

        if (interpolation < 1) {
            angle = angle + interpolation;
            if (angle > 360) angle = 0;
        }

        rotQuat.fromAngleAxis(angle, axis);
        t.setLocalRotation(rotQuat);

        scene.updateGeometricState(interpolation, true);
    }

    /**
     * Clears the buffers and then draws the PQ torus.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(scene);
    }

    /**
     * Creates the displays and sets up the viewport.
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        try {
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(properties.getWidth(), properties.getHeight(),
                    properties.getDepth(), properties.getFreq(), properties.getFullscreen());
            cam = display.getRenderer().getCamera(properties.getWidth(),
                    properties.getHeight());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);

        display.getRenderer().setCamera(cam);

        camNode = new CameraNode("Camera Node", cam);
        camNode.setLocalTranslation(new Vector3f(0, 0, -25));
        camNode.updateWorldData(0);

        input = new NodeController(this, camNode, properties.getRenderer());
        input.setKeySpeed(10f);
        input.setMouseSpeed(1f);
        display.setTitle("PQ Torus Test");
    }

    /**
     * builds the trimesh.
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        scene = new Node("scene");

        //Generate the geometry
        p = 3.0f;
        q = 2.0f;
        generatePQTorus();

        //Set the render states
        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setFunction(ZBufferState.CF_LEQUAL);
        buf.setEnabled(true);

        WireframeState ws = display.getRenderer().getWireframeState();
        ws.setFace(WireframeState.WS_FRONT);
        ws.setEnabled(false);

		TextureState ts = display.getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(
			TextureManager.loadTexture(
				TestBoxColor.class.getClassLoader().getResource(
					"jmetest/data/texture/dirt.jpg"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));

		scene.setRenderState(ts);
        scene.setRenderState(buf);
        scene.setRenderState(ws);

        setUpLighting();

        scene.setForceView(true);
        scene.updateGeometricState(0.0f, true);
    }

    private void setUpLighting(){
		SpotLight am = new SpotLight();
		am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am.setDirection(new Vector3f(0, 0, 0));
		am.setLocation(new Vector3f(250, 100, 0));
		am.setAngle(1);

		SpotLight am2 = new SpotLight();
		am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am2.setDirection(new Vector3f(0, 0, 0));
		am2.setLocation(new Vector3f(-250, 10, 0));
		am2.setAngle(1);

		DirectionalLight dr = new DirectionalLight();
		dr.setDiffuse(new ColorRGBA(0.25f, 0.75f, 0.25f, 1.0f));
		dr.setAmbient(new ColorRGBA(0.25f, 0.25f, 0.25f, 1.0f));
		dr.setDirection(new Vector3f(150, 0, 150));

		LightState state = display.getRenderer().getLightState();
		state.attach(am);
		state.attach(dr);
		state.attach(am2);
		state.setEnabled(true);

		am.setEnabled(true);
		am2.setEnabled(true);
		dr.setEnabled(true);

		scene.setRenderState(state);
    }

    private void generatePQTorus(){
        //Generate a torus with 128 steps along the torus, 16 radial samples,
        //and a radius of 2.0 units
        t = new PQTorus("torus", p, q, 2.0f, 1.0f, 128, 16);

        //Update the scene
        scene.detachAllChildren();
        scene.attachChild(t);
    }

    /**
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {}

    /**
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {}

}
