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
package com.jme.input.action;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyStrafeLeftAction</code> defines an action that causes the camera to
 * move along the negative left vector. The speed at which it moves is set and
 * of the form units per second.
 * @author Mark Powell
 * @version $Id: KeyStrafeRightAction.java,v 1.4 2004-02-24 01:32:20 mojomonkey Exp $
 */
public class KeyStrafeRightAction implements InputAction {

    private Camera camera;
    private float speed;
    private String key;

    /**
     * Constructor instantiates a new <code>KeyStrafeLeftAction</code> object.
     * @param camera the camera to move along the negative left vector.
     * @param speed the speed at which to move the camera.
     */
    public KeyStrafeRightAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * <code>performAction</code> moves the camera along the negative left
     * vector for a given distance of speed * time.
     * @see com.jme.input.action.InputAction#performAction(float)
     */
    public void performAction(float time) {
        Vector3f loc = camera.getLocation();
        loc.subtractLocal(camera.getLeft().mult((speed * time)));
        camera.setLocation(loc);
        camera.update();
    }

    /**
     * <code>getKey</code> retrieves the key associated with this action.
     * @see com.jme.input.action.InputAction#getKey()
     */
    public String getKey() {
        return key;
    }

    /**
     * <code>setKey</code> sets the key associated with this action.
     * @see com.jme.input.action.InputAction#setKey(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;

    }

}