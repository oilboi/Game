import com.google.gson.stream.JsonToken;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

//ray object
public class Ray {
    private final static int renderDistance = Crafter.getRenderDistance();
    private Vector3f pos;
    private Vector3f dir;
    private float length;
    private Vector3f cachePos;
    private Vector3f newPos;
    private Vector3f finalPos;
    private Vector3f lastPos;
    public Ray(Vector3f position, Vector3f direction, float l){
        pos = position;
        dir = direction;
        length = l;
    }

    public void rayCast(Node rootNode, AssetManager assetManager){
        float step = 0;
        float precision = 0.01f;
        boolean solved = false;
        finalPos = null;

        while(!solved && step < length) {
            cachePos = new Vector3f(dir.x * step, dir.y * step, dir.z * step);
            newPos = new Vector3f(FastMath.floor(pos.x + cachePos.x), FastMath.floor(pos.y + cachePos.y), FastMath.floor(pos.z + cachePos.z));

            if (detectBlock(newPos)){
                solved = true;
                finalPos = newPos;
                break;
            }
            step += precision;

            lastPos = newPos.clone();
        }

        //System.out.println(finalPos);
        if(finalPos != null) {
            rootNode.getChild("selector").setLocalTranslation(finalPos.x+0.5f, finalPos.y+0.5f, finalPos.z+0.5f);
            if(Player.getMining()) {
                short thisBlock = destroyBlock(finalPos, assetManager, rootNode);
                new ItemEntity(new Vector3f(finalPos.x+0.5f, finalPos.y+0.5f, finalPos.z+0.5f), new Vector3f((FastMath.nextRandomFloat()-0.5f)*10,FastMath.nextRandomFloat()*10f,(FastMath.nextRandomFloat()-0.5f)*10), thisBlock, rootNode);
            } else if (Player.getPlacing() && lastPos != null){
                placeBlock(lastPos, assetManager, rootNode, Player.getSelectedItem());
            }
        } else {
            rootNode.getChild("selector").setLocalTranslation(0, -1000f, 0);
        }
    }

    private static boolean detectBlock(Vector3f flooredPos){
        int[] current = new int[2];

        current[0] = (int)(FastMath.floor(flooredPos.x / 16f));
        current[1] = (int)(FastMath.floor(flooredPos.z / 16f));

        Vector3f realPos = new Vector3f(flooredPos.x - (16*current[0]), flooredPos.y, flooredPos.z - (16*current[1]));

        return ChunkData.getBlock((int)realPos.x, (int)realPos.y, (int)realPos.z, current[0]+renderDistance, current[1]+renderDistance) != 0;
    }

    private static short destroyBlock(Vector3f flooredPos, AssetManager assetManager, Node rootNode){
        int[] current = new int[2];
        current[0] = (int)(FastMath.floor(flooredPos.x / 16f));
        current[1] = (int)(FastMath.floor(flooredPos.z / 16f));
        Vector3f realPos = new Vector3f(flooredPos.x - (16*current[0]), flooredPos.y, flooredPos.z - (16*current[1]));

        short thisBlock = ChunkData.getBlock((int)realPos.x, (int)realPos.y, (int)realPos.z, current[0]+renderDistance, current[1]+renderDistance);

        ChunkData.setBlock((int)realPos.x, (int)realPos.y, (int)realPos.z, current[0]+renderDistance, current[1]+renderDistance, (short) 0);

        Chunk chunk = ChunkData.getChunk(current[0],current[1]);
        ChunkMesh.genChunkMesh(chunk, assetManager, current[0],current[1], rootNode, false);
        return thisBlock;
    }
    private static void placeBlock(Vector3f flooredPos, AssetManager assetManager, Node rootNode, short selectedItem){
        int[] current = new int[2];
        current[0] = (int)(FastMath.floor(flooredPos.x / 16f));
        current[1] = (int)(FastMath.floor(flooredPos.z / 16f));

        Vector3f realPos = new Vector3f(flooredPos.x - (16*current[0]), flooredPos.y, flooredPos.z - (16*current[1]));

        CustomAABB us = new CustomAABB(Player.getPos().x, Player.getPos().y+0.1f, Player.getPos().z, Player.getWidth(), Player.getHeight());
        CustomBlockBox theBlock = new CustomBlockBox((int) flooredPos.x, (int) flooredPos.y, (int) flooredPos.z);
        if(!Collision.wouldCollide(us, theBlock)) {
            ChunkData.setBlock((int) realPos.x, (int) realPos.y, (int) realPos.z, current[0] + renderDistance, current[1] + renderDistance, selectedItem);
            Chunk chunk = ChunkData.getChunk(current[0], current[1]);
            ChunkMesh.genChunkMesh(chunk, assetManager, current[0], current[1], rootNode, false);
        }
    }
}
