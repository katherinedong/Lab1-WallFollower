package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distError;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(200);				// Start robot moving forward
		rightMotor.setSpeed(200);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		//Sensor on left, wall on left
		this.distError = bandCenter - distance;
		if (Math.abs(distError)<=bandwidth) {
			leftMotor.setSpeed(200);
			rightMotor.setSpeed(200);
			leftMotor.forward();
			rightMotor.forward();
		}
		else if (this.distance<bandCenter) { 				//too close
			leftMotor.setSpeed(200);
			rightMotor.setSpeed(50);
			leftMotor.forward();
			rightMotor.backward();
		}
		else if (this.distance>bandCenter) {					 //too far
			leftMotor.setSpeed(30);
			rightMotor.setSpeed(130);
			leftMotor.forward();
			rightMotor.forward();
		}
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
