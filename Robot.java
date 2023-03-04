package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Servo;
import java.time.Clock;

import edu.wpi.first.cameraserver.*;

//import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DigitalInput;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //decalring intigers (PWM ports) for motor controllers
  private static final int frontLeftWheel = 1;   // setting up wheel motors to their PMW ports on the RobotRIO
  private static final int backLeftWheel = 0;
  private static final int frontRightWheel = 2;
  private static final int backRightWheel = 3;
  private static final int intake= 6; //For the claw system. or intake?
  private static final int elevator = 7;//For the elevator
  private static final int arm = 4;//For the first motor of the arm
  private static final int slam = 5;//This is for flipping the cones
  private static final int arm2 = 8;//This is for the second motor of the arm

  //declaring integers (DIO ports) for sensors
  private static final int HALT = 0;
  private static final int CEASE = 1;

  //Servos
  Servo servo1 = new Servo(9);
  Servo servo2 = new Servo(10);
  //int replay = 0;
 
  double output;
  private double startTime;

  private final I2C.Port i2cPort = I2C.Port.kOnboard;

  private static final int gamer = 0; //sets up joystick to connect to usb port 1 on the laptop/computer

  //private static final double USonHoldDist = 12.0; //ultrasonic sensor limited before it stops the robot(in inches)
  //private static final double MathValToDist = 0.125; //set value used to convert sensor values to inches
  //private static final int UsonPort = 0; //ultrasonic analog port (aka Analog In on the RobotRIO)

  //private final AnalogInput AUsonIn = new AnalogInput(UsonPort); //gives the ultrasonic sensor a name
  private DifferentialDrive mecaTibbs; //gives the drive train a name
  private Joystick gStick;  //gives the joystick a name

  MotorController m_frontLeft = new PWMVictorSPX(frontLeftWheel);//2
  MotorController m_backLeft = new PWMVictorSPX(backLeftWheel);//3 M,KL
  MotorControllerGroup m_left = new MotorControllerGroup(m_frontLeft, m_backLeft);
  MotorController m_frontRight = new PWMVictorSPX(frontRightWheel);//1
  MotorController m_backRight = new PWMVictorSPX(backRightWheel);//0
  MotorControllerGroup m_right = new MotorControllerGroup(m_frontRight, m_backRight);
   
  PWMSparkMax  sucker = new PWMSparkMax(intake); //6
  PWMSparkMax hammerTime = new PWMSparkMax(slam); //5
  PWMSparkMax  limb = new PWMSparkMax(arm);//4
  PWMSparkMax elevatorMusic = new PWMSparkMax(elevator);//7
  PWMSparkMax limb2 = new PWMSparkMax(arm2);//8
  
  
  
  DigitalInput DomGoth = new DigitalInput(HALT);//not important for now
  DigitalInput SubFemboy= new DigitalInput(CEASE);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Forward only", kDefaultAuto);
    m_chooser.addOption("Back shooter", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    CameraServer.startAutomaticCapture().setResolution(240,180);

    m_frontRight.setInverted(false);
    m_backRight.setInverted(false);
    m_frontLeft.setInverted(false); //flips the left side of motors for wheels
    m_backLeft.setInverted(false);//false cause... not needed this year
 
    mecaTibbs = new DifferentialDrive( m_left, m_right); //hooks up the drive train with the PMW motors
                                                 
    //that are linked to the wheels
      mecaTibbs.setExpiration(0.1);                           
    gStick = new Joystick(gamer); //hooks up joysick to the usb port that is connected to the joystick
    
  
  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  }
  @Override
  public void robotPeriodic(){
    mecaTibbs.setSafetyEnabled(true);}

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the  auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    startTime = Timer.getFPGATimestamp();

     
  }

  /*
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    double time = Timer.getFPGATimestamp();
    
    //This segment of code is for the center starting position
    //Comment out the other 2 sections if you want to use this position code

             if(time - startTime < 1){//Moving to payload dropoff quickly with arm2 moving up
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);

              limb2.set(0.3);
            }
            else if((time - startTime >= 1  && time - startTime < 2)){ //Moving to payload dropoff slowly with arm2 slowly going up and arm1 going down
              m_frontLeft.set(0.2);
              m_frontRight.set(0.2);
              m_backLeft.set(0.2);
              m_backRight.set(0.2);
              
              limb.set(-0.3);
              limb2.set(0.1);


            } else if(time-startTime >=2 && time-startTime < 2.25){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              
              limb.set(0);
              limb2.set(0);
            }  else if(time-startTime >=2.25 && time-startTime<2.5){
            sucker.set(-0.3);
            }
            else if(time - startTime >= 2.5  && time - startTime < 3.5){//Moving backwards
             sucker.set(0);
              m_frontLeft.set(-0.2);
              m_frontRight.set(-0.2);
              m_backLeft.set(-0.2);
              m_backRight.set(-0.2);
              
              limb.set(0.3);
              limb2.set(-0.1);
            } 
            else if(time - startTime >= 3.5  && time - startTime < 4.5){//Moving onto ramp (slowly)
              m_frontLeft.set(-0.3);
              m_frontRight.set(-0.3);
              m_backLeft.set(-0.3);
              m_backRight.set(-0.3);

              limb.set(0);
              limb2.set(-0.3);
             }
             else if(time - startTime >= 4.5  && time - startTime < 6.5){//Adjusting on the ramp
              limb2.set(0);
              m_frontLeft.set(-0.1);
              m_frontRight.set(-0.1);
              m_backLeft.set(-0.1);
              m_backRight.set(-0.1);
             }
             else if(time - startTime >= 6.5 && time-startTime < 9.5){//Stopping on the ramp
              m_frontLeft.set(0.0);
              m_frontRight.set(0.0);
              m_backLeft.set(0.0);
              m_backRight.set(0.0);
             
             }
           
            else if(time- startTime >= 9.5){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              servo1.set(90);
              servo2.set(90);
            }

            //This is the left position (relative to the alliance area)
            if(time - startTime < 1){//Moving to payload dropoff quickly with arm2 moving up
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);

              limb2.set(0.3);
            }
            else if((time - startTime >= 1  && time - startTime < 2)){ //Moving to payload dropoff slowly with arm2 slowly going up and arm1 going down
              m_frontLeft.set(0.2);
              m_frontRight.set(0.2);
              m_backLeft.set(0.2);
              m_backRight.set(0.2);
              
              limb.set(-0.3);
              limb2.set(0.1);


            } else if(time-startTime >=2 && time-startTime < 2.25){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              
              limb.set(0);
              limb2.set(0);
            }  else if(time-startTime >=2.25 && time-startTime<2.5){
            sucker.set(-0.3);
            }
            else if(time - startTime >= 2.5  && time - startTime < 3.5){//Moving backwards
             sucker.set(0);
              m_frontLeft.set(-0.2);
              m_frontRight.set(-0.2);
              m_backLeft.set(-0.2);
              m_backRight.set(-0.2);
              
              limb.set(0.3);
              limb2.set(-0.1);
            } 
            else if(time - startTime >= 3.5  && time - startTime < 4.5){//Moving onto ramp (slowly)
              m_frontLeft.set(-0.3);
              m_frontRight.set(-0.3);
              m_backLeft.set(-0.3);
              m_backRight.set(-0.3);

              limb.set(0);
              limb2.set(-0.3);
             }
             else if(time-startTime >=4.5 && time-startTime< 5.5){
              m_frontRight.set(0.3);
              m_backRight.set(0.3);
              m_frontLeft.set(0);
              m_backLeft.set(0);
             }
             else if(time-startTime >=5.5 && time-startTime< 6.5){
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);
             }
             else if(time-startTime >=6.5 && time-startTime< 7.5){
              m_frontRight.set(0.3);
              m_backRight.set(0.3);
              m_frontLeft.set(0);
              m_backLeft.set(0);
             }
             else if(time-startTime >=7.5 && time-startTime< 8.5){
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);
             }
             else if(time-startTime >=8.5 && time-startTime< 9.5){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              servo1.set(90);
              servo2.set(90);
             }
                
            //This is for the right position (relative to the alliance area)
            if(time - startTime < 1){//Moving to payload dropoff quickly with arm2 moving up
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);

              limb2.set(0.3);
            }
            else if((time - startTime >= 1  && time - startTime < 2)){ //Moving to payload dropoff slowly with arm2 slowly going up and arm1 going down
              m_frontLeft.set(0.2);
              m_frontRight.set(0.2);
              m_backLeft.set(0.2);
              m_backRight.set(0.2);
              
              limb.set(-0.3);
              limb2.set(0.1);


            } else if(time-startTime >=2 && time-startTime < 2.25){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              
              limb.set(0);
              limb2.set(0);
            }  else if(time-startTime >=2.25 && time-startTime<2.5){
            sucker.set(-0.3);
            }
            else if(time - startTime >= 2.5  && time - startTime < 3.5){//Moving backwards
             sucker.set(0);
              m_frontLeft.set(-0.2);
              m_frontRight.set(-0.2);
              m_backLeft.set(-0.2);
              m_backRight.set(-0.2);
              
              limb.set(0.3);
              limb2.set(-0.1);
            } 
            else if(time - startTime >= 3.5  && time - startTime < 4.5){//Moving onto ramp (slowly)
              m_frontLeft.set(-0.3);
              m_frontRight.set(-0.3);
              m_backLeft.set(-0.3);
              m_backRight.set(-0.3);

              limb.set(0);
              limb2.set(-0.3);
             }
             else if(time-startTime >=4.5 && time-startTime< 5.5){
              m_frontLeft.set(0.3);
              m_backLeft.set(0.3);
              m_frontRight.set(0);
              m_backRight.set(0);
             }
             else if(time-startTime >=5.5 && time-startTime< 6.5){
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);
             }
             else if(time-startTime >=6.5 && time-startTime< 7.5){
              m_frontLeft.set(0.3);
              m_backLeft.set(0.3);
              m_frontRight.set(0);
              m_backRight.set(0);
             }
             else if(time-startTime >=7.5 && time-startTime< 8.5){
              m_frontLeft.set(0.3);
              m_frontRight.set(0.3);
              m_backLeft.set(0.3);
              m_backRight.set(0.3);
             }
             else if(time-startTime >=8.5 && time-startTime< 9.5){
              m_frontLeft.set(0);
              m_frontRight.set(0);
              m_backLeft.set(0);
              m_backRight.set(0);
              servo1.set(90);
              servo2.set(90);
             }
            }
  // This function is called periodically during operator control.
  @Override
  public void teleopPeriodic() {
    xValue xylophone = new xValue(gStick.getX());
    yValue yylophone = new yValue(gStick.getY());
    zValue zylophone = new zValue(gStick.getZ());
    //wValue wylophone = new wValue(gStick.getRawAxis(3));
    //Fricker Shaquille = new Fricker(gStick.getThrottle());

  

    mecaTibbs.arcadeDrive(yylophone.yJoy(), zylophone.zJoy(), false); //sets driving to run using  //joystick controls
                                                                                   //joystick controls
                                                                                        

    if(gStick.getRawButton(5) == true){//This is for the intake
        sucker.set(-1.0);
    }
    else if(gStick.getRawButton(3) == true){
       sucker.set(1.0);
    }
    else{
      sucker.set(0.0);
    }

    if(gStick.getRawButton(1) == true){//This is for the cone flipper
      hammerTime.set(-0.5);
      }
    else if(gStick.getRawButton(2) == true){
       hammerTime.set(0.5);
    }
    else{
      hammerTime.set(0.0);
    }
    if(gStick.getRawButton(12)){ //Carefully lowers the cone flipper
      hammerTime.set(-0.2);
    }
    if(gStick.getRawButton(6) == true){//Elevator
      elevatorMusic.set(-0.4);
    }
    else if(gStick.getRawButton(4)== true){
      elevatorMusic.set(0.4);
    }
    else{
        elevatorMusic.set(0.0);
    }
    if(gStick.getRawButton(7)== true){
      limb.set(0.4);
    }
    else{
      limb.set(0);
    }
     if(gStick.getRawButton(8)==true){
      limb.set(-0.4);
     }
     else{
      limb.set(0.0);
     }
     if (gStick.getRawButton(9)==true){
      limb2.set(0.1);
     }
     else{
      limb.set(0.0);
     }
    if(gStick.getRawButton(10)==true){
      limb2.set(-0.1);      
    }
    else{
      limb2.set(0.0);
    }
    
    if(gStick.getRawButton(11)==true) {
      servo1.set(0);
      servo2.set(0);
    }
     if(gStick.getRawButton(12)==true){
      servo1.set(90);
      servo2.set(90);
     }
     /*  try{
        Thread.sleep(500);
      }
      catch (InterruptedException ex){
        Thread.currentThread().interrupted();
      }
      hammerTime.set(-0.5);
      try{
        Thread.sleep(500);
      }
      catch (InterruptedException ex){
        Thread.currentThread().interrupted();
        hammerTime.set(0);
    } */
   /*  if (DomGoth.get()==false){
      output = Math.min(output, 0);
    }
    else if (SubFemboy.get()==false){
      output = Math.max(output, 0);}
    }
    MeganTheeStallion.set((-output));
    DojaCat.set(output/2);
*/




   Timer.delay(0.001);
   
    }  //timer sets up the code to have a 1 millisecond delay to avoid overworking and 
          //over heating the RobotRIO

   // This function is called periodically during test mode.
  @Override
  public void testPeriodic() {
  }


