package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	private int distError;
	private double correction;
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		this.distError = 0;
		this.correction = 1.0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		//
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		// TODO: process a movement based on the us distance passed in (P style)
		
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in
		// (BANG-BANG style)
		// Sensor on left, wall on left
		this.distError = bandCenter - distance;
	
		if (Math.abs(distError) <= bandwidth) {
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		} else if (this.distance < bandCenter) { // too close
			correction= 1.0 + distError/40.0;
			leftMotor.setSpeed((int) (correction*motorStraight));
			rightMotor.setSpeed(30);
			leftMotor.forward();
			rightMotor.backward();
		} else if (this.distance > bandCenter) { // too far
			correction= 1.0 + distance/250.0;
			if(correction>2.0){
			correction = 1.5;
			}
			leftMotor.setSpeed(100);
			rightMotor.setSpeed((int) (correction*motorStraight));
		
			leftMotor.forward();
			rightMotor.forward();
		}

	}
	

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
