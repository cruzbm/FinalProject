import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
/**
 * Remake of Space Invaders
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ProjectMain extends Applet implements Runnable, MouseMotionListener, MouseListener
{
    Thread gameThread;
    int width = 400, height = 400, MAX = 1;
    int currentX[] = new int[MAX];
    int currentY[] = new int[MAX];
    int directionX[] = new int[MAX];
    int directionY[] = new int[MAX];
    int speed = 10;
    BufferedImage bufImg;
    Graphics2D bufImgSurface;
    boolean collided = false;
    float dist;
    int step = 0; //Number of movements left/right
    int direction = 1; //Current left/right direction(0=left,1=right);
    int shipX = width/2-10; //Current player X Position
    int shipY = height-45; //Current player Y position
    int mbx =-10; //The mouse position after mouse down sets the
    int mby=-10; //enemy bullet position to this.
    int randomShoot = 0; //Used to work out which enemy is shooting
    int health = 50; //The players health
    int BNUM = 10; //Number of bullets
    int playing = 0; //Are is the game playing (0=Playing, 1=paused, 2=game over, 3= win)

    int bX[] = new int[BNUM]; //bullet x pos
    int bY[] = new int[BNUM]; //bullet y pos

    int ebX[] = new int[BNUM]; //Enemy bullet x pos
    int ebY[] = new int[BNUM]; //Enemy bullet Y pos

    float fps = 0;
    public void start() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void init() {
        currentX[0] = 100;
        currentY[0] = 0;
        directionX[0] = 1;
        directionY[0] = 0;
        bufImg = (BufferedImage)createImage(width,height);
        bufImgSurface = bufImg.createGraphics();
        currentX[1]=0;
        currentY[1]=100;
        directionX[1]=0;
        directionY[1]=1;
        int row = 10;
        int col = 10;
        int count = 0;
        currentX[0]=col;
        currentY[0]=row;

        for(int i = 0; i <50; i++) {
            count++;
            currentX[i]=col;
            col+=25;

            currentY[i] = row;

            if (count == 10) {
                row+=25;
                col=10;
                count=0;
            }
        }

        addMouseMotionListener(this);
        addMouseListener(this);

        for(int i = 0; i<BNUM; i++) {
            bX[i]=-10;
            bY[i]=-10;
            ebX[i]=0;
            ebY[i]=height+10;
        }
    }

    public void paint(Graphics g) {
        update(g);
    }

    public void update(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Set the background color.
        bufImgSurface.setBackground(Color.BLACK);

        //Clear the applet.
        bufImgSurface.clearRect(0,0,width,height);

        //Set the drawing color to red.
        bufImgSurface.setColor(Color.RED);
  
        //(X pos, Y pos, Width, Height).
        for(int i = 0; i < MAX; i++){
         bufImgSurface.fillOval(currentX[1], currentY[1], 20, 20);
        }
        
        //Draw the read ship (a square)
        bufImgSurface.setColor(Color.GREEN);
        bufImgSurface.fillRect(shipX,shipY,20,20);
        
        for(int j = 0; j <BNUM; j++) {
            bufImgSurface.setColor(Color.YELLOW);
            bufImgSurface.fillOval(bX[j],bY[j],5,5);
            bufImgSurface.setColor(Color.blue);
            bufImgSurface.fillOval(ebX[j],ebY[j],5,10);
        }
        
        if(playing==1)
            bufImgSurface.drawString("PAUSED",width/2-10,390);
        else if(playing==2)
            bufImgSurface.drawString("*****GAME OVER*****",width/2-10,390);
        else if(playing==3)
            bufImgSurface.drawString("*****YOU WIN!*****",width/2-10,390);
            for(int i =0; i<health; i++)
                bufImgSurface.drawString(" I",(2*i),390);
            //Draw the buffered image to the screen.
        g2.drawImage(bufImg,0,0,this);

        //Display the current frame rate.
        g.drawString("FPS:"+fps,1,400);
        
        
        if(collided==true) {
            bufImgSurface.drawString("Collided",10,10);
        }
        
    }

    long start = 0;
    long tick_end_time;
    long tick_duration;
    long sleep_duration;

    static final int MIN_SLEEP_TIME = 10;
    static final int MAX_FPS = 20;
    static final int MAX_MS_PER_FRAME = 1000/MAX_FPS;


    public void run() {
        while (true) { //Starts the game loop
            start = System.currentTimeMillis(); //Sets the current time

            if(playing==0) { //Are we playing or is the game over?
                step++;
                for(int i =0; i<MAX; i++) {
                    if(step>15) {
                        if(direction==1) {
                            direction=0;
                        } else {
                            direction =1;
                        }
                        step = 0;
                        for(int j = 0; j<MAX; j++) 
                            currentY[j]+=speed;
                    }
                    if(direction==1)
                        currentX[i]+=speed;
                    else
                        currentX[i]-=speed;
                }
                for(int i = 0; i< BNUM; i++) {
                    if(bY[i]<=0){
                        bX[i]=mbx;
                        bY[i]=mby;
                        mbx=-10;
                        mby=-10;
                    }
                    bY[i]-=speed;
                }
                for(int i = 0; i< MAX; i++) {
                    for(int j =0; j< BNUM; j++) {
                        if(!(bY[j]<=0)){
                            dist = (int)(Math.sqrt(Math.pow((currentX[i]+10)-bX[j],2)+Math.pow((currentY[i]+10)-bY[j],2)));
                            if(dist <= 20) {
                                bY[j] =-50;
                                currentY[i]=-500;
                            }
                        }
                    }
                }
                for(int k = 0; k < MAX; k++) {
                    randomShoot=(int)(Math.random()*MAX);
                    if (currentY[randomShoot]>=0){
                        for(int i = 0; i< BNUM; i++){
                            if(ebY[i]>=height){
                                ebX[i]=currentX[randomShoot];
                                ebY[i]=currentY[randomShoot];
                                break;
                            }
                        }
                    }
                }
                for(int j=0; j< BNUM; j++){
                    if(!(ebY[j]>=height)){
                        dist = (int)(Math.sqrt(Math.pow((shipX+10)-ebX[j],2)+Math.pow((shipY+10)-ebY[j],2)));
                        if(dist<=20){
                            ebY[j]=height+10;
                            health-=10;
                        }
                    }
                }
                for(int i = 0; i< BNUM; i++){
                    if(ebY[i]<height){
                        ebY[i]+=speed;
                    }
                }
                for(int i=0; i< BNUM; i++){
                    if(ebY[i]<height){
                        ebY[i]+=speed;
                    }
                }
                if(health<=0){
                    playing = 2;                
                    int count = 0;
                    for(int j = 0; j < MAX; j++) {
                        if(currentY[j]<0)
                            count++;
                        if(currentY[j]>=340)
                            playing=2;
                    }
                    if(count==MAX)
                        playing=3;
                }
                else{}
                repaint();  //Redraw the screen

                tick_end_time=System.currentTimeMillis();
                tick_duration=tick_end_time-start;
                sleep_duration=MAX_MS_PER_FRAME-tick_duration;

                if(sleep_duration<MIN_SLEEP_TIME){
                    sleep_duration=MIN_SLEEP_TIME;
                }
                fps = 1000 / (sleep_duration+tick_duration);

                try{
                    Thread.sleep(sleep_duration);
                } catch(InterruptedException e){}
            }
        }
    }
    
    public void mouseMoved(MouseEvent e) {shipX=e.getX()-5;}
    
    @Override
    public void mouseDragged(MouseEvent e) {shipX=e.getX()-5;}
    
    public void mouseClicked(MouseEvent e) {
        mbx=e.getX();
        mby=shipY;
    }
    
    public void mousePressed(MouseEvent e) {
        mbx=e.getX();
        mby=shipY;
    }
    
    public void mouseEntered(MouseEvent e) {
        playing = 0;
    }
    
    public void mouseExited(MouseEvent e) {
        playing =1;
    }
    
    public void mouseReleased(MouseEvent e) {}
    
    
}