@Override
public void simulationInit(){

}

@Override
public void simulationPeriodic(){
  
}

class yValue{
  public yValue(double y){
    yCal = y;
  }
  public double yJoy(){
    if((yCal <= 0.2) && (yCal >= 0.0)){
      return 0.0;
    }
    else if(yCal > 0.2){
      return -yCal;
    }
    else if((yCal >= -0.2) && (yCal <= 0.0)){
      return 0.0;
    }
    else if(yCal < -0.2){
      return -yCal;
    }
    else{
      return 0.0;
    }
  }
  public double yCal;
}

class xValue{
  public xValue(double x){
    xCal = x;
  }
public double xJoy(){
  if((xCal <= 0.2) && (xCal >= 0.0)){
    return 0.0;
  }
  else if(xCal > 0.2){
    return xCal;
  }
  else if((xCal >= -0.2) && (xCal <= 0.0)){
    return 0.0;
  }
  else if(xCal < -0.2){
    return xCal;
  }
  else{
    return 0.0;
  }
}
public double xCal;
}

class zValue{
  public zValue(double z){
    zCal = z;
  }
public double zJoy(){
  if((zCal <= 0.3) && (zCal >= 0.0)){
    return 0.0;
  }
  else if(zCal > 0.3){
    return (zCal * 0.5);
  }
  else if((zCal >= -0.3) && (zCal <= 0.0)){
    return 0.0;
  }
  else if(zCal < -0.3){
    return (zCal * 0.5);
  }
  else{
    return 0.0;
  }
}
public double zCal;
}

class wValue{
  public wValue(double w){
    wCal = w;
  }
  public double wSlide(){
    return (-wCal+1)/2;

  }
  public double wCal;






}
/* 
class Fricker{
  public Fricker(double f){
    fCal = f;
  }
  public double fThot(){
    if((fCal <= 0.4)&& (fCal >= 0.0)){
      return 0.0;
    }
    else if (fCal > 0.4){
      return -fCal;
    }
    else if((fCal >= -0.4) && (fCal <= 0.0)){
      return 0.0;
    }
    else if(fCal < -0.4){
      return -fCal;
    }
    else{
      return 0.0;
    }
  }
public double fCal;
}

*/
}
