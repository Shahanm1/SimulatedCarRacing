/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hg.controller;

import scr.Action;
import scr.Controller;
import scr.SensorModel;

import java.awt.AWTEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 * @author Jan Quadflieg
 */
public class HumanController extends Controller implements
KeyListener {
	
	

/*
	class MyKeyHandle extends JFrame implements KeyListener{
	    private JTextArea text=new JTextArea();
	    public MyKeyHandle(){
	        super.setTitle("Welcome！");
	        JScrollPane scr=new JScrollPane(text);
	        scr.setBounds(5, 5, 300, 200);
	        super.add(scr);
	        text.addKeyListener(this);
	        super.setSize(310,210);
	        super.setVisible(true);
	        super.addWindowListener(new WindowAdapter(){
	            public void windowClosing(WindowEvent arg0){
	                System.exit(1);
	            }
	        });
	    }
	    @Override
	    public void keyTyped(KeyEvent e) {
	        text.append("输入的内容是:"+e.getKeyChar()+"\n");
	        acc= 0.1;
	        
	    }
	    @Override
	    public void keyPressed(KeyEvent e) {
	        text.append("键盘“"+KeyEvent.getKeyText(e.getKeyCode())+"”键按下\n"); 
	        
	    }
	    @Override
	    public void keyReleased(KeyEvent e) {
	        // TODO Auto-generated method stub
	        text.append("键盘“"+KeyEvent.getKeyText(e.getKeyCode())+"”键松开\n");
	    }
	}
	*/
	
	class KeyDemo extends JFrame{
		 JButton jb = new JButton();
		 public KeyDemo(){
		  this.addKeyListener(new MyListener());
		  jb.addKeyListener(new MyListener());
		  this.add(jb);
		  this.setSize(200,200);
		  this.setVisible(true);
		 }
		 public class MyListener extends KeyAdapter{
		  public void keyPressed(KeyEvent event) {
		 
			KeyEvent keyEvent = (KeyEvent) event;
			int keyid = keyEvent.getKeyCode();
			
			if (keyid == KeyEvent.VK_UP) {
				acc = 1;
				brake = 0;
			}

			if (keyid == KeyEvent.VK_DOWN) {
				acc = 0;
				brake =1;
			}
			if (keyid == KeyEvent.VK_LEFT) {
				steering = -0.5;
			}

			if (keyid == KeyEvent.VK_RIGHT) {
				steering = 0.5;
			}
//		System.out.println(acc);
		   
		  }
		 }
		
		}

//	public KeyBoardInput keyBoardInput;

	double acc = 0.5;
	double brake =0;
	double clutch = 0;
	double steering = 0.1;
	int gear = 4;
//	JFrame jf ;
	public HumanController() {
//		this.keyBoardInput = new KeyBoardInput();
//		jf = new JFrame("COBOSTAR Monitor: Driver's View");
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		jf.addw
//		jf.setVisible(true);
		
		new KeyDemo();
		
	}

	public Action control(SensorModel m) {
		Action action = new Action();

		double[] focus = m.getFocusSensors();
		for(int i=0; i < focus.length ;i++){
			System.out.print(focus[i] + "  ");
		}
		System.out.println();
		// if(keyBoardInput.getKey() == KeyEvent.VK_UP){
		// action.accelerate = 0.1;
		// }
		//
		// if(keyBoardInput.getKey() == KeyEvent.VK_DOWN){
		// action.accelerate = 0;
		// }
		action.accelerate = acc;
		action.brake = brake;
		action.clutch = clutch;
		
		action.steering = steering;
		
		action.gear = gear;
		System.out.println(action.accelerate);
		double[] edge = m.getTrackEdgeSensors();
		for(int i=0;i < edge.length; i++){
			System.out.print(edge[i] + "  ");
		}
		System.out.println();
		return action;

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("sss");
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		
			KeyEvent keyEvent = (KeyEvent) event;
			int keyid = keyEvent.getKeyCode();
			System.out.println(keyid);
			if (keyid == KeyEvent.VK_UP) {
				acc = 1;
			}

			if (keyid == KeyEvent.VK_DOWN) {
				acc = 0;
			}
		
	}
	
	public void add(){
		
		
	}
}
