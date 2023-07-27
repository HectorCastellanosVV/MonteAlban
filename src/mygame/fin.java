package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class fin extends SimpleApplication implements ActionListener {

    private CharacterControl player;
    final private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private float walkSpeedFactor = 4.0f; // Adjust this value to control the walking speed

    private boolean topView = false;
    private boolean frontView = false;
    private boolean sideView = false;

    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();

    public static void main(String[] args) {
        fin app = new fin();
        app.start();
    }

    private void showInstructions() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText instructionsText = new BitmapText(guiFont, false);
        instructionsText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        instructionsText.setText("Instrucciones:\n"
                + "W - Mover hacia adelante\n"
                + "A - Mover hacia la izquierda\n"
                + "S - Mover hacia atrás\n"
                + "D - Mover hacia la derecha\n"
                + "Barra Espaciadora - Saltar\n"
                + "Click derecho - Cambiar perspectiva");

        instructionsText.setLocalTranslation(0, settings.getHeight(), 0);
        guiNode.attachChild(instructionsText);
        float width = instructionsText.getLineWidth();
        float height = instructionsText.getLineHeight() * 7; // Ajusta el valor multiplicador según la cantidad de líneas de texto
        Geometry background = new Geometry("TextBackground", new Quad(width + 20, height + 20)); // Aumenta el tamaño del fondo según tus necesidades
        Material bgMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMaterial.setColor("Color", new ColorRGBA(0.2f, 0.2f, 0.2f, 0.7f)); // Color del fondo (RGBA)
        bgMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        background.setMaterial(bgMaterial);

        // Ajustar la posición del fondo para que coincida con la posición del texto
        float textX = instructionsText.getLocalTranslation().x;
        float textY = instructionsText.getLocalTranslation().y;
        float textWidth = width * instructionsText.getLocalScale().x;
        float textHeight = height * instructionsText.getLocalScale().y;
        background.setLocalTranslation(textX - 10, textY - textHeight + 10, 0);

        guiNode.attachChild(background);
    }

    @Override
    public void simpleInitApp() {
        showInstructions();
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(50000000);
        setUpKeys();
        setUpLight();

        assetManager.registerLocator(
                "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/town.zip",
                HttpZipLocator.class);
        Spatial sceneModel = assetManager.loadModel("Models/mAlban4/montealban3.j3o");

        sceneModel.setLocalScale(9f);
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(-2250, 450, 150));
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0f);
        sceneModel.addControl(landscape);

        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 0.2f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(10);
        player.setFallSpeed(10);
        player.setGravity(0f);
        player.setPhysicsLocation(new Vector3f(-10, 3, -5));

        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
    }

    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);

        Vector3f northDirection = new Vector3f(0f, -1f, 0f);
        Vector3f southDirection = new Vector3f(0f, 1f, 0f);
        Vector3f eastDirection = new Vector3f(-1f, 0f, 0f);
        Vector3f westDirection = new Vector3f(1f, 0f, 0f);

        ColorRGBA lightColor = ColorRGBA.White.mult(0.8f);

        DirectionalLight dlNorth = new DirectionalLight();
        dlNorth.setColor(lightColor);
        dlNorth.setDirection(northDirection.normalizeLocal());
        rootNode.addLight(dlNorth);

        DirectionalLight dlSouth = new DirectionalLight();
        dlSouth.setColor(lightColor);
        dlSouth.setDirection(southDirection.normalizeLocal());
        rootNode.addLight(dlSouth);

        DirectionalLight dlEast = new DirectionalLight();
        dlEast.setColor(lightColor);
        dlEast.setDirection(eastDirection.normalizeLocal());
        rootNode.addLight(dlEast);

        DirectionalLight dlWest = new DirectionalLight();
        dlWest.setColor(lightColor);
        dlWest.setDirection(westDirection.normalizeLocal());
        rootNode.addLight(dlWest);
    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("TogglePerspective", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, "Left", "Right", "Up", "Down", "Jump", "TogglePerspective");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Left")) {
            left = isPressed;
        } else if (name.equals("Right")) {
            right = isPressed;
        } else if (name.equals("Up")) {
            up = isPressed;
        } else if (name.equals("Down")) {
            down = isPressed;
        } else if (name.equals("Jump")) {
            player.jump();
        } else if (name.equals("TogglePerspective") && isPressed) {
            if (topView) {
                topView = false;
                frontView = true;
                sideView = false;
                camDir.set(0, 0, -1);
                camLeft.set(0, 1, 0);
            } else if (frontView) {
                topView = false;
                frontView = false;
                sideView = true;
                camDir.set(-1, 0, 0);
                camLeft.set(0, 1, 0);
            } else if (sideView) {
                topView = true;
                frontView = false;
                sideView = false;
                camDir.set(0, 0, -1);
                camLeft.set(-1, 0, 0);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f * walkSpeedFactor);
        camLeft.set(cam.getLeft()).multLocal(0.4f * walkSpeedFactor);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
}
