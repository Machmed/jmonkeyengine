package jme3test.model.anim;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.ControlType;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.debug.SkeletonWire;
import com.jme3.scene.debug.SkeletonPoints;
import com.jme3.scene.plugins.ogre.OgreMaterialList;
import com.jme3.scene.plugins.ogre.OgreMeshKey;

public class TestOgreComplexAnim extends SimpleApplication {

    private AnimControl control;
    private float angle = 0;
    private float rate = 1;

    public static void main(String[] args) {
        TestOgreComplexAnim app = new TestOgreComplexAnim();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        cam.setLocation(new Vector3f(6.4013605f, 7.488437f, 12.843031f));
        cam.setRotation(new Quaternion(-0.060740203f, 0.93925786f, -0.2398315f, -0.2378785f));

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);

        OgreMaterialList matList = (OgreMaterialList) manager.loadContent("OTO.material");
        OgreMeshKey key = new OgreMeshKey("OTO.meshxml", matList);
        Node model = (Node) manager.loadContent(key);

        control = (AnimControl) model.getControl(ControlType.BoneAnimation);
        AnimChannel feet = control.createChannel();
        AnimChannel leftHand = control.createChannel();
        AnimChannel rightHand = control.createChannel();

        // feet will dodge
        feet.addFromRootBone("hip.right");
        feet.addFromRootBone("hip.left");
        feet.setAnim("Dodge");
        feet.setSpeed(2);
        feet.setLoopMode(LoopMode.Cycle);

        // will blend over 15 seconds to stand
        feet.setAnim("Walk", 15);
        feet.setSpeed(0.25f);
        feet.setLoopMode(LoopMode.Cycle);

        // left hand will pull
        leftHand.addFromRootBone("uparm.right");
        leftHand.setAnim("pull");
        leftHand.setSpeed(.5f);

        // will blend over 15 seconds to stand
        leftHand.setAnim("stand", 15);

        // right hand will push
        rightHand.addBone("spinehigh");
        rightHand.addFromRootBone("uparm.left");
        rightHand.setAnim("push");

        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat = new Material(manager, "wire_color.j3md");
        mat.setColor("m_Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);

        model.attachChild(skeletonDebug);
        rootNode.attachChild(model);
    }

    @Override
    public void simpleUpdate(float tpf){
        Bone b = control.getSkeleton().getBone("spinehigh");

        angle += tpf * rate;
        if (angle > FastMath.HALF_PI / 2f){
            angle = FastMath.HALF_PI / 2f;
            rate = -1;
        }else if (angle < -FastMath.HALF_PI / 2f){
            angle = -FastMath.HALF_PI / 2f;
            rate = 1;
        }

        Quaternion q = new Quaternion();
        q.fromAngles(0, angle, 0);

        b.setUserControl(true);
        b.setUserTransforms(Vector3f.ZERO, q, Vector3f.UNIT_XYZ);
    }

}
