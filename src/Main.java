import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener, FrameData {

    //BASIC VARIABLES
    private final int WIDTH=1180, HEIGHT=(int)(WIDTH*1f);
    ArrayList<Object> objects=new ArrayList<>();
    float rad=-500f;
    Vec3f pos=new Vec3f(-rad,0,0);
    Vec2f orient=new Vec2f(0,0);
    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;
    boolean gravon=false;
    Object frame=new Object(2,null,null,0);
    float graconstant=2;
    boolean magon=false;
    //COLORS
    Color background=new Color(255, 255, 255);
    float rld=((WIDTH/2f)/2f);
    String fov="";

    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        //objects.add(new Object(0,new Vec3f(50,0,20),new Vec3f(0,0,0)));
        //objects.add(new Object(0,new Vec3f(50,20,0),new Vec3f(0,0,0)));
        float hd=(float)(2*(180f/3.1415f)*Math.atan((HEIGHT/2f)/rld));
        float wd=(float)(2*(180f/3.1415f)*Math.atan((WIDTH/2f)/rld));
        fov="fov : "+hd+", "+wd;
        thread=new Thread(this);
        thread.start();
    }

    public void addRandParticle(float velmin, float velmax,float rad){
        Vec3f loc=new Vec3f((float)(Math.random()*.95*BOUNDS[0]-(.475*(BOUNDS[0]))),(float)(Math.random()*.95*BOUNDS[1]-(.475*(BOUNDS[1]))),(float)(Math.random()*.95*BOUNDS[2]-(.475*(BOUNDS[2]))));
        Vec3f vel=new Vec3f((float)((velmax-velmin)*Math.random()+velmin),(float)((velmax-velmin)*Math.random()+velmin),(float)((velmax-velmin)*Math.random()+velmin));
        objects.add(new Object(0,loc,vel,rad));
    }

    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(Color.BLACK);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        int pSize=3;
        int pw=WIDTH/pSize;
        int ph=HEIGHT/pSize;
        //float fovx=3.14f*2/3;
        //float fovy=3.14f/2;
        int cx=pw/2;
        int cy=ph/2;
        //int lensdist=cy;
        //System.out.println("fov : "+hd+", "+wd);


        /*for (int x=0; x<pw; x++){
            float xor=(float)(Math.atan2(x-cx,lensdist));
            for(int y=0; y<ph; y++){
                float yor=(float)(Math.atan2(y-cy,lensdist));
                gfx.setColor(getColorInDir(xor,yor));
                gfx.fillRect(pSize*x,pSize*y,pSize,pSize);
            }
        }*/
        gfx.setColor(Color.BLUE);
        int sx=50, sy=50;
        gfx.drawString(fov,sx,sy);
        gfx.drawString((int)(orient.x*180/3.14f)+", "+(int)(orient.y*180/3.14f),sx,sy+30);
        gfx.drawString("rad = "+(int)(rad),sx,sy+60);

        //gfx.drawString(frame.getType(),sx,sy+90);
        sortObjects();
        Vec2f or1=new Vec2f(orient.x,0);//TODO REMOVE THIS LATER
        or1=new Vec2f(orient.x,0);//TODO REMOVE THIS LATER
        for (Object o : objects){
            o.render(gfx,WIDTH,HEIGHT,rld, pos,or1,orient.y);
        }
        gfx.setColor(Color.BLACK);
        frame.render(gfx,WIDTH,HEIGHT,rld, pos,or1,orient.y);
        String msg="particles : "+objects.size();
        gfx.setColor(Color.BLACK);
        gfx.setFont(gfx.getFont().deriveFont(30f));
        gfx.drawString(msg,20,20);
        //FINAL
        g.drawImage(img,0,0,this);
    }


    public void addRandCluster(float velmin, float velmax,float rad, int num, float sr){
        Vec3f loc=new Vec3f((float)(Math.random()*.95*BOUNDS[0]-(.475*(BOUNDS[0]))),(float)(Math.random()*.95*BOUNDS[1]-(.475*(BOUNDS[1]))),(float)(Math.random()*.95*BOUNDS[2]-(.475*(BOUNDS[2]))));
        Vec3f vel=new Vec3f((float)((velmax-velmin)*Math.random()+velmin),(float)((velmax-velmin)*Math.random()+velmin),(float)((velmax-velmin)*Math.random()+velmin));
        for (int i=0; i<num; i++){
            Vec3f p=new Vec3f(loc.x-(rad)+(float)(rad*2*Math.random()),loc.y-(rad)+(float)(rad*2*Math.random()),loc.z-(rad)+(float)(rad*2*Math.random()));
            Vec3f v=new Vec3f(vel.x*(float)(.5+Math.random()),vel.y*(float)(.5+Math.random()),vel.z*(float)(.5+Math.random()));
            Object o=new Object(0,p,v,sr);
            objects.add(o);

        }
    }

    public void sortObjects(){
        ArrayList<Object> o1=new ArrayList<>();
        float[] dists=new float[objects.size()];
        int i1=0;
        for (Object o: objects){
            Vec3f vd=new Vec3f(pos.x-o.loc.x,pos.y-o.loc.y,pos.z-o.loc.z);
            dists[i1]=vd.length();
            i1++;
        }
        ArrayList<Integer> s=new ArrayList<>();
        for (int i=0; i<dists.length; i++){
            if(s.size()==0){s.add(i);}else {
                int addin = s.size() - 1;
                for (Integer f : s) {
                    if (dists[f] > dists[i]) {
                        addin=s.indexOf(f);
                        break;
                    }
                }
                s.add(addin,i);
            }
        }
        for (Integer i:s){
            o1.add(0,objects.get(i));
        }
        objects=o1;
    }

    public Color getColorInDir(float xor,float yor){
        Vec2f o1=new Vec2f(orient.x+xor,orient.y+yor);
        for (int i=0; i<objects.size(); i++){
            Object o = objects.get(i);
            Color c=o.doesLineCross(o1,pos);
            if (c!=null){
                return c;
            }
        }
        return Color.WHITE;
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME
            Vec2f field=new Vec2f(0,3.1415f/2);
            //Vec3f f=new Vec3f();
            //UPDATES
            for (int i=0; i<objects.size(); i++){
                Object o = objects.get(i);
                o.update(.03f,objects);
                if(magon){o.applyField(field,.03f);}
                for (int z=0; z<objects.size(); z++){
                    if(i==z||!objects.contains(o)){continue;}
                    Object o1 = objects.get(z);
                    //if(objects.indexOf(o)==objects.indexOf(o1)){continue;}
                    try {
                        if (gravon) {
                            o.attractTo(o1.loc, graconstant * o1.getVolume());
                        }
                    }catch (NullPointerException e){
                        System.out.println("ERROR");
                    }
                    //if(magon){o.magnetize(magc,o1);}
                }
            }

            repaint();//UPDATES FRAME
            try{ Thread.sleep(30); } //ADDS TIME BETWEEN FRAMES (FPS)
            catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    public void rotateAround(float xor, float yor){
        boolean up=(orient.y==yor);
        float r=rad;
        //System.out.println(pos+", "+orient);
        orient.x=xor;
        orient.y=yor;
        yor=0;
        if (orient.x>6.28){orient.x-=6.28f;}else if (orient.x<-6.28f){orient.x+=6.28f;}
        if (orient.y>6.28){orient.y-=6.28f;}else if (orient.y<-6.28f){orient.y+=6.28f;}
        if (up) {
            //System.out.println("not u[");
            //orient.y=0;
            float r1 = r * (float) (Math.cos(-yor));
            pos.z = 0;//r * (float) (Math.sin(yor));
            pos.x = -r1 * (float) (Math.cos(-xor));
            pos.y = r1 * (float) (Math.sin(-xor));
        }
        //System.out.println(pos+", "+orient);
        //System.out.println("->");
        //printPosData();
        //printPosData();


    }

    public void printPosData(){
        System.out.println("");
        String s="pos["+(int)pos.x+", "+(int)pos.y+", "+(int)pos.z+"] o["+(int)(orient.x*180/3.1415)+", "+(int)(orient.y*180/3.1415)+"]";
        System.out.println(s);
    }

    //INPUT
    public void keyPressed(KeyEvent e) {
        float rad1=3f;
        if (e.getKeyCode()==KeyEvent.VK_RIGHT){
            rotateAround(orient.x+.05f,orient.y);
        }else if (e.getKeyCode()==KeyEvent.VK_LEFT){
            rotateAround(orient.x-.05f,orient.y);
        }if (e.getKeyCode()==KeyEvent.VK_UP){
            //orient.y+=.2;
            rotateAround(orient.x,orient.y+.05f);
        }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
            //orient.y-=.2;
            rotateAround(orient.x,orient.y-.05f);
        }else if (e.getKeyCode()==KeyEvent.VK_EQUALS){
            rad*=.9f;
            rotateAround(orient.x,orient.y);

        }else if (e.getKeyCode()==KeyEvent.VK_MINUS){
            rad*=1.1f;
            rotateAround(orient.x,orient.y);
        }else if(e.getKeyCode()==KeyEvent.VK_SPACE){
            addRandParticle(-10,10,rad1);
        }else if(e.getKeyCode()==KeyEvent.VK_C){
            addRandCluster(-10,10,80,40,rad1);
        }else if(e.getKeyCode()==KeyEvent.VK_T){
            for (int i=0; i<50; i++){ addRandParticle(-10,10,rad1);}
        }else if(e.getKeyCode()==KeyEvent.VK_B){
            addRandParticle(-10,10,10);
        }else if(e.getKeyCode()==KeyEvent.VK_N){
            addRandParticle(0,0,10);
        }else if (e.getKeyCode()==KeyEvent.VK_Y){

            pos=new Vec3f(0,0,rad);
            orient=new Vec2f(0,3.14159f/2f);
        }else if (e.getKeyCode()==KeyEvent.VK_A){

            pos=new Vec3f(0,0,rad);
            orient=new Vec2f(0,3.14159f/2f);
        }else if (e.getKeyCode()==KeyEvent.VK_V){

            pos=new Vec3f(-rad,0,0);
            orient=new Vec2f(0,0);
        }
        if(e.getKeyCode()==KeyEvent.VK_L) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x*=.7f;
                o.vel.y*=.7f;
                o.vel.z*=.7f;
            }
        }if(e.getKeyCode()==KeyEvent.VK_F) {
            for (int i=0; i<objects.size(); i++){
                Object o=objects.get(i);
                o.vel.x*=1.2f;
                o.vel.y*=1.2f;
                o.vel.z*=1.2f;
            }
        }if (e.getKeyCode()==KeyEvent.VK_R){
            if (objects.size()>0){
                objects.remove(0);
            }
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_G){
            gravon=!gravon;
            System.out.println("grav = "+gravon);
        }if (e.getKeyCode()==KeyEvent.VK_M){
            magon=!magon;
            System.out.println("mag = "+magon);
            //System.out.println("grav = "+gravon);
        }
    }
    public void keyTyped(KeyEvent e) { }

    //QUICK METHOD I MADE TO DISPLAY A COORDINATE GRID

}