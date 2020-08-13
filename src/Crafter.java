import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jogamp.openal.sound3d.Buffer;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

public class Crafter extends SimpleApplication {
    public static void main(String[] args) throws IOException {
        //self app creation
        Crafter app = new Crafter();

        //disable annoying jmonkey settings menu
        app.showSettings = false;
        AppSettings appSettings = new AppSettings(true);
        appSettings.setResizable(true);

        //window sizing
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        appSettings.setWidth(d.width/2);
        appSettings.setHeight(d.height/2);

        //window title
        appSettings.setTitle("Crafter");

        app.setDisplayFps(true);

        //set window image
        BufferedImage icon = ImageIO.read(new File(System.getProperty("user.dir") + "\\texture\\icon.png"));
        appSettings.setIcons(new BufferedImage[]{icon});

        //apply settings
        app.setSettings(appSettings);

        //begin app
        app.start();
    }


    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(100);

        this.assetManager.registerLocator("texture/", FileLocator.class); // default

        rootNode.setCullHint(Spatial.CullHint.Never);

        //used statically. Object initialized here for loading textures
        Loader textureLoader = new Loader(assetManager);
    }

    private static int renderDistance = 20;

    private int x = -renderDistance;
    private int z = -renderDistance;
    private int counter = 0;
    private boolean genned = false;

    Chunk chunk;

    public static int getRenderDistance() {
        return renderDistance;
    }

    @Override
    public void simpleUpdate(float tpf){

        //this is for warming up vm then gen 1 chunk
//        if (counter > 5 && !genned) {
//            long startTime = System.currentTimeMillis();
//            Chunk chunk = new Chunk();
//            ChunkData.storeChunk(x,z, chunk);
//
//            Geometry geo = ChunkMesh.genChunkMesh(chunk, assetManager, x, z);
//            rootNode.attachChild(geo);
//            genned = true;
//            long endTime = System.currentTimeMillis();
//            double timeElapsed = (double)(endTime - startTime)/1000;
//            System.out.println("Chunk init time: " + timeElapsed + " seconds");
//        } else if (!genned){
//            System.out.println(counter);
//            counter++;
//        }

        //this is for dynamic chunk generation
        counter++;
        if (counter > 5 && z <= renderDistance) {
//            long startTime = System.currentTimeMillis();

            chunk = new Chunk();
            ChunkData.storeChunk(x,z, chunk);
            ChunkMesh.genChunkMesh(chunk, assetManager, x, z, rootNode,false);

//            long endTime = System.currentTimeMillis();
//            double timeElapsed = (double) (endTime - startTime) / 1000;
//            System.out.println("Chunk init time: " + timeElapsed + " seconds");

            //ChunkMesh.updateNeighbors(chunk, assetManager, x, z, rootNode);

            //rootNode.detachChildNamed()

            counter = 0;

            x++;
            if (x > renderDistance) {
                x = -renderDistance;
                z++;
            }
        }
//        } else if(z == renderDistance){
//            x  = z = -renderDistance;
//
//            counter = 0;
//
//            rootNode.detachAllChildren();
//        }

    }


}